package com.tnong.boot.system.role.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
public class SysRole implements Serializable {
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
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
