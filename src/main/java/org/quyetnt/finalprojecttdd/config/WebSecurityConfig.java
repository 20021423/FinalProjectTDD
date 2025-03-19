package org.quyetnt.finalprojecttdd.config;

import org.quyetnt.finalprojecttdd.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    // Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean AuthenticationManager để xác thực người dùng
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Cấu hình SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt CSRF vì là API REST
                .csrf(csrf -> csrf.disable())
                // Cho phép cấu hình CORS (có thể sử dụng mặc định)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Cho phép truy cập không cần xác thực đến /api/auth/** và Swagger
                        .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api/").permitAll()
                        // Các request còn lại yêu cầu phải xác thực
                        .anyRequest().authenticated()
                )
                // Tắt form login và HTTP Basic
                .formLogin(form -> form.disable())
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
