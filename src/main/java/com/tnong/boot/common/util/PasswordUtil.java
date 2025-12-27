package com.tnong.boot.common.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 * 使用 SHA-256 + Salt
 */
public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    static void main() {
        System.out.println(encode("123456"));
    }

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        try {
            // 生成随机盐
            byte[] salt = generateSalt();
            
            // 密码 + 盐进行哈希
            byte[] hash = hash(rawPassword, salt);
            
            // 将盐和哈希值合并：salt(16字节) + hash(32字节)
            byte[] combined = new byte[salt.length + hash.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hash, 0, combined, salt.length, hash.length);
            
            // Base64 编码
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        try {
            // Base64 解码
            byte[] combined = Base64.getDecoder().decode(encodedPassword);
            
            // 提取盐
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(combined, 0, salt, 0, SALT_LENGTH);
            
            // 提取哈希值
            byte[] storedHash = new byte[combined.length - SALT_LENGTH];
            System.arraycopy(combined, SALT_LENGTH, storedHash, 0, storedHash.length);
            
            // 用相同的盐对输入密码进行哈希
            byte[] computedHash = hash(rawPassword, salt);
            
            // 比较哈希值
            return MessageDigest.isEqual(storedHash, computedHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 生成随机盐
     */
    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * 哈希计算
     */
    private static byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(salt);
        return md.digest(password.getBytes(StandardCharsets.UTF_8));
    }
}
