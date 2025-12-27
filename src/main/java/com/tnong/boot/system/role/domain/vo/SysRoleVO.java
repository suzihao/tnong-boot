package com.tnong.boot.system.role.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色返回对象
 */
@Data
public class SysRoleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private String code;
    private String name;
    private Integer dataScope;
    private Integer sort;
    private Integer status;
    private Integer isSystem;
    private String remark;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer version;
}
