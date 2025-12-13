package com.tnong.boot.system.user.controller;

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
    @GetMapping("/page")
    public Result<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysUserVO> pageResult = sysUserService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    public Result<SysUserVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysUserVO user = sysUserService.getById(id, tenantId);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
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
    @PutMapping
    public Result<Void> update(@RequestBody SysUserSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysUserService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysUserService.delete(id, tenantId, version, currentUserId);
        return Result.success("删除成功", null);
    }
}
