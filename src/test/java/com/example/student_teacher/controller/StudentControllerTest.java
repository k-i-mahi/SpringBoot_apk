package com.example.student_teacher.controller;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentRepository studentRepo;

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private Principal principal;

    @InjectMocks
    private StudentController controller;

    private Student student;
    private Course course;
    private Teacher teacher;

    private void setId(Object entity, Long id) throws Exception {
        Field field = entity.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    @BeforeEach
    void setUp() throws Exception {
        teacher = new Teacher();
        teacher.setName("Teacher");
        teacher.setEmail("teacher@test.com");

        course = new Course();
        setId(course, 1L);
        course.setTitle("Test Course");
        course.setCredit(3);
        course.setTeacher(teacher);

        student = new Student();
        setId(student, 1L);
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setPassword("password");
    }

    @Test
    void takeCourse_Success() {
        when(principal.getName()).thenReturn("student@test.com");
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(courseRepo.findById(1L)).thenReturn(Optional.of(course));
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        String result = controller.takeCourse(1L, principal);

        assertEquals("Course taken", result);
        assertTrue(student.getCourses().contains(course));
        verify(studentRepo).save(student);
    }

    @Test
    void removeCourse_Success() {
        // Add course first, then remove it
        student.getCourses().add(course);
        when(principal.getName()).thenReturn("student@test.com");
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        String result = controller.removeCourse(course.getId(), principal);

        assertEquals("Course removed", result);
        verify(studentRepo).save(student);
    }
}
