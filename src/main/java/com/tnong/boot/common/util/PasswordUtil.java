package com.tnong.boot.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * 使用 BCrypt 算法（Spring Security Crypto）
 * BCrypt 是专门为密码哈希设计的慢哈希算法，具有以下优势：
 * 1. 自动生成和管理盐值
 * 2. 可配置工作因子（计算复杂度）
 * 3. 抗暴力破解和彩虹表攻击
 */
public class PasswordUtil {

    /**
     * BCrypt 编码器
     * 工作因子设置为 12（2^12 = 4096 次迭代）
     * 工作因子越高，计算越慢，安全性越高，但也会影响性能
     * 推荐值：10-12
     */
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder(12);

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码（包含盐值和哈希值）
     */
    public static String encode(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码
     *
     * @param rawPassword 原始密码
     * @param encodedPassword 加密后的密码
     * @return 密码是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        try {
            return ENCODER.matches(rawPassword, encodedPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
