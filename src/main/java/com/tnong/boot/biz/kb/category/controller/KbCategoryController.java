package com.tnong.boot.biz.kb.category.controller;

import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.biz.kb.category.domain.dto.KbCategoryDTO;
import com.tnong.boot.biz.kb.category.domain.entity.KbCategory;
import com.tnong.boot.biz.kb.category.mapper.KbCategoryMapper;
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
    
        KbCategory category = new KbCategory();
        category.setTenantId(UserContext.getTenantId());
        category.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        category.setCreatedUser(UserContext.getUserId());
        category.setUpdatedUser(UserContext.getUserId());
    
        kbCategoryMapper.insert(category);
        return Result.success("创建成功");
    }

    /**
     * 更新目录
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody KbCategoryDTO dto) {
        Long currentUserBizId = UserContext.getUserId();

        KbCategory category = new KbCategory();
        category.setId(dto.getId());
        category.setParentId(dto.getParentId());
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort());
        category.setDescription(dto.getDescription());
        category.setStatus(dto.getStatus());
        category.setUpdatedUser(currentUserBizId);
        category.setVersion(dto.getVersion());

        int rows = kbCategoryMapper.updateById(category);
        if (rows == 0) {
            return Result.fail("更新失败，数据可能已被修改");
        }
        return Result.success("更新成功");
    }

    /**
     * 拖动调整目录层级与排序
     */
    @PostMapping("/move")
    public Result<String> move(@RequestParam Long id,
                               @RequestParam Long parentId,
                               @RequestParam Integer sort) {
        Long currentUserBizId = UserContext.getUserId();
        kbCategoryMapper.updateParentAndSort(id, parentId, sort, currentUserBizId);
        return Result.success("调整成功");
    }

    /**
     * 删除目录
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody KbCategoryDTO dto) {
        Long currentUserBizId = UserContext.getUserId();
        int rows = kbCategoryMapper.deleteById(dto.getId(), currentUserBizId);
        if (rows == 0) {
            return Result.fail("删除失败，目录不存在或已被删除");
        }
        return Result.success("删除成功");
    }
}
