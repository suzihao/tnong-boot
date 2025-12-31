package com.tnong.boot.system.job.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.job.domain.dto.SysJobLogQueryDTO;
import com.tnong.boot.system.job.domain.vo.SysJobLogVO;
import com.tnong.boot.system.job.service.SysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/job-log")
@RequiredArgsConstructor
public class SysJobLogController {

    private final SysJobLogService sysJobLogService;

    @GetMapping("/page")
    public Result<PageResult<SysJobLogVO>> page(SysJobLogQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysJobLogVO> pageResult = sysJobLogService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysJobLogVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysJobLogVO jobLog = sysJobLogService.getById(id, tenantId);
        return Result.success(jobLog);
    }
}
