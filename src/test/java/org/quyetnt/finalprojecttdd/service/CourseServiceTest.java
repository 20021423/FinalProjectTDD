package org.quyetnt.finalprojecttdd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.repository.CourseRepository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Khởi tạo dữ liệu mẫu
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
        course2.setStartTime(LocalDateTime.now().minusDays(5));
        course2.setEndTime(LocalDateTime.now().plusDays(20));
        course2.setPrice(BigInteger.valueOf(1500000));
    }

    @Test
    void findCourseById_WithValidId_ShouldReturnCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course1));
        Course result = courseService.findCourseById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Java Programming", result.getName());
    }

    @Test
    void findCourseById_WithInvalidId_ShouldThrowException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> courseService.findCourseById(99L));
    }

    @Test
    void findAllUncompletedCourses_ShouldReturnUncompletedCourses() {
        when(courseRepository.findByEndTimeAfter(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(course1, course2));

        List<Course> result = courseService.findAllUncompletedCourses();

        assertEquals(2, result.size());
        assertTrue(result.contains(course1));
        assertTrue(result.contains(course2));
    }

    @Test
    void findCoursesNotStartedYet_ShouldReturnCoursesNotStarted() {
        when(courseRepository.findByStartTimeAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(course1));

        List<Course> result = courseService.findCoursesNotStartedYet();

        assertEquals(1, result.size());
        assertEquals(course1, result.get(0));
    }
}
