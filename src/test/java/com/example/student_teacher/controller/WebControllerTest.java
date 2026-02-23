package com.example.student_teacher.controller;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.student_teacher.config.SecurityConfig;
import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import com.example.student_teacher.security.CustomUserDetailsService;

@WebMvcTest(WebController.class)
@Import({SecurityConfig.class, CustomUserDetailsService.class})
class WebControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepo;

    @MockitoBean
    private TeacherRepository teacherRepo;

    @MockitoBean
    private CourseRepository courseRepo;

    private static void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    private Student student;
    private Teacher teacher;
    private Course course;

    @BeforeEach
    void setUp() throws Exception {
        teacher = new Teacher();
        setId(teacher, 1L);
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setPassword("encodedPassword");

        student = new Student();
        setId(student, 1L);
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setPassword("encodedPassword");

        course = new Course();
        setId(course, 1L);
        course.setTitle("Test Course");
        course.setCredit(3);
        course.setTeacher(teacher);
    }

    // --- Login page tests ---

    @Test
    void loginPage_AccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    // --- Dashboard redirect tests ---

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void dashboard_StudentRedirectsToStudentDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void dashboard_TeacherRedirectsToTeacherDashboard() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }

    // --- Student dashboard tests ---

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void studentDashboard_ReturnsView() throws Exception {
        student.getCourses().add(course);
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(courseRepo.findAll()).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("student-dashboard"))
                .andExpect(model().attributeExists("student", "myCourses", "allCourses",
                        "totalCredits", "availableCourses", "enrolledCount"));
    }

    // --- Teacher dashboard tests ---

    @Test
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void teacherDashboard_ReturnsView() throws Exception {
        when(teacherRepo.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));
        when(courseRepo.findByTeacher(teacher)).thenReturn(Arrays.asList(course));
        when(studentRepo.count()).thenReturn(2L);
        when(studentRepo.findAll()).thenReturn(Arrays.asList(student));

        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacher-dashboard"))
                .andExpect(model().attributeExists("teacher", "myCourses",
                        "totalCredits", "totalStudents", "totalEnrollments"));
    }

    // --- Access control tests ---

    @Test
    void unauthenticated_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void student_CannotAccessTeacherDashboard() throws Exception {
        mockMvc.perform(get("/teacher/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void teacher_CannotAccessStudentDashboard() throws Exception {
        mockMvc.perform(get("/student/dashboard"))
                .andExpect(status().isForbidden());
    }

    // --- Course enrollment (POST) tests ---

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void enrollCourse_RedirectsToStudentDashboard() throws Exception {
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(courseRepo.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student/courses/enroll/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    @Test
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void dropCourse_RedirectsToStudentDashboard() throws Exception {
        student.getCourses().add(course);
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student/courses/drop/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/student/dashboard"));
    }

    // --- Teacher course management (POST) tests ---

    @Test
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void addCourse_RedirectsToTeacherDashboard() throws Exception {
        when(teacherRepo.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));
        when(courseRepo.save(any(Course.class))).thenReturn(course);

        mockMvc.perform(post("/teacher/courses/add").with(csrf())
                        .param("title", "New Course")
                        .param("credit", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/dashboard"));
    }
}
