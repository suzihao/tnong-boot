package com.tnong.boot.system.dept.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 部门实体
 */
@Data
public class SysDept implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String name;
    private String code;
    private Integer sort;
    private Integer status;
    private Long leaderUserId;
    private String phone;
    private String email;
    private String remark;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
