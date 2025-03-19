package org.quyetnt.finalprojecttdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quyetnt.finalprojecttdd.dto.CourseDTO;
import org.quyetnt.finalprojecttdd.model.Course;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;
import org.quyetnt.finalprojecttdd.service.CourseService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController courseController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void getAllUncompletedCourses_WithValidData_ShouldReturnSuccessResponse() throws Exception {
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(new Course());
        mockCourses.add(new Course());

        List<CourseDTO> mockCourseDTOs = new ArrayList<>();
        CourseDTO courseDTO1 = new CourseDTO();
        courseDTO1.setId(1L);
        courseDTO1.setName("Java Programming");

        CourseDTO courseDTO2 = new CourseDTO();
        courseDTO2.setId(2L);
        courseDTO2.setName("Spring Boot");

        mockCourseDTOs.add(courseDTO1);
        mockCourseDTOs.add(courseDTO2);

        when(courseService.findAllUncompletedCourses()).thenReturn(mockCourses);
        when(courseService.convertToDTOList(mockCourses)).thenReturn(mockCourseDTOs);

        mockMvc.perform(get("/api/courses/uncompleted")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Lấy danh sách khóa học chưa kết thúc thành công")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].name", is("Java Programming")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].name", is("Spring Boot")));

        verify(courseService, times(1)).findAllUncompletedCourses();
        verify(courseService, times(1)).convertToDTOList(mockCourses);
    }

    @Test
    public void getAllCoursesNotStarted_WithValidData_ShouldReturnSuccessResponse() throws Exception {
        List<Course> mockCourses = new ArrayList<>();
        mockCourses.add(new Course());

        List<CourseDTO> mockCourseDTOs = new ArrayList<>();
        CourseDTO courseDTO = new CourseDTO();
        courseDTO.setId(3L);
        courseDTO.setName("Python Basics");
        mockCourseDTOs.add(courseDTO);

        when(courseService.findCoursesNotStartedYet()).thenReturn(mockCourses);
        when(courseService.convertToDTOList(mockCourses)).thenReturn(mockCourseDTOs);

        mockMvc.perform(get("/api/courses/not-started")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Lấy danh sách khóa học chưa bắt đầu thành công")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].name", is("Python Basics")));

        verify(courseService, times(1)).findCoursesNotStartedYet();
        verify(courseService, times(1)).convertToDTOList(mockCourses);
    }

    @Test
    public void getCourseById_WithExistingId_ShouldReturnSuccessResponse() throws Exception {
        Long courseId = 1L;
        Course mockCourse = new Course();

        CourseDTO mockCourseDTO = new CourseDTO();
        mockCourseDTO.setId(courseId);
        mockCourseDTO.setName("Java Programming");
        mockCourseDTO.setDescription("Learn Java from scratch");

        when(courseService.findCourseById(courseId)).thenReturn(mockCourse);
        when(courseService.convertToDTO(mockCourse)).thenReturn(mockCourseDTO);

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("Lấy thông tin khóa học thành công")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Java Programming")))
                .andExpect(jsonPath("$.data.description", is("Learn Java from scratch")));

        verify(courseService, times(1)).findCourseById(courseId);
        verify(courseService, times(1)).convertToDTO(mockCourse);
    }

    @Test
    public void getCourseById_WithNonExistingId_ShouldReturnErrorResponse() throws Exception {
        Long courseId = 999L;
        String errorMessage = "Không tìm thấy khóa học với ID: " + courseId;

        when(courseService.findCourseById(courseId)).thenThrow(new RuntimeException(errorMessage));

        mockMvc.perform(get("/api/courses/{id}", courseId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Note: Trong controller vẫn trả về status code 200 dù có lỗi
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", is(errorMessage)))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(courseService, times(1)).findCourseById(courseId);
        verify(courseService, never()).convertToDTO(any(Course.class));
    }
}