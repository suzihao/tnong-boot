package com.tnong.boot.system.role.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色-菜单/权限关联实体
 */
@Data
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private Long roleId;
    private Long menuId;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private Integer version;
}
