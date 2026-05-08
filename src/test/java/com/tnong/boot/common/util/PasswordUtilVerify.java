package com.tnong.boot.common.util;

/**
 * 密码工具验证程序
 */
public class PasswordUtilVerify {

    public static void main(String[] args) {
        System.out.println("=== 密码加密工具验证 ===\n");

        // 测试1: 密码加密
        System.out.println("测试1: 密码加密");
        String rawPassword = "123456";
        String encoded = PasswordUtil.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后: " + encoded);
        System.out.println("加密格式正确: " + (encoded.startsWith("$2a$") || encoded.startsWith("$2b$")));
        System.out.println();

        // 测试2: 密码验证 - 正确密码
        System.out.println("测试2: 密码验证 - 正确密码");
        boolean matches = PasswordUtil.matches(rawPassword, encoded);
        System.out.println("验证结果: " + matches);
        System.out.println();

        // 测试3: 密码验证 - 错误密码
        System.out.println("测试3: 密码验证 - 错误密码");
        boolean wrongMatches = PasswordUtil.matches("wrongpassword", encoded);
        System.out.println("验证结果: " + wrongMatches);
        System.out.println();

        // 测试4: 每次加密生成不同哈希
        System.out.println("测试4: 每次加密生成不同哈希");
        String encoded1 = PasswordUtil.encode(rawPassword);
        String encoded2 = PasswordUtil.encode(rawPassword);
        System.out.println("第一次加密: " + encoded1);
        System.out.println("第二次加密: " + encoded2);
        System.out.println("两次加密结果不同: " + !encoded1.equals(encoded2));
        System.out.println("两次加密都能验证: " + PasswordUtil.matches(rawPassword, encoded1) + " && " + PasswordUtil.matches(rawPassword, encoded2));
        System.out.println();

        // 测试5: 空密码异常
        System.out.println("测试5: 空密码异常");
        try {
            PasswordUtil.encode("");
            System.out.println("❌ 应该抛出异常");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ 正确抛出异常: " + e.getMessage());
        }
        System.out.println();

        // 测试6: null密码异常
        System.out.println("测试6: null密码异常");
        try {
            PasswordUtil.encode(null);
            System.out.println("❌ 应该抛出异常");
        } catch (IllegalArgumentException e) {
            System.out.println("✓ 正确抛出异常: " + e.getMessage());
        }
        System.out.println();

        // 测试7: null参数处理
        System.out.println("测试7: null参数处理");
        System.out.println("matches(null, encoded): " + PasswordUtil.matches(null, encoded));
        System.out.println("matches(password, null): " + PasswordUtil.matches("test", null));
        System.out.println("matches(null, null): " + PasswordUtil.matches(null, null));
        System.out.println();

        System.out.println("=== 所有测试完成 ===");
    }
}
