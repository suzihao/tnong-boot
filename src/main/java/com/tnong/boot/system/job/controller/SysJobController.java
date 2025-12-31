package com.tnong.boot.system.job.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.job.domain.dto.SysJobQueryDTO;
import com.tnong.boot.system.job.domain.dto.SysJobSaveDTO;
import com.tnong.boot.system.job.domain.vo.SysJobVO;
import com.tnong.boot.system.job.service.SysJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/job")
@RequiredArgsConstructor
public class SysJobController {

    private final SysJobService sysJobService;

    @GetMapping("/page")
    public Result<PageResult<SysJobVO>> page(SysJobQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysJobVO> pageResult = sysJobService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysJobVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysJobVO job = sysJobService.getById(id, tenantId);
        return Result.success(job);
    }

    @PostMapping
    public Result<Long> save(@RequestBody SysJobSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysJobService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysJobSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysJobService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysJobService.delete(id, tenantId, version, currentUserId);
        return Result.success("删除成功", null);
    }

    @PutMapping("/status/{id}")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysJobService.changeStatus(id, status, tenantId, currentUserId);
        return Result.success("状态修改成功", null);
    }

    @PostMapping("/run/{id}")
    public Result<Void> runOnce(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        sysJobService.runOnce(id, tenantId);
        return Result.success("任务已触发执行", null);
    }
}

