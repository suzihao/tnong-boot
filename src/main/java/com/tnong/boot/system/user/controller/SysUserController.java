package com.tnong.boot.system.user.controller;

import com.tnong.boot.common.annotation.Log;
import com.tnong.boot.common.annotation.RequirePermission;
import com.tnong.boot.common.constant.BusinessType;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.dto.SysUserSaveDTO;
import com.tnong.boot.system.user.domain.vo.SysUserVO;
import com.tnong.boot.system.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @RequirePermission("user:list")
    @GetMapping("/page")
    public Result<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysUserVO> pageResult = sysUserService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询用户详情
     */
    @RequirePermission("user:query")
    @GetMapping("/{id}")
    public Result<SysUserVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysUserVO user = sysUserService.getById(id, tenantId);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @RequirePermission("user:add")
    @Log(module = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> save(@RequestBody SysUserSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long userId = sysUserService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", userId);
    }

    /**
     * 更新用户
     */
    @RequirePermission("user:edit")
    @Log(module = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysUserSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysUserService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @RequirePermission("user:delete")
    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody SysUserSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysUserService.delete(dto.getId(), tenantId, dto.getVersion(), currentUserId);
        return Result.success("删除成功", null);
    }

    /**
     * 查询用户已分配的角色ID列表
     */
    @RequirePermission("user:query")
    @GetMapping("/{userId}/role-ids")
    public Result<java.util.List<Long>> getRoleIds(@PathVariable Long userId) {
        Long tenantId = UserContext.getTenantId();
        java.util.List<Long> roleIds = sysUserService.getUserRoleIds(userId, tenantId);
        return Result.success(roleIds);
    }

    /**
     * 为用户分配角色
     */
    @RequirePermission(value = {"user:edit", "role:assign"}, logical = RequirePermission.Logical.AND)
    @PostMapping("/{userId}/role-ids")
    public Result<Void> assignRoles(@PathVariable Long userId, @RequestBody java.util.List<Long> roleIds) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysUserService.assignRoles(userId, roleIds, tenantId, currentUserId);
        return Result.success("分配角色成功", null);
    }
}
