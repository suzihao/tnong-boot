package com.tnong.boot.system.role.controller;

import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.role.domain.dto.RoleMenuAssignDTO;
import com.tnong.boot.system.role.domain.dto.SysRoleSaveDTO;
import com.tnong.boot.system.role.domain.vo.SysRoleVO;
import com.tnong.boot.system.role.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    public Result<List<SysRoleVO>> list() {
        Long tenantId = UserContext.getTenantId();
        List<SysRoleVO> list = sysRoleService.list(tenantId);
        return Result.success(list);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Long> save(@RequestBody SysRoleSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysRoleService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    /**
     * 更新角色
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysRoleSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysRoleService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody SysRoleSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysRoleService.delete(dto.getId(), tenantId, dto.getVersion(), currentUserId);
        return Result.success("删除成功", null);
    }

    /**
     * 查询角色已分配的菜单ID
     */
    @GetMapping("/{roleId}/menu-ids")
    public Result<List<Long>> listMenuIds(@PathVariable Long roleId) {
        Long tenantId = UserContext.getTenantId();
        List<Long> menuIds = sysRoleService.listMenuIdsByRole(roleId, tenantId);
        return Result.success(menuIds);
    }

    /**
     * 为角色分配菜单
     */
    @PostMapping("/{roleId}/menu-ids")
    public Result<Void> assignMenus(@PathVariable Long roleId, @RequestBody RoleMenuAssignDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysRoleService.assignMenus(roleId, tenantId, dto, currentUserId);
        return Result.success("保存成功", null);
    }
}
