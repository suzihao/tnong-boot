package com.tnong.boot.system.tenant.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.dto.SysTenantSaveDTO;
import com.tnong.boot.system.tenant.domain.vo.SysTenantVO;
import com.tnong.boot.system.tenant.service.SysTenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 租户管理控制器
 */
@RestController
@RequestMapping("/api/system/tenant")
@RequiredArgsConstructor
public class SysTenantController {

    private final SysTenantService sysTenantService;

    /**
     * 分页查询租户列表
     */
    @GetMapping("/page")
    public Result<PageResult<SysTenantVO>> page(SysTenantQueryDTO query) {
        PageResult<SysTenantVO> pageResult = sysTenantService.pageList(query);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询租户详情
     */
    @GetMapping("/{id}")
    public Result<SysTenantVO> getById(@PathVariable Long id) {
        SysTenantVO tenant = sysTenantService.getById(id);
        return Result.success(tenant);
    }

    /**
     * 新增租户
     */
    @PostMapping
    public Result<Long> save(@RequestBody SysTenantSaveDTO dto) {
        Long currentUserId = 1L;
        Long tenantId = sysTenantService.save(dto, currentUserId);
        return Result.success("新增成功", tenantId);
    }

    /**
     * 更新租户
     */
    @PutMapping
    public Result<Void> update(@RequestBody SysTenantSaveDTO dto) {
        Long currentUserId = 1L;
        sysTenantService.update(dto, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long currentUserId = 1L;
        sysTenantService.delete(id, version, currentUserId);
        return Result.success("删除成功", null);
    }
}
