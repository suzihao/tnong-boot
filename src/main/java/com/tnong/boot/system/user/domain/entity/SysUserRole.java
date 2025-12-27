package com.tnong.boot.system.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户-角色关联实体
 */
@Data
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private Long userId;
    private Long roleId;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private Integer version;
}
