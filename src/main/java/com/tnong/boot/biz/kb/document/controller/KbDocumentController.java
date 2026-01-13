package com.tnong.boot.biz.kb.document.controller;

import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.biz.kb.document.domain.dto.KbDocumentDTO;
import com.tnong.boot.biz.kb.document.domain.entity.KbDocument;
import com.tnong.boot.biz.kb.document.mapper.KbDocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 知识文档控制器
 */
@RestController
@RequestMapping("/api/kb/document")
@RequiredArgsConstructor
public class KbDocumentController {

    private final KbDocumentMapper kbDocumentMapper;

    /**
     * 根据目录ID获取文档列表
     */
    @GetMapping("/list")
    public Result<List<KbDocument>> getListByCategoryId(@RequestParam Long categoryId) {
        Long tenantId = UserContext.getTenantId();
        List<KbDocument> list = kbDocumentMapper.selectListByCategoryId(categoryId, tenantId);
        return Result.success(list);
    }

    /**
     * 搜索文档
     */
    @GetMapping("/search")
    public Result<List<KbDocument>> search(@RequestParam String keyword) {
        Long tenantId = UserContext.getTenantId();
        List<KbDocument> list = kbDocumentMapper.searchByKeyword(keyword, tenantId);
        return Result.success(list);
    }

    /**
     * 获取文档详情
     */
    @GetMapping("/{id}")
    public Result<KbDocument> getById(@PathVariable Long id) {
        KbDocument document = kbDocumentMapper.selectById(id);
        // 增加浏览次数
        kbDocumentMapper.increaseViewCount(id);
        return Result.success(document);
    }

    /**
     * 创建文档
     */
    @PostMapping
    public Result<Long> create(@RequestBody KbDocumentDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserBizId = UserContext.getUserId();
    
        KbDocument document = new KbDocument();
        document.setTenantId(tenantId);
        document.setCategoryId(dto.getCategoryId());
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setTags(dto.getTags());
        document.setSort(dto.getSort() != null ? dto.getSort() : 0);
        document.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        document.setCreatedUser(currentUserBizId);
        document.setUpdatedUser(currentUserBizId);
    
        kbDocumentMapper.insert(document);
        return Result.success("创建成功", document.getId());
    }

    /**
     * 更新文档
     */
    @PostMapping("/update")
    public Result<String> update(@RequestBody KbDocumentDTO dto) {
        Long currentUserBizId = UserContext.getUserId();

        KbDocument document = new KbDocument();
        document.setId(dto.getId());
        document.setCategoryId(dto.getCategoryId());
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setTags(dto.getTags());
        document.setSort(dto.getSort());
        document.setStatus(dto.getStatus());
        document.setUpdatedUser(currentUserBizId);
        document.setVersion(dto.getVersion());

        int rows = kbDocumentMapper.updateById(document);
        if (rows == 0) {
            return Result.fail("更新失败，数据可能已被修改");
        }
        return Result.success("更新成功");
    }

    /**
     * 删除文档
     */
    @PostMapping("/delete")
    public Result<String> delete(@RequestBody KbDocumentDTO dto) {
        Long currentUserBizId = UserContext.getUserId();
        kbDocumentMapper.deleteById(dto.getId(), currentUserBizId);
        return Result.success("删除成功");
    }
}
