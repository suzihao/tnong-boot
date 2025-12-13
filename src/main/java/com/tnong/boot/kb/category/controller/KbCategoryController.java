package com.tnong.boot.kb.category.controller;

import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.kb.category.domain.dto.KbCategoryDTO;
import com.tnong.boot.kb.category.domain.entity.KbCategory;
import com.tnong.boot.kb.category.mapper.KbCategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识库目录控制器
 */
@RestController
@RequestMapping("/api/kb/category")
@RequiredArgsConstructor
public class KbCategoryController {

    private final KbCategoryMapper kbCategoryMapper;

    /**
     * 获取目录树
     */
    @GetMapping("/tree")
    public Result<List<KbCategory>> getTree() {
        Long tenantId = UserContext.getTenantId();
        List<KbCategory> list = kbCategoryMapper.selectListByTenantId(tenantId);
        return Result.success(list);
    }

    /**
     * 获取目录详情
     */
    @GetMapping("/{id}")
    public Result<KbCategory> getById(@PathVariable Long id) {
        KbCategory category = kbCategoryMapper.selectById(id);
        return Result.success(category);
    }

    /**
     * 创建目录
     */
    @PostMapping
    public Result<String> create(@RequestBody KbCategoryDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long userId = UserContext.getUserId();

        KbCategory category = new KbCategory();
        category.setTenantId(tenantId);
        category.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        category.setCreatedUser(userId);
        category.setUpdatedUser(userId);

        kbCategoryMapper.insert(category);
        return Result.success("创建成功");
    }

    /**
     * 更新目录
     */
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody KbCategoryDTO dto) {
        Long userId = UserContext.getUserId();

        KbCategory category = new KbCategory();
        category.setId(id);
        category.setParentId(dto.getParentId());
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort());
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus());
        category.setUpdatedUser(userId);
        category.setVersion(dto.getVersion());

        int rows = kbCategoryMapper.updateById(category);
        if (rows == 0) {
            return Result.fail("更新失败，数据可能已被修改");
        }
        return Result.success("更新成功");
    }

    /**
     * 删除目录
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        kbCategoryMapper.deleteById(id, userId);
        return Result.success("删除成功");
    }
}
