package com.tnong.boot.system.dept.mapper;

import com.tnong.boot.system.dept.domain.entity.SysDept;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysDeptMapper {
    SysDept selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    List<SysDept> selectList(@Param("tenantId") Long tenantId);
    List<SysDept> selectByParentId(@Param("parentId") Long parentId, @Param("tenantId") Long tenantId);
    int insert(SysDept dept);
    int updateByIdWithVersion(SysDept dept);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
