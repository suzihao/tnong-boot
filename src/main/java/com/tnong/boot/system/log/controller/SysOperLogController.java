package com.tnong.boot.system.log.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.log.domain.dto.SysOperLogQueryDTO;
import com.tnong.boot.system.log.domain.vo.SysOperLogVO;
import com.tnong.boot.system.log.service.SysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志控制器
 */
@RestController
@RequestMapping("/api/system/log/oper")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService sysOperLogService;

    /**
     * 分页查询操作日志列表
     */
    @GetMapping("/page")
    public Result<PageResult<SysOperLogVO>> page(SysOperLogQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysOperLogVO> pageResult = sysOperLogService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询操作日志详情
     */
    @GetMapping("/{id}")
    public Result<SysOperLogVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysOperLogVO log = sysOperLogService.getById(id, tenantId);
        return Result.success(log);
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteByIds(@RequestBody List<Long> ids) {
        Long tenantId = UserContext.getTenantId();
        sysOperLogService.deleteByIds(ids, tenantId);
        return Result.success("删除成功", null);
    }

    /**
     * 清空操作日志
     */
    @DeleteMapping("/truncate")
    public Result<Void> truncate() {
        Long tenantId = UserContext.getTenantId();
        sysOperLogService.truncate(tenantId);
        return Result.success("清空成功", null);
    }
}
