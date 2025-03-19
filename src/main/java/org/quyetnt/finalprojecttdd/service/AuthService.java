package org.quyetnt.finalprojecttdd.service;

import lombok.RequiredArgsConstructor;
import org.quyetnt.finalprojecttdd.model.ERole;
import org.quyetnt.finalprojecttdd.model.Role;
import org.quyetnt.finalprojecttdd.model.User;
import org.quyetnt.finalprojecttdd.payload.request.LoginRequest;
import org.quyetnt.finalprojecttdd.payload.request.SignupRequest;
import org.quyetnt.finalprojecttdd.payload.response.JwtResponse;
import org.quyetnt.finalprojecttdd.repository.RoleRepository;
import org.quyetnt.finalprojecttdd.repository.UserRepository;
import org.quyetnt.finalprojecttdd.security.jwt.JwtUtils;
import org.quyetnt.finalprojecttdd.security.service.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public boolean registerUser(SignupRequest signupRequest) {
        // Kiểm tra xem username hoặc email đã tồn tại chưa
        if (userRepository.existsByUsername(signupRequest.getUsername()) ||
                userRepository.existsByEmail(signupRequest.getEmail())) {
            return false; // Tên người dùng hoặc email đã tồn tại
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        // Tạo đối tượng User mới
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(encodedPassword)
                .build();

        // Xử lý các roles từ request
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Nếu không có roles thì mặc định gán role USER
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // Nếu có roles, tìm role trong database và gán cho người dùng
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        // Tìm Role ADMIN trong cơ sở dữ liệu
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        // Nếu role không phải "admin", mặc định là USER
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        // Gán roles cho người dùng và lưu vào cơ sở dữ liệu
        user.setRoles(roles);
        userRepository.save(user);

        return true; // Đăng ký thành công
    }


    public JwtResponse login(LoginRequest loginRequest) throws BadCredentialsException {
        // Xác thực thông tin đăng nhập
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getUsername(), loginRequest.getPassword())
                );

        // Lưu thông tin vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tạo JWT token
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Lấy thông tin người dùng đã xác thực
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Trả về JwtResponse
        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList()));
        return jwtResponse;
    }
}
