package com.tnong.boot.framework.security;

/**
 * 用户上下文 - 存储当前登录用户信息
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void set(LoginUser loginUser) {
        USER_THREAD_LOCAL.set(loginUser);
    }

    /**
     * 获取当前用户
     */
    public static LoginUser get() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getUserId() {
        LoginUser user = get();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前租户ID
     */
    public static Long getTenantId() {
        LoginUser user = get();
        return user != null ? user.getTenantId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getUsername() {
        LoginUser user = get();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 清除当前用户
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
