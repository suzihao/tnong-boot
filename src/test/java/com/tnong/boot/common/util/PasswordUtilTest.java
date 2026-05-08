package com.tnong.boot.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 密码工具类测试
 */
@DisplayName("密码加密工具测试")
class PasswordUtilTest {

    @Test
    @DisplayName("密码加密 - 成功")
    void testEncode_Success() {
        String rawPassword = "123456";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$"));
        assertTrue(encodedPassword.length() > 50);
    }

    @Test
    @DisplayName("密码加密 - 每次生成不同的哈希值")
    void testEncode_DifferentHashEachTime() {
        String rawPassword = "123456";
        String encoded1 = PasswordUtil.encode(rawPassword);
        String encoded2 = PasswordUtil.encode(rawPassword);

        assertNotEquals(encoded1, encoded2, "BCrypt每次应该生成不同的哈希值");
    }

    @Test
    @DisplayName("密码验证 - 正确密码")
    void testMatches_CorrectPassword() {
        String rawPassword = "mySecurePassword123";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        assertTrue(PasswordUtil.matches(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("密码验证 - 错误密码")
    void testMatches_WrongPassword() {
        String rawPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        assertFalse(PasswordUtil.matches(wrongPassword, encodedPassword));
    }

    @Test
    @DisplayName("密码加密 - 空密码抛出异常")
    void testEncode_EmptyPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.encode("");
        });
    }

    @Test
    @DisplayName("密码加密 - null密码抛出异常")
    void testEncode_NullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.encode(null);
        });
    }

    @Test
    @DisplayName("密码验证 - null参数返回false")
    void testMatches_NullParameters() {
        String encodedPassword = PasswordUtil.encode("test");

        assertFalse(PasswordUtil.matches(null, encodedPassword));
        assertFalse(PasswordUtil.matches("test", null));
        assertFalse(PasswordUtil.matches(null, null));
    }

    @Test
    @DisplayName("密码验证 - 无效的编码格式返回false")
    void testMatches_InvalidEncodedFormat() {
        assertFalse(PasswordUtil.matches("password", "invalid-hash-format"));
    }
}
