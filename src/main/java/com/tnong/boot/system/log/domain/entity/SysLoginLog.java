package com.tnong.boot.system.log.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志实体
 */
@Data
public class SysLoginLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long tenantId;
    private Long userId;
    private String username;
    private Integer loginType;
    private Integer status;
    private String ipAddress;
    private String userAgent;
    private String os;
    private String browser;
    private String msg;
    private LocalDateTime loginTime;
}
