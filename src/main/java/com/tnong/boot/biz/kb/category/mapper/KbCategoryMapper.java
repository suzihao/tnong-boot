package com.tnong.boot.biz.kb.category.mapper;

import com.tnong.boot.biz.kb.category.domain.entity.KbCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 知识库目录 Mapper
 */
public interface KbCategoryMapper {
    
    /**
     * 插入目录
     */
    int insert(KbCategory category);
    
    /**
     * 根据ID更新
     */
    int updateById(KbCategory category);
    
    /**
     * 根据ID删除（逻辑删除）
     */
    int deleteById(@Param("id") Long id, @Param("updatedUser") Long updatedUser);
    
    /**
     * 根据ID查询
     */
    KbCategory selectById(@Param("id") Long id);
    
    /**
     * 查询租户下的所有目录（树形结构）
     */
    List<KbCategory> selectListByTenantId(@Param("tenantId") Long tenantId);
    
    /**
     * 更新父级和排序
     */
    int updateParentAndSort(@Param("id") Long id,
                            @Param("parentId") Long parentId,
                            @Param("sort") Integer sort,
                            @Param("updatedUser") Long updatedUser);
}
