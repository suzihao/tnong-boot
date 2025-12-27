package com.tnong.boot.system.dept.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.dept.domain.dto.SysDeptQueryDTO;
import com.tnong.boot.system.dept.domain.dto.SysDeptSaveDTO;
import com.tnong.boot.system.dept.domain.entity.SysDept;
import com.tnong.boot.system.dept.domain.vo.SysDeptSimpleVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptTreeVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptVO;
import com.tnong.boot.system.dept.mapper.SysDeptMapper;
import com.tnong.boot.system.dept.service.SysDeptService;
import com.tnong.boot.system.user.mapper.SysUserDeptMapper;

import lombok.RequiredArgsConstructor;

/**
 * 部门服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl implements SysDeptService {

    private final SysDeptMapper sysDeptMapper;
    private final SysUserDeptMapper sysUserDeptMapper;

    @Override
    public List<SysDeptVO> list(SysDeptQueryDTO query, Long tenantId) {
        return sysDeptMapper.selectList(query, tenantId);
    }

    @Override
    public List<SysDeptTreeVO> tree(Long tenantId) {
        List<SysDeptTreeVO> allDepts = sysDeptMapper.selectTreeList(tenantId);
        return buildTree(allDepts, 0L);
    }

    @Override
    public List<SysDeptSimpleVO> simpleList(Long tenantId) {
        return sysDeptMapper.selectSimpleList(tenantId);
    }

    @Override
    public SysDeptVO getById(Long id, Long tenantId) {
        SysDept dept = sysDeptMapper.selectById(id, tenantId);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        SysDeptVO vo = new SysDeptVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysDeptSaveDTO dto, Long tenantId, Long currentUserId) {
        // 校验部门编码是否已存在
        if (StringUtils.hasText(dto.getCode())) {
            SysDept existDept = sysDeptMapper.selectByCode(dto.getCode(), tenantId);
            if (existDept != null) {
                throw new BusinessException("部门编码已存在");
            }
        }

        // 校验父部门是否存在
        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            SysDept parentDept = sysDeptMapper.selectById(dto.getParentId(), tenantId);
            if (parentDept == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        // 构建实体
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);
        dept.setTenantId(tenantId);
        dept.setCreatedUser(currentUserId);
        dept.setUpdatedUser(currentUserId);

        // 设置默认值
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        if (dept.getStatus() == null) {
            dept.setStatus(1);
        }

        sysDeptMapper.insert(dept);
        return dept.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDeptSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("部门ID不能为空");
        }

        // 查询原部门信息
        SysDept existDept = sysDeptMapper.selectById(dto.getId(), tenantId);
        if (existDept == null) {
            throw new BusinessException("部门不存在");
        }

        // 校验部门编码是否重复
        if (StringUtils.hasText(dto.getCode()) && !dto.getCode().equals(existDept.getCode())) {
            SysDept codeDept = sysDeptMapper.selectByCode(dto.getCode(), tenantId);
            if (codeDept != null) {
                throw new BusinessException("部门编码已存在");
            }
        }

        // 校验不能将部门设置为自己的子部门
        if (dto.getParentId() != null && dto.getParentId().equals(dto.getId())) {
            throw new BusinessException("不能将部门设置为自己的子部门");
        }

        // 校验不能将部门设置为自己的后代部门
        if (dto.getParentId() != null && dto.getParentId() != 0L) {
            List<Long> childrenIds = sysDeptMapper.selectChildrenIds(dto.getId(), tenantId);
            if (childrenIds.contains(dto.getParentId())) {
                throw new BusinessException("不能将部门设置为其后代部门的子部门");
            }

            // 校验父部门是否存在
            SysDept parentDept = sysDeptMapper.selectById(dto.getParentId(), tenantId);
            if (parentDept == null) {
                throw new BusinessException("父部门不存在");
            }
        }

        // 构建更新实体
        SysDept dept = new SysDept();
        BeanUtils.copyProperties(dto, dept);
        dept.setTenantId(tenantId);
        dept.setUpdatedUser(currentUserId);

        // 执行更新（带乐观锁）
        int rows = sysDeptMapper.updateByIdWithVersion(dept);
        if (rows == 0) {
            throw new OptimisticLockException("部门信息已被修改，请刷新后重试");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        // 校验部门是否存在
        SysDept dept = sysDeptMapper.selectById(id, tenantId);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }

        // 校验是否存在子部门
        List<Long> childrenIds = sysDeptMapper.selectChildrenIds(id, tenantId);
        if (!childrenIds.isEmpty()) {
            throw new BusinessException("存在子部门，无法删除");
        }

        // 校验是否存在用户
        Long userCount = sysDeptMapper.countUsersByDeptId(id, tenantId);
        if (userCount > 0) {
            throw new BusinessException("部门下存在用户，无法删除");
        }

        // 执行删除（带乐观锁）
        int rows = sysDeptMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("部门信息已被修改，请刷新后重试");
        }

        // 删除用户-部门关联关系
        sysUserDeptMapper.deleteByDeptId(tenantId, id, currentUserId);
    }

    /**
     * 构建树形结构
     */
    private List<SysDeptTreeVO> buildTree(List<SysDeptTreeVO> allDepts, Long parentId) {
        List<SysDeptTreeVO> tree = new ArrayList<>();

        // 按父ID分组
        Map<Long, List<SysDeptTreeVO>> groupByParent = allDepts.stream()
                .collect(Collectors.groupingBy(SysDeptTreeVO::getParentId));

        // 递归构建树
        for (SysDeptTreeVO dept : allDepts) {
            if (dept.getParentId().equals(parentId)) {
                List<SysDeptTreeVO> children = groupByParent.get(dept.getId());
                if (children != null && !children.isEmpty()) {
                    dept.setChildren(children);
                }
                tree.add(dept);
            }
        }

        return tree;
    }
}
