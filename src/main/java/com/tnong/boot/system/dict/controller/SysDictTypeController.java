package com.tnong.boot.system.dict.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictTypeVO;
import com.tnong.boot.system.dict.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @GetMapping("/page")
    public Result<PageResult<SysDictTypeVO>> page(SysDictTypeQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysDictTypeVO> pageResult = sysDictTypeService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysDictTypeVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysDictTypeVO dictType = sysDictTypeService.getById(id, tenantId);
        return Result.success(dictType);
    }

    @PostMapping
    public Result<Long> save(@RequestBody SysDictTypeSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysDictTypeService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody SysDictTypeSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDictTypeService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody SysDictTypeSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDictTypeService.delete(dto.getId(), tenantId, dto.getVersion(), currentUserId);
        return Result.success("删除成功", null);
    }
}
