package com.tnong.boot.framework.security;

import lombok.Data;

import java.util.Set;

/**
 * 登录用户信息
 */
@Data
public class LoginUser {

    /**
     * 用户ID
     */
    private Long id;

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

    /**
     * 用户权限标识集合
     * 例如：["system:user:add", "system:user:edit", "system:role:list"]
     */
    private Set<String> permissions;

    /**
     * 是否为超级管理员
     * 超级管理员拥有所有权限，跳过权限校验
     */
    private Boolean isSuperAdmin;
}
