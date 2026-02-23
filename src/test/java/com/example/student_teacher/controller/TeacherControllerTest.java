package com.example.student_teacher.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.student_teacher.entity.Course;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.CourseRepository;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
class TeacherControllerTest {

    @Mock
    private CourseRepository courseRepo;

    @Mock
    private TeacherRepository teacherRepo;

    @Mock
    private StudentRepository studentRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    @InjectMocks
    private TeacherController controller;

    private Teacher teacher;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setPassword("encodedPassword");

        student = new Student();
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setPassword("encodedPassword");

        course = new Course();
        course.setTitle("Test Course");
        course.setCredit(3);
        course.setTeacher(teacher);
    }

    @Test
    void addCourse_Success() {
        when(principal.getName()).thenReturn("teacher@test.com");
        when(teacherRepo.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));
        when(courseRepo.save(any(Course.class))).thenReturn(course);

        Course newCourse = new Course();
        newCourse.setTitle("New Course");
        newCourse.setCredit(4);

        Course result = controller.addCourse(newCourse, principal);

        assertNotNull(result);
        verify(courseRepo).save(newCourse);
    }

    @Test
    void getAllStudents_ReturnsList() {
        List<Student> students = Arrays.asList(student);
        when(studentRepo.findAll()).thenReturn(students);

        List<Student> result = controller.getAllStudents();

        assertEquals(1, result.size());
        assertEquals("Test Student", result.get(0).getName());
    }

    @Test
    void addStudent_Success() {
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(studentRepo.save(any(Student.class))).thenReturn(student);

        Student newStudent = new Student();
        newStudent.setEmail("student@test.com");
        newStudent.setPassword("rawPassword");

        ResponseEntity<?> response = controller.addStudent(newStudent);

        assertEquals(200, response.getStatusCode().value());
        verify(passwordEncoder).encode("rawPassword");
    }

    @Test
    void addStudent_DuplicateEmail_ReturnsBadRequest() {
        when(studentRepo.findByEmail("student@test.com")).thenReturn(Optional.of(student));

        Student newStudent = new Student();
        newStudent.setEmail("student@test.com");
        newStudent.setPassword("password");

        ResponseEntity<?> response = controller.addStudent(newStudent);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Email already exists", response.getBody());
    }

    @Test
    void deleteStudent_Success() {
        when(studentRepo.existsById(1L)).thenReturn(true);

        ResponseEntity<?> response = controller.deleteStudent(1L);

        assertEquals(200, response.getStatusCode().value());
        verify(studentRepo).deleteById(1L);
    }

    @Test
    void deleteStudent_NotFound_ReturnsBadRequest() {
        when(studentRepo.existsById(99L)).thenReturn(false);

        ResponseEntity<?> response = controller.deleteStudent(99L);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Student not found", response.getBody());
    }
}