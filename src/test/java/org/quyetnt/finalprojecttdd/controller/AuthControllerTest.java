package org.quyetnt.finalprojecttdd.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.quyetnt.finalprojecttdd.payload.request.LoginRequest;
import org.quyetnt.finalprojecttdd.payload.request.SignupRequest;
import org.quyetnt.finalprojecttdd.payload.response.JwtResponse;
import org.quyetnt.finalprojecttdd.security.jwt.JwtUtils;
import org.quyetnt.finalprojecttdd.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)  // Tắt security filters cho mục đích test endpoint
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Giả lập các bean của Spring để phục vụ cho test
    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnSuccessResponse() throws Exception {
        // Chuẩn bị request đăng nhập
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        // Giả lập logic login của AuthService
        JwtResponse jwtResponse = JwtResponse.builder()
                .token("fake-jwt-token") // Token giả lập
                .username("testuser")
                .email("test@example.com") // Email giả lập
                .roles(java.util.Collections.singletonList("ROLE_USER"))
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(jwtResponse);

        // Thực hiện request POST /api/auth/signin
        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User authenticated successfully!"))
                .andExpect(jsonPath("$.data.token").value("fake-jwt-token"));
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldReturnErrorResponse() throws Exception {
        // Chuẩn bị request đăng nhập với mật khẩu sai
        LoginRequest loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword")
                .build();

        // Giả lập lỗi xác thực
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void registerUser_WithValidSignupRequest_ShouldReturnSuccessResponse() throws Exception {
        // Chuẩn bị đối tượng SignupRequest hợp lệ
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .roles(Set.of("user"))  // Role mặc định là user
                .build();

        // Giả lập đăng ký thành công
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(true);

        // Thực hiện request POST /api/auth/signup
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())  // Kiểm tra trạng thái HTTP 200
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("User registered successfully!"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void registerUser_WithExistingUsernameOrEmail_ShouldReturnErrorResponse() throws Exception {
        // Chuẩn bị đối tượng SignupRequest hợp lệ với username đã tồn tại
        SignupRequest signupRequest = SignupRequest.builder()
                .username("testuser")
                .email("testuser@example.com")
                .password("password123")
                .roles(Set.of("user"))
                .build();

        // Giả lập username hoặc email đã tồn tại
        when(authService.registerUser(any(SignupRequest.class))).thenReturn(false);

        // Thực hiện request POST /api/auth/signup
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())  // Kiểm tra trạng thái HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("Error: Username or email is already taken!"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
