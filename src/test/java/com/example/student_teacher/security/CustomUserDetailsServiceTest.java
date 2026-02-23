package com.example.student_teacher.security;

import com.example.student_teacher.entity.Role;
import com.example.student_teacher.entity.Student;
import com.example.student_teacher.entity.Teacher;
import com.example.student_teacher.repository.StudentRepository;
import com.example.student_teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    private Student student;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setName("Test Student");
        student.setEmail("student@test.com");
        student.setPassword("encodedPassword");

        teacher = new Teacher();
        teacher.setName("Test Teacher");
        teacher.setEmail("teacher@test.com");
        teacher.setPassword("encodedPassword");
    }

    @Test
    void loadUserByUsername_StudentFound_ReturnsStudentDetails() {
        when(studentRepository.findByEmail("student@test.com")).thenReturn(Optional.of(student));

        UserDetails userDetails = service.loadUserByUsername("student@test.com");

        assertEquals("student@test.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
        verify(teacherRepository, never()).findByEmail(anyString());
    }

    @Test
    void loadUserByUsername_TeacherFound_ReturnsTeacherDetails() {
        when(studentRepository.findByEmail("teacher@test.com")).thenReturn(Optional.empty());
        when(teacherRepository.findByEmail("teacher@test.com")).thenReturn(Optional.of(teacher));

        UserDetails userDetails = service.loadUserByUsername("teacher@test.com");

        assertEquals("teacher@test.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER")));
    }

    @Test
    void loadUserByUsername_NotFound_ThrowsException() {
        when(studentRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());
        when(teacherRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown@test.com"));
    }

    @Test
    void loadUserByUsername_StudentTakesPriorityOverTeacher() {
        // If same email exists in both tables, student should be returned
        when(studentRepository.findByEmail("both@test.com")).thenReturn(Optional.of(student));

        UserDetails userDetails = service.loadUserByUsername("both@test.com");

        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT")));
        verify(teacherRepository, never()).findByEmail(anyString());
    }
}
