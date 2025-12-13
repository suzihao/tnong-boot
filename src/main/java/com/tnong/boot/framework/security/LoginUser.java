package com.tnong.boot.framework.security;

import lombok.Data;

/**
 * 登录用户信息
 */
@Data
public class LoginUser {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 部门ID
     */
    private Long deptId;
    
    /**
     * Token
     */
    private String token;
}
