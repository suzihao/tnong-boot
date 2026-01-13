package com.tnong.boot.biz.kb.document.domain.dto;

import lombok.Data;

/**
 * 知识文档DTO
 */
@Data
public class KbDocumentDTO {
    
    /** 文档ID（更新时需要） */
    private Long id;
    
    /** 目录ID */
    private Long categoryId;
    
    /** 文档标题 */
    private String title;
    
    /** Markdown内容 */
    private String content;
    
    /** 标签 */
    private String tags;
    
    /** 排序 */
    private Integer sort;
    
    /** 状态 */
    private Integer status;
    
    /** 版本号（更新时需要） */
    private Integer version;
}
