package com.tnong.boot.system.dict.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.dict.domain.dto.SysDictItemQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictItemSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictItemVO;
import com.tnong.boot.system.dict.service.SysDictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system/dict/item")
@RequiredArgsConstructor
public class SysDictItemController {

    private final SysDictItemService sysDictItemService;

    @GetMapping("/page")
    public Result<PageResult<SysDictItemVO>> page(SysDictItemQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysDictItemVO> pageResult = sysDictItemService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysDictItemVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysDictItemVO dictItem = sysDictItemService.getById(id, tenantId);
        return Result.success(dictItem);
    }

    @GetMapping("/list/{typeCode}")
    public Result<List<SysDictItemVO>> listByTypeCode(@PathVariable String typeCode) {
        Long tenantId = UserContext.getTenantId();
        List<SysDictItemVO> list = sysDictItemService.listByTypeCode(typeCode, tenantId);
        return Result.success(list);
    }

    @PostMapping
    public Result<Long> save(@RequestBody SysDictItemSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        Long id = sysDictItemService.save(dto, tenantId, currentUserId);
        return Result.success("新增成功", id);
    }

    @PutMapping
    public Result<Void> update(@RequestBody SysDictItemSaveDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDictItemService.update(dto, tenantId, currentUserId);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestParam Integer version) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysDictItemService.delete(id, tenantId, version, currentUserId);
        return Result.success("删除成功", null);
    }
}
