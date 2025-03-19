package org.quyetnt.finalprojecttdd.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quyetnt.finalprojecttdd.dto.CourseDTO;
import org.quyetnt.finalprojecttdd.payload.request.CourseRegistrationRequest;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;

import org.quyetnt.finalprojecttdd.service.RegistrationService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Tắt security filters cho mục đích test endpoint
public class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegistrationController registrationController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void registerCourse_WithValidData_ShouldReturnSuccessResponse() throws Exception {
        CourseRegistrationRequest request = new CourseRegistrationRequest(1L, "test@example.com");
        List<CourseDTO> courseDTOList = new ArrayList<>();
        ResponseObject<List<CourseDTO>> response = new ResponseObject<>("success", "Đăng ký khóa học thành công", courseDTOList);

        when(registrationService.registerCourse(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/courses/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Đăng ký khóa học thành công"));
    }

    @Test
    void registerCourse_WithInvalidData_ShouldReturnErrorResponse() throws Exception {
        CourseRegistrationRequest request = new CourseRegistrationRequest(99L, "test@example.com");
        ResponseObject<List<CourseDTO>> response = new ResponseObject<>("error", "Không tìm thấy khóa học với ID đã cung cấp", null);

        when(registrationService.registerCourse(anyString(), anyLong())).thenReturn(response);

        mockMvc.perform(post("/api/courses/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Không tìm thấy khóa học với ID đã cung cấp"));
    }

    @Test
    void unregisterCourse_WithValidData_ShouldReturnSuccessResponse() throws Exception {
        ResponseObject<?> response = new ResponseObject<>("success", "Hủy đăng ký khóa học thành công", null);

        when(registrationService.unregisterCourse(anyLong(), anyString())).thenReturn((ResponseObject<String>) response);

        mockMvc.perform(delete("/api/courses/unregister/1/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Hủy đăng ký khóa học thành công"));
    }

    @Test
    void unregisterCourse_WithInvalidData_ShouldReturnErrorResponse() throws Exception {
        ResponseObject<?> response = new ResponseObject<>("error", "Học viên chưa đăng ký khóa học này", null);

        when(registrationService.unregisterCourse(anyLong(), anyString())).thenReturn((ResponseObject<String>) response);

        mockMvc.perform(delete("/api/courses/unregister/1/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Học viên chưa đăng ký khóa học này"));
    }
}
