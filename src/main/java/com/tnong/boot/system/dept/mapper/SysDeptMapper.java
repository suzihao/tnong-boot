package com.tnong.boot.system.dept.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tnong.boot.system.dept.domain.dto.SysDeptQueryDTO;
import com.tnong.boot.system.dept.domain.entity.SysDept;
import com.tnong.boot.system.dept.domain.vo.SysDeptSimpleVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptTreeVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptVO;

/**
 * 部门 Mapper 接口
 */
public interface SysDeptMapper {

    /**
     * 根据ID查询部门
     */
    SysDept selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 根据部门编码查询
     */
    SysDept selectByCode(@Param("code") String code, @Param("tenantId") Long tenantId);

    /**
     * 查询部门列表（带负责人姓名）
     */
    List<SysDeptVO> selectList(@Param("query") SysDeptQueryDTO query, @Param("tenantId") Long tenantId);

    /**
     * 查询树形结构（所有部门）
     */
    List<SysDeptTreeVO> selectTreeList(@Param("tenantId") Long tenantId);

    /**
     * 查询简单部门列表（用于下拉框）
     */
    List<SysDeptSimpleVO> selectSimpleList(@Param("tenantId") Long tenantId);

    /**
     * 查询子部门ID列表（递归）
     */
    List<Long> selectChildrenIds(@Param("parentId") Long parentId, @Param("tenantId") Long tenantId);

    /**
     * 查询部门用户数量
     */
    Long countUsersByDeptId(@Param("deptId") Long deptId, @Param("tenantId") Long tenantId);

    /**
     * 插入部门
     */
    int insert(SysDept dept);

    /**
     * 更新部门（带乐观锁）
     */
    int updateByIdWithVersion(SysDept dept);

    /**
     * 逻辑删除部门（带乐观锁）
     */
    int deleteById(@Param("id") Long id,
                   @Param("tenantId") Long tenantId,
                   @Param("version") Integer version,
                   @Param("updatedUser") Long updatedUser);
}
