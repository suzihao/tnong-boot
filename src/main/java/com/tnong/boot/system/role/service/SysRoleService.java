package com.tnong.boot.system.role.service;

import com.tnong.boot.system.role.domain.dto.RoleMenuAssignDTO;
import com.tnong.boot.system.role.domain.dto.SysRoleSaveDTO;
import com.tnong.boot.system.role.domain.vo.SysRoleVO;

import java.util.List;

public interface SysRoleService {

    /**
     * 查询角色列表（当前租户）
     */
    List<SysRoleVO> list(Long tenantId);

    /**
     * 新增角色
     */
    Long save(SysRoleSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 更新角色
     */
    void update(SysRoleSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 删除角色
     */
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);

    /**
     * 查询角色已分配的菜单ID
     */
    List<Long> listMenuIdsByRole(Long roleId, Long tenantId);

    /**
     * 为角色分配菜单
     */
    void assignMenus(Long roleId, Long tenantId, RoleMenuAssignDTO dto, Long currentUserId);
}
