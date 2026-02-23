package com.example.student_teacher.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import org.springframework.transaction.annotation.Transactional;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FullFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Teacher teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        // Clear enrollments first (student_courses join table) by clearing each student's courses
        for (Student s : studentRepo.findAll()) {
            s.getCourses().clear();
            studentRepo.save(s);
        }
        // Then delete in proper FK order
        courseRepo.deleteAll();
        studentRepo.deleteAll();
        teacherRepo.deleteAll();

        // Seed a teacher
        teacher = new Teacher();
        teacher.setName("Integration Teacher");
        teacher.setEmail("iteacher@test.com");
        teacher.setPassword(passwordEncoder.encode("password"));
        teacher = teacherRepo.save(teacher);

        // Seed a student
        student = new Student();
        student.setName("Integration Student");
        student.setEmail("istudent@test.com");
        student.setPassword(passwordEncoder.encode("password"));
        student = studentRepo.save(student);

        // Seed a course
        course = new Course();
        course.setTitle("Integration Course");
        course.setCredit(3);
        course.setTeacher(teacher);
        course = courseRepo.save(course);
    }

    // -------- Authentication & Authorization --------

    @Test
    void unauthenticatedRequest_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginPage_IsAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void studentRole_CannotAccessTeacherEndpoints() throws Exception {
        mockMvc.perform(get("/teacher/dashboard")
                        .with(user("istudent@test.com").roles("STUDENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void teacherRole_CannotAccessStudentEndpoints() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("iteacher@test.com").roles("TEACHER")))
                .andExpect(status().isForbidden());
    }

    // -------- Dashboard routing --------

    @Test
    void studentDashboard_RendersCorrectly() throws Exception {
        mockMvc.perform(get("/student/dashboard")
                        .with(user("istudent@test.com").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(view().name("student-dashboard"))
                .andExpect(model().attributeExists("student", "allCourses"));
    }

    @Test
    void teacherDashboard_RendersCorrectly() throws Exception {
        mockMvc.perform(get("/teacher/dashboard")
                        .with(user("iteacher@test.com").roles("TEACHER")))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher-dashboard"))
                .andExpect(model().attributeExists("teacher", "myCourses"));
    }

    @Test
    void dashboard_StudentRole_RedirectsToStudentDashboard() throws Exception {
        mockMvc.perform(get("/dashboard")
                        .with(user("istudent@test.com").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    void dashboard_TeacherRole_RedirectsToTeacherDashboard() throws Exception {
        mockMvc.perform(get("/dashboard")
                        .with(user("iteacher@test.com").roles("TEACHER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    // -------- Student course enrollment flow --------

    @Test
    void student_EnrollInCourse_Success() throws Exception {
        mockMvc.perform(post("/student/courses/enroll/" + course.getId())
                        .with(user("istudent@test.com").roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));

        // Verify enrollment persisted
        Student updated = studentRepo.findByEmail("istudent@test.com").get();
        assertTrue(updated.getCourses().stream()
                .anyMatch(c -> c.getId().equals(course.getId())));
    }

    @Test
    void student_DropCourse_Success() throws Exception {
        // First enroll
        student.getCourses().add(course);
        studentRepo.save(student);

        // Then drop
        mockMvc.perform(post("/student/courses/drop/" + course.getId())
                        .with(user("istudent@test.com").roles("STUDENT"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));

        Student updated = studentRepo.findByEmail("istudent@test.com").get();
        assertFalse(updated.getCourses().stream()
                .anyMatch(c -> c.getId().equals(course.getId())));
    }

    // -------- Teacher course management flow --------

    @Test
    void teacher_AddCourse_Success() throws Exception {
        mockMvc.perform(post("/teacher/courses/add")
                        .with(user("iteacher@test.com").roles("TEACHER"))
                        .with(csrf())
                        .param("title", "New Integration Course")
                        .param("credit", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));

        // Verify course persisted
        assertTrue(courseRepo.findAll().stream()
                .anyMatch(c -> c.getTitle().equals("New Integration Course")));
    }

    @Test
    void teacher_DeleteCourse_Success() throws Exception {
        mockMvc.perform(post("/teacher/courses/delete/" + course.getId())
                        .with(user("iteacher@test.com").roles("TEACHER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));

        assertFalse(courseRepo.findById(course.getId()).isPresent());
    }

    // -------- Data integrity tests --------

    @Test
    void deleteCourse_RemovesFromEnrolledStudents() throws Exception {
        // Enroll student first
        student.getCourses().add(course);
        studentRepo.save(student);

        // Teacher deletes the course
        mockMvc.perform(post("/teacher/courses/delete/" + course.getId())
                        .with(user("iteacher@test.com").roles("TEACHER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Verify course removed from student enrollments
        Student updated = studentRepo.findByEmail("istudent@test.com").get();
        assertFalse(updated.getCourses().stream()
                .anyMatch(c -> c.getId().equals(course.getId())));
    }

    @Test
    void teacherCannotDeleteOtherTeachersCourse() throws Exception {
        // Create another teacher
        Teacher otherTeacher = new Teacher();
        otherTeacher.setName("Other Teacher");
        otherTeacher.setEmail("other@test.com");
        otherTeacher.setPassword(passwordEncoder.encode("password"));
        teacherRepo.save(otherTeacher);

        // Try to delete course belonging to first teacher
        mockMvc.perform(post("/teacher/courses/delete/" + course.getId())
                        .with(user("other@test.com").roles("TEACHER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Course should still exist
        assertTrue(courseRepo.findById(course.getId()).isPresent());
    }
}
