package com.tnong.boot.system.dept.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tnong.boot.common.annotation.Log;
import com.tnong.boot.common.constant.BusinessType;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.dept.domain.dto.SysDeptQueryDTO;
import com.tnong.boot.system.dept.domain.dto.SysDeptSaveDTO;
import com.tnong.boot.system.dept.domain.vo.SysDeptSimpleVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptTreeVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptVO;
import com.tnong.boot.system.dept.service.SysDeptService;

import lombok.RequiredArgsConstructor;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 查询部门列表
     */
    @GetMapping("/list")
    public Result<List<SysDeptVO>> list(SysDeptQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        List<SysDeptVO> list = sysDeptService.list(query, tenantId);
        return Result.success(list);
    }

    /**
     * 查询部门树形结构
     */
    @GetMapping("/tree")
    public Result<List<SysDeptTreeVO>> tree() {
        Long tenantId = UserContext.getTenantId();
        List<SysDeptTreeVO> tree = sysDeptService.tree(tenantId);
        return Result.success(tree);
    }

    /**
     * 查询简单部门列表（用于下拉框）
     */
    @GetMapping("/simple")
    public Result<List<SysDeptSimpleVO>> simpleList() {
        Long tenantId = UserContext.getTenantId();
        List<SysDeptSimpleVO> list = sysDeptService.simpleList(tenantId);
        return Result.success(list);
    }

    /**
     * 根据ID查询部门详情
     */
    @GetMapping("/{id}")
    public Result<SysDeptVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysDeptVO dept = sysDeptService.getById(id, tenantId);
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @Log(module = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result<Long> save(@RequestBody SysDeptSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysDeptService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    /**
     * 更新部门
     */
    @Log(module = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result<Void> update(@RequestBody SysDeptSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDeptService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    /**
     * 删除部门
     */
    @Log(module = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDeptService.delete(id, tenantId, version, currentUserId);
        return Result.success("删除成功", null);
    }
}
