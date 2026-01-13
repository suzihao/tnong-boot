package com.tnong.boot.system.file.controller;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.common.web.Result;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.file.domain.dto.FileUploadDTO;
import com.tnong.boot.system.file.domain.dto.SysFileDeleteDTO;
import com.tnong.boot.system.file.domain.dto.SysFileQueryDTO;
import com.tnong.boot.system.file.domain.vo.SysFileVO;
import com.tnong.boot.system.file.service.SysFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/system/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService sysFileService;

    @GetMapping("/page")
    public Result<PageResult<SysFileVO>> page(SysFileQueryDTO query) {
        Long tenantId = UserContext.getTenantId();
        PageResult<SysFileVO> pageResult = sysFileService.pageList(query, tenantId);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    public Result<SysFileVO> getById(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysFileVO file = sysFileService.getById(id, tenantId);
        return Result.success(file);
    }

    @PostMapping("/upload")
    public Result<SysFileVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bizType", required = false) String bizType,
            @RequestParam(value = "bizId", required = false) Long bizId,
            @RequestParam(value = "remark", required = false) String remark) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();

        FileUploadDTO dto = new FileUploadDTO();
        dto.setBizType(bizType);
        dto.setBizId(bizId);
        dto.setRemark(remark);

        SysFileVO fileVO = sysFileService.upload(file, dto, tenantId, currentUserId);
        return Result.success("上传成功", fileVO);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Long tenantId = UserContext.getTenantId();
        SysFileVO fileVO = sysFileService.getById(id, tenantId);
        byte[] data = sysFileService.download(id, tenantId);

        String encodedFilename = URLEncoder.encode(fileVO.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename)
                .body(data);
    }

    @PostMapping("/delete")
    public Result<Void> delete(@RequestBody SysFileDeleteDTO dto) {
        Long tenantId = UserContext.getTenantId();
        Long currentUserId = UserContext.getUserId();
        sysFileService.delete(dto.getId(), tenantId, dto.getVersion(), currentUserId);
        return Result.success("删除成功", null);
    }
}
