package com.tnong.boot.system.file.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.file.domain.dto.FileUploadDTO;
import com.tnong.boot.system.file.domain.dto.SysFileQueryDTO;
import com.tnong.boot.system.file.domain.vo.SysFileVO;
import org.springframework.web.multipart.MultipartFile;

public interface SysFileService {
    PageResult<SysFileVO> pageList(SysFileQueryDTO query, Long tenantId);
    SysFileVO getById(Long id, Long tenantId);
    SysFileVO upload(MultipartFile file, FileUploadDTO dto, Long tenantId, Long currentUserId);
    byte[] download(Long id, Long tenantId);
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
}
