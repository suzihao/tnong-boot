package com.tnong.boot.kb.document.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识文档实体
 */
@Data
public class KbDocument {
    
    /** 文档ID */
    private Long id;
    
    /** 租户ID */
    private Long tenantId;
    
    /** 目录ID */
    private Long categoryId;
    
    /** 文档标题 */
    private String title;
    
    /** Markdown内容 */
    private String content;
    
    /** 标签，逗号分隔 */
    private String tags;
    
    /** 排序 */
    private Integer sort;
    
    /** 浏览次数 */
    private Integer viewCount;
    
    /** 状态：1已发布，0草稿 */
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
