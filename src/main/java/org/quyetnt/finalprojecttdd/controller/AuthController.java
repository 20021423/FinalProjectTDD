package org.quyetnt.finalprojecttdd.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.quyetnt.finalprojecttdd.payload.request.LoginRequest;
import org.quyetnt.finalprojecttdd.payload.request.SignupRequest;
import org.quyetnt.finalprojecttdd.payload.response.JwtResponse;
import org.quyetnt.finalprojecttdd.payload.response.ResponseObject;
import org.quyetnt.finalprojecttdd.security.jwt.JwtUtils;
import org.quyetnt.finalprojecttdd.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (authService.registerUser(signupRequest)) {
            ResponseObject<?> response = new ResponseObject<>("success", "User registered successfully!", null);
            return ResponseEntity.ok(response);
        } else {
            ResponseObject<?> response = new ResponseObject<>("error", "Error: Username or email is already taken!", null);
            return ResponseEntity.badRequest().body(response);
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Gọi AuthService để đăng nhập và lấy thông tin người dùng cùng token
            JwtResponse jwtResponse = authService.login(loginRequest);

            // Trả về response với JwtResponse
            ResponseObject<JwtResponse> response = new ResponseObject<>("success", "User authenticated successfully!", jwtResponse);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            // Nếu thông tin đăng nhập sai
            ResponseObject<?> response = new ResponseObject<>("error", "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
