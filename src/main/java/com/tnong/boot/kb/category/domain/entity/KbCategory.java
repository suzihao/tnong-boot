package com.tnong.boot.kb.category.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库目录实体
 */
@Data
public class KbCategory {
    
    /** 目录ID */
    private Long id;
    
    /** 租户ID */
    private Long tenantId;
    
    /** 父目录ID，0表示根目录 */
    private Long parentId;
    
    /** 目录名称 */
    private String name;
    
    /** 图标 */
    private String icon;
    
    /** 排序 */
    private Integer sort;
    
    /** 描述 */
    private String description;
    
    /** 状态：1启用，0禁用 */
    private Integer status;
    
    /** 删除标记：0未删除，1已删除 */
    private Integer deleteFlag;
    
    /** 创建时间 */
    private LocalDateTime createdTime;

    /** 创建人ID */
    private Long createdUser;
        
    /** 更新时间 */
    private LocalDateTime updatedTime;
    
    /** 更新人ID */
    private Long updatedUser;
        
    /** 乐观锁版本号 */
    private Integer version;
}
