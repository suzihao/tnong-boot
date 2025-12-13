package com.tnong.boot.system.auth.domain.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录请求参数
 */
@Data
public class LoginDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 租户编码（可选，如果不传则使用默认租户）
     */
    private String tenantCode;
}
