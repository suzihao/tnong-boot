package com.tnong.boot.system.dept.service;

import java.util.List;

import com.tnong.boot.system.dept.domain.dto.SysDeptQueryDTO;
import com.tnong.boot.system.dept.domain.dto.SysDeptSaveDTO;
import com.tnong.boot.system.dept.domain.vo.SysDeptSimpleVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptTreeVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptVO;

/**
 * 部门服务接口
 */
public interface SysDeptService {

    /**
     * 查询部门列表
     */
    List<SysDeptVO> list(SysDeptQueryDTO query, Long tenantId);

    /**
     * 查询部门树形结构
     */
    List<SysDeptTreeVO> tree(Long tenantId);

    /**
     * 查询简单部门列表（用于下拉框）
     */
    List<SysDeptSimpleVO> simpleList(Long tenantId);

    /**
     * 根据ID查询部门详情
     */
    SysDeptVO getById(Long id, Long tenantId);

    /**
     * 新增部门
     */
    Long save(SysDeptSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 更新部门
     */
    void update(SysDeptSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 删除部门
     */
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
}
