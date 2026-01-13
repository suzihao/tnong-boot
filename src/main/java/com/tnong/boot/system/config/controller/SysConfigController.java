package com.tnong.boot.system.config.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.config.domain.dto.SysConfigQueryDTO;
import com.tnong.boot.system.config.domain.dto.SysConfigSaveDTO;
import com.tnong.boot.system.config.domain.vo.SysConfigVO;
import com.tnong.boot.system.config.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final SysConfigService sysConfigService;

    @GetMapping("/page")
    public Result<PageResult<SysConfigVO>> page(SysConfigQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysConfigVO> pageResult = sysConfigService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysConfigVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysConfigVO config = sysConfigService.getById(id, tenantId);
        return Result.success(config);
    }

    @GetMapping("/key/{configKey}")
    public Result<String> getValueByKey(@PathVariable String configKey) {
        Long tenantId = UserContext.getTenantId();
        String value = sysConfigService.getValueByKey(configKey, tenantId);
        return Result.success(value);
    }

    @PostMapping
    public Result<Long> save(@RequestBody SysConfigSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysConfigService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysConfigSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysConfigService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody SysConfigSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysConfigService.delete(dto.getId(), tenantId, dto.getVersion(), currentUserId);
        return Result.success("删除成功", null);
    }
}
