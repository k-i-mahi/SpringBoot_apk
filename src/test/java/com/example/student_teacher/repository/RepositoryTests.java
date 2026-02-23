package com.example.student_teacher.repository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;

@DataJpaTest
class RepositoryTests {

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private CourseRepository courseRepo;

    private Teacher teacher;
    private Student student;

    @BeforeEach
    void setUp() {
        courseRepo.deleteAll();
        studentRepo.deleteAll();
        teacherRepo.deleteAll();

        teacher = new Teacher();
        teacher.setName("Repo Teacher");
        teacher.setEmail("repo.teacher@test.com");
        teacher.setPassword("pass");
        teacher = teacherRepo.save(teacher);

        student = new Student();
        student.setName("Repo Student");
        student.setEmail("repo.student@test.com");
        student.setPassword("pass");
        student = studentRepo.save(student);
    }

    // --- StudentRepository ---

    @Test
    void findStudentByEmail_Found() {
        Optional<Student> found = studentRepo.findByEmail("repo.student@test.com");
        assertTrue(found.isPresent());
        assertEquals("Repo Student", found.get().getName());
    }

    @Test
    void findStudentByEmail_NotFound() {
        Optional<Student> found = studentRepo.findByEmail("nonexistent@test.com");
        assertFalse(found.isPresent());
    }

    // --- TeacherRepository ---

    @Test
    void findTeacherByEmail_Found() {
        Optional<Teacher> found = teacherRepo.findByEmail("repo.teacher@test.com");
        assertTrue(found.isPresent());
        assertEquals("Repo Teacher", found.get().getName());
    }

    @Test
    void findTeacherByEmail_NotFound() {
        Optional<Teacher> found = teacherRepo.findByEmail("ghost@test.com");
        assertFalse(found.isPresent());
    }

    // --- CourseRepository ---

    @Test
    void findCoursesByTeacher_ReturnsCourses() {
        Course c1 = new Course();
        c1.setTitle("Course A");
        c1.setCredit(3);
        c1.setTeacher(teacher);
        courseRepo.save(c1);

        Course c2 = new Course();
        c2.setTitle("Course B");
        c2.setCredit(4);
        c2.setTeacher(teacher);
        courseRepo.save(c2);

        List<Course> courses = courseRepo.findByTeacher(teacher);
        assertEquals(2, courses.size());
    }

    @Test
    void findCoursesByTeacher_EmptyForOtherTeacher() {
        Teacher other = new Teacher();
        other.setName("Other");
        other.setEmail("other@test.com");
        other.setPassword("pass");
        other = teacherRepo.save(other);

        Course c = new Course();
        c.setTitle("Course X");
        c.setCredit(3);
        c.setTeacher(teacher);
        courseRepo.save(c);

        List<Course> courses = courseRepo.findByTeacher(other);
        assertEquals(0, courses.size());
    }

    // --- Student-Course enrollment ---

    @Test
    void studentEnrollment_PersistsCorrectly() {
        Course c = new Course();
        c.setTitle("Enroll Course");
        c.setCredit(3);
        c.setTeacher(teacher);
        c = courseRepo.save(c);

        student.getCourses().add(c);
        studentRepo.save(student);

        Student fetched = studentRepo.findByEmail("repo.student@test.com").get();
        assertEquals(1, fetched.getCourses().size());
        assertTrue(fetched.getCourses().stream().anyMatch(co -> co.getTitle().equals("Enroll Course")));
    }

    @Test
    void studentDropCourse_RemovesEnrollment() {
        Course c = new Course();
        c.setTitle("Drop Course");
        c.setCredit(3);
        c.setTeacher(teacher);
        c = courseRepo.save(c);

        student.getCourses().add(c);
        studentRepo.save(student);

        // Drop
        Student fetched = studentRepo.findByEmail("repo.student@test.com").get();
        fetched.getCourses().clear();
        studentRepo.save(fetched);

        Student afterDrop = studentRepo.findByEmail("repo.student@test.com").get();
        assertEquals(0, afterDrop.getCourses().size());
    }
}
