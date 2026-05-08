package com.tnong.boot.common.util;

import com.tnong.tnongboot.TnongBootApplication;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JWT工具类测试
 */
@SpringBootTest(classes = TnongBootApplication.class)
@DisplayName("JWT工具类测试")
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // 确保JwtUtil已初始化
        assertNotNull(jwtUtil);
    }

    @Test
    @DisplayName("生成Token - 成功")
    void testGenerateToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long tenantId = 100L;

        String token = JwtUtil.generateToken(userId, username, tenantId);

        assertNotNull(token);
        assertTrue(token.length() > 0);
        assertTrue(token.split("\\.").length == 3, "JWT应该包含3个部分");
    }

    @Test
    @DisplayName("解析Token - 成功")
    void testParseToken_Success() {
        Long userId = 1L;
        String username = "testuser";
        Long tenantId = 100L;

        String token = JwtUtil.generateToken(userId, username, tenantId);
        Claims claims = JwtUtil.parseToken(token);

        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
        assertEquals(tenantId, claims.get("tenantId", Long.class));
    }

    @Test
    @DisplayName("验证Token - 有效Token")
    void testValidateToken_ValidToken() {
        String token = JwtUtil.generateToken(1L, "testuser", 100L);

        assertTrue(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("验证Token - 无效Token")
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        assertFalse(JwtUtil.validateToken(invalidToken));
    }

    @Test
    @DisplayName("获取用户ID - 成功")
    void testGetUserId_Success() {
        Long userId = 12345L;
        String token = JwtUtil.generateToken(userId, "testuser", 100L);

        Long extractedUserId = JwtUtil.getUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("获取租户ID - 成功")
    void testGetTenantId_Success() {
        Long tenantId = 999L;
        String token = JwtUtil.generateToken(1L, "testuser", tenantId);

        Long extractedTenantId = JwtUtil.getTenantId(token);

        assertEquals(tenantId, extractedTenantId);
    }

    @Test
    @DisplayName("获取用户名 - 成功")
    void testGetUsername_Success() {
        String username = "john.doe";
        String token = JwtUtil.generateToken(1L, username, 100L);

        String extractedUsername = JwtUtil.getUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Token包含过期时间")
    void testToken_ContainsExpiration() {
        String token = JwtUtil.generateToken(1L, "testuser", 100L);
        Claims claims = JwtUtil.parseToken(token);

        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().getTime() > System.currentTimeMillis());
    }

    @Test
    @DisplayName("Token包含签发时间")
    void testToken_ContainsIssuedAt() {
        String token = JwtUtil.generateToken(1L, "testuser", 100L);
        Claims claims = JwtUtil.parseToken(token);

        assertNotNull(claims.getIssuedAt());
        assertTrue(claims.getIssuedAt().getTime() <= System.currentTimeMillis());
    }
}
