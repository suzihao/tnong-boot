package com.tnong.boot.system.menu.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单实体
 */
@Data
public class SysMenu implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long tenantId;
    private Long parentId;
    private Integer type;
    private String name;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private String remark;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
