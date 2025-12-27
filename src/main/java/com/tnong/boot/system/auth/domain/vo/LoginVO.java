package com.tnong.boot.system.auth.domain.vo;

import lombok.Data;
import java.io.Serializable;

/**
 * 登录响应
 */
@Data
public class LoginVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Token
     */
    private String token;
    
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
}
