package com.tnong.boot.system.role.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.role.domain.dto.RoleMenuAssignDTO;
import com.tnong.boot.system.role.domain.dto.SysRoleSaveDTO;
import com.tnong.boot.system.role.domain.entity.SysRole;
import com.tnong.boot.system.role.domain.vo.SysRoleVO;
import com.tnong.boot.system.role.mapper.SysRoleMapper;
import com.tnong.boot.system.role.mapper.SysRoleMenuMapper;
import com.tnong.boot.system.role.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysRoleVO> list(Long tenantId) {
        List<SysRole> roles = sysRoleMapper.selectList(tenantId);
        return roles.stream().map(role -> {
            SysRoleVO vo = new SysRoleVO();
            BeanUtils.copyProperties(role, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysRoleSaveDTO dto, Long tenantId, Long currentUserId) {
        if (!Objects.isNull(dto.getId())) {
            throw new BusinessException("新增角色时不允许携带ID");
        }
        if (!Objects.isNull(dto.getVersion())) {
            throw new BusinessException("新增角色时不允许携带版本号");
        }

        SysRole exist = sysRoleMapper.selectByCode(dto.getCode(), tenantId);
        if (exist != null) {
            throw new BusinessException("角色编码已存在");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setTenantId(tenantId);
        role.setDeleteFlag(CommonConstant.DELETE_FLAG_NORMAL);
        role.setCreatedUser(currentUserId);
        role.setUpdatedUser(currentUserId);
        if (role.getStatus() == null) {
            role.setStatus(CommonConstant.STATUS_ENABLE);
        }
        if (role.getIsSystem() == null) {
            role.setIsSystem(0);
        }

        int rows = sysRoleMapper.insert(role);
        if (rows == 0) {
            throw new BusinessException("新增角色失败");
        }
        return role.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysRoleSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("角色ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysRole dbRole = sysRoleMapper.selectById(dto.getId(), tenantId);
        if (dbRole == null || !Objects.equals(dbRole.getTenantId(), tenantId)) {
            throw new BusinessException("角色不存在或已删除");
        }

        // 角色编码不可修改
        if (dto.getCode() != null && !dto.getCode().equals(dbRole.getCode())) {
            throw new BusinessException("角色编码不允许修改");
        }

        SysRole role = new SysRole();
        BeanUtils.copyProperties(dto, role);
        role.setTenantId(tenantId);
        role.setCode(null);
        role.setUpdatedUser(currentUserId);

        int rows = sysRoleMapper.updateByIdWithVersion(role);
        if (rows == 0) {
            throw new OptimisticLockException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }
        int rows = sysRoleMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
        // 这里不强制级联删除角色菜单关系，只做逻辑删除；如需清理可在后续补充
    }

    @Override
    public List<Long> listMenuIdsByRole(Long roleId, Long tenantId) {
        return sysRoleMenuMapper.selectMenuIdsByRoleId(tenantId, roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, Long tenantId, RoleMenuAssignDTO dto, Long currentUserId) {
        // 先校验角色是否存在
        SysRole role = sysRoleMapper.selectById(roleId, tenantId);
        if (role == null || !Objects.equals(role.getTenantId(), tenantId)) {
            throw new BusinessException("角色不存在或已删除");
        }

        // 先清空原有关系
        sysRoleMenuMapper.deleteByRoleId(tenantId, roleId, currentUserId);

        // 再批量插入新关系
        List<Long> menuIds = dto.getMenuIds();
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMenuMapper.insertBatch(tenantId, roleId, menuIds, currentUserId);
        }
    }
}
