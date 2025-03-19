package org.quyetnt.finalprojecttdd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quyetnt.finalprojecttdd.dto.CourseDTO;

import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.model.Registration;
import org.quyetnt.finalprojecttdd.model.RegistrationId;
import org.quyetnt.finalprojecttdd.model.Student;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;
import org.quyetnt.finalprojecttdd.repository.CourseRepository;
import org.quyetnt.finalprojecttdd.repository.RegistrationRepository;
import org.quyetnt.finalprojecttdd.repository.StudentRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private RegistrationService registrationService;

    private Student student;
    private Course course1;
    private Course course2;
    private Registration registration1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo dữ liệu mẫu
        student = new Student();
        student.setId(1L);
        student.setEmail("test@example.com");
        student.setFirstName("John");
        student.setLastName("Doe");

        course1 = new Course();
        course1.setId(1L);
        course1.setName("Java Programming");
        course1.setDescription("Learn Java Programming");
        course1.setStartTime(LocalDateTime.now().plusDays(5));
        course1.setEndTime(LocalDateTime.now().plusDays(30));
        course1.setPrice(BigInteger.valueOf(1000000));

        course2 = new Course();
        course2.setId(2L);
        course2.setName("Spring Boot");
        course2.setDescription("Learn Spring Boot");
        course2.setStartTime(LocalDateTime.now().plusDays(1));
        course2.setEndTime(LocalDateTime.now().plusDays(20));
        course2.setPrice(BigInteger.valueOf(1500000));

        registration1 = new Registration();
        RegistrationId r1 = new RegistrationId();
        r1.setCourseId(course1.getId());
        r1.setStudentId(student.getId());
        registration1.setId(r1);
        registration1.setStudent(student);
        registration1.setCourse(course1);
        registration1.setPrice(BigInteger.valueOf(1000000));
        registration1.setRegisteredDate(LocalDateTime.now());
    }

    @Test
    void registerCourse_WithValidStudentAndCourse_ShouldRegisterSuccessfully() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(registrationRepository.findByStudentAndCourse(student, course1)).thenReturn(Optional.empty());
        when(registrationRepository.countByStudent(student)).thenReturn(0);
        when(courseService.findAllUncompletedCourses()).thenReturn(Arrays.asList(course1, course2));

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("test@example.com", 1L);

        // Then
        assertEquals("success", response.getStatus());
        assertEquals("Đăng ký khóa học thành công", response.getMessage());
        assertEquals(2, response.getData().size());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void registerCourse_WithNonexistentStudent_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("nonexistent@example.com", 1L);

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Không tìm thấy học viên với email đã cung cấp", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void registerCourse_WithNonexistentCourse_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("test@example.com", 99L);

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Không tìm thấy khóa học với ID đã cung cấp", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void registerCourse_WithAlreadyRegisteredCourse_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(registrationRepository.findByStudentAndCourse(student, course1)).thenReturn(Optional.of(registration1));

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("test@example.com", 1L);

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Học viên đã đăng ký khóa học này", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void registerCourse_WithStartedCourse_ShouldReturnError() {
        // Given
        Course startedCourse = new Course();
        startedCourse.setId(3L);
        startedCourse.setName("Python");
        startedCourse.setStartTime(LocalDateTime.now().minusDays(1));
        startedCourse.setEndTime(LocalDateTime.now().plusDays(10));

        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(3L)).thenReturn(Optional.of(startedCourse));
        when(registrationRepository.findByStudentAndCourse(student, startedCourse)).thenReturn(Optional.empty());

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("test@example.com", 3L);

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Không thể đăng ký khóa học đã bắt đầu", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void registerCourse_WithDiscountForThirdCourse_ShouldApplyDiscount() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course2));
        when(registrationRepository.findByStudentAndCourse(student, course2)).thenReturn(Optional.empty());
        when(registrationRepository.countByStudent(student)).thenReturn(2); // Đã đăng ký 2 khóa học
        when(courseService.findAllUncompletedCourses()).thenReturn(Arrays.asList(course1, course2));

        // When
        ResponseObject<List<CourseDTO>> response = registrationService.registerCourse("test@example.com", 2L);

        // Then
        assertEquals("success", response.getStatus());
        assertEquals("Đăng ký khóa học thành công với giảm giá 25%", response.getMessage());
        assertEquals(2, response.getData().size());
        verify(registrationRepository, times(1)).save(any(Registration.class));
    }

    @Test
    void unregisterCourse_WithValidStudentAndCourse_ShouldUnregisterSuccessfully() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        when(registrationRepository.findByStudentAndCourse(student, course1)).thenReturn(Optional.of(registration1));

        // When
        ResponseObject<?> response = registrationService.unregisterCourse(1L, "test@example.com");

        // Then
        assertEquals("success", response.getStatus());
        assertEquals("Hủy đăng ký khóa học thành công", response.getMessage());
        verify(registrationRepository, times(1)).delete(registration1);
    }

    @Test
    void unregisterCourse_WithNonexistentStudent_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        ResponseObject<?> response = registrationService.unregisterCourse(1L, "nonexistent@example.com");

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Không tìm thấy học viên với email đã cung cấp", response.getMessage());
    }

    @Test
    void unregisterCourse_WithNonexistentCourse_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        ResponseObject<?> response = registrationService.unregisterCourse(99L, "test@example.com");

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Không tìm thấy khóa học với ID đã cung cấp", response.getMessage());
    }

    @Test
    void unregisterCourse_WithNotRegisteredCourse_ShouldReturnError() {
        // Given
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(student));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(course2));
        when(registrationRepository.findByStudentAndCourse(student, course2)).thenReturn(Optional.empty());

        // When
        ResponseObject<?> response = registrationService.unregisterCourse(2L, "test@example.com");

        // Then
        assertEquals("error", response.getStatus());
        assertEquals("Học viên chưa đăng ký khóa học này", response.getMessage());
    }
}