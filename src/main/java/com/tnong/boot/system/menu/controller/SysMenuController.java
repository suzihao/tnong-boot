package com.tnong.boot.system.menu.controller;

import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.menu.domain.dto.SysMenuSaveDTO;
import com.tnong.boot.system.menu.domain.vo.MyMenuAndPermVO;
import com.tnong.boot.system.menu.domain.vo.SysMenuVO;
import com.tnong.boot.system.menu.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    /**
     * 查询菜单树（根据用户权限过滤）
     */
    @GetMapping("/tree")
    public Result<List<SysMenuVO>> tree() {
        Long tenantId = UserContext.getTenantId();
        Long userId = UserContext.getUserId();
        List<SysMenuVO> tree = sysMenuService.tree(tenantId, userId);
        return Result.success(tree);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result<Long> save(@RequestBody SysMenuSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysMenuService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    /**
     * 更新菜单
     */
    @PutMapping
    public Result<Void> update(@RequestBody SysMenuSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysMenuService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysMenuService.delete(id, tenantId, version, currentUserId);
        return Result.success("删除成功", null);
    }

    /**
     * 当前用户菜单与权限
     */
    @GetMapping("/my-routes")
    public Result<MyMenuAndPermVO> myRoutes() {
        Long tenantId = UserContext.getTenantId();
        Long userId = UserContext.getUserId();
        MyMenuAndPermVO vo = sysMenuService.currentUserMenusAndPerms(tenantId, userId);
        return Result.success(vo);
    }
}
