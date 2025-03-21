package org.quyetnt.finalprojecttdd.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quyetnt.finalprojecttdd.model.ERole;
import org.quyetnt.finalprojecttdd.model.Role;
import org.quyetnt.finalprojecttdd.model.User;
import org.quyetnt.finalprojecttdd.payload.request.LoginRequest;
import org.quyetnt.finalprojecttdd.payload.request.SignupRequest;
import org.quyetnt.finalprojecttdd.payload.response.JwtResponse;
import org.quyetnt.finalprojecttdd.repository.RoleRepository;
import org.quyetnt.finalprojecttdd.repository.UserRepository;
import org.quyetnt.finalprojecttdd.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private SignupRequest validSignupRequest;
    private Role userRole;

    @Mock
    private UserDetailsService userDetailsService;

    private LoginRequest validLoginRequest;
    private UserDetails mockUserDetails;
    private String jwtToken;


    @BeforeEach
    void setUp() {
        validSignupRequest = SignupRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .build();

        userRole = new Role(ERole.ROLE_USER);
        userRole.setId(1L);

        validLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();

        mockUserDetails = org.springframework.security.core.userdetails.User
                .builder()
                .username("testuser")
                .password("password123")
                .authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_USER"))
                .build();
        jwtToken = "mockJwtToken";
    }

    @Test
    void registerUser_WithValidData_ShouldReturnSuccess() {
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validSignupRequest.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = authService.registerUser(validSignupRequest);
        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnFalse() {
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(true);
        boolean result = authService.registerUser(validSignupRequest);
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldReturnFalse() {
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(true);

        boolean result = authService.registerUser(validSignupRequest);
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithRoleAdmin_ShouldReturnSuccess() {
        when(userRepository.existsByUsername(validSignupRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(validSignupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(validSignupRequest.getPassword())).thenReturn("encodedPassword");
        Role adminRole = new Role(ERole.ROLE_ADMIN);
        adminRole.setId(2L);
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        validSignupRequest.setRoles(Set.of("admin"));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        boolean result = authService.registerUser(validSignupRequest);
        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signin_WithInvalidCredentials_ShouldThrowException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));
        try {
            authService.login(validLoginRequest);
        } catch (RuntimeException ex) {
            assertEquals("Invalid credentials", ex.getMessage());
        }

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

}
