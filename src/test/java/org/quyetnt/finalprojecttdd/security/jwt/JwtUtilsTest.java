package org.quyetnt.finalprojecttdd.security.jwt;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        // Sử dụng ReflectionTestUtils để thiết lập giá trị cho các field @Value của JwtUtils
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "mysecretkeywhichneedstobeverylong123456789012345678901234567890");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L);
    }

    @Test
    void generateAndValidateToken_ShouldWorkCorrectly() {
        // Tạo đối tượng Authentication dummy để test
        var authentication = new DummyAuthentication("testuser");
        String token = jwtUtils.generateJwtToken(authentication);
        assertNotNull(token, "Token không được null");
        assertTrue(jwtUtils.validateJwtToken(token), "Token phải hợp lệ");

        // Lấy username từ token và so sánh
        String username = jwtUtils.getUserNameFromJwtToken(token);
        assertEquals("testuser", username);
    }

    // Lớp dummy để mô phỏng Authentication
    static class DummyAuthentication implements org.springframework.security.core.Authentication {
        private final String username;
        public DummyAuthentication(String username) {
            this.username = username;
        }
        @Override public String getName() { return username; }
        @Override public Object getCredentials() { return null; }
        @Override public Object getDetails() { return null; }
        @Override public Object getPrincipal() {
            // Sử dụng đối tượng UserDetails của Spring Security
            return new org.springframework.security.core.userdetails.User(username, "", java.util.Collections.emptyList());
        }
        @Override public boolean isAuthenticated() { return true; }
        @Override public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException { }
        @Override public java.util.Collection<org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return java.util.Collections.emptyList();
        }
    }
}
