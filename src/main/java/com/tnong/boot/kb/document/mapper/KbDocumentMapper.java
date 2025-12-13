package com.tnong.boot.kb.document.mapper;

import com.tnong.boot.kb.document.domain.entity.KbDocument;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识文档 Mapper
 */
public interface KbDocumentMapper {
    
    /**
     * 插入文档
     */
    int insert(KbDocument document);
    
    /**
     * 根据ID更新
     */
    int updateById(KbDocument document);
    
    /**
     * 根据ID删除（逻辑删除）
     */
    int deleteById(@Param("id") Long id, @Param("updatedUser") Long updatedUser);
    
    /**
     * 根据ID查询
     */
    KbDocument selectById(@Param("id") Long id);
    
    /**
     * 根据目录ID查询文档列表
     */
    List<KbDocument> selectListByCategoryId(@Param("categoryId") Long categoryId, @Param("tenantId") Long tenantId);
    
    /**
     * 根据租户ID查询所有文档
     */
    List<KbDocument> selectListByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 搜索文档（根据标题）
     */
    List<KbDocument> searchByTitle(@Param("title") String title, @Param("tenantId") Long tenantId);
    
    /**
     * 增加浏览次数
     */
    int increaseViewCount(@Param("id") Long id);
}
