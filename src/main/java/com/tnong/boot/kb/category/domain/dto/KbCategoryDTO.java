package com.tnong.boot.kb.category.domain.dto;

import lombok.Data;

/**
 * 知识库目录DTO
 */
@Data
public class KbCategoryDTO {
    
    /** 目录ID（更新时需要） */
    private Long id;
    
    /** 父目录ID */
    private Long parentId;
    
    /** 目录名称 */
    private String name;
    
    /** 图标 */
    private String icon;
    
    /** 排序 */
    private Integer sort;
    
    /** 描述 */
    private String description;
    
    /** 状态 */
    private Integer status;
    
    /** 版本号（更新时需要） */
    private Integer version;
}
