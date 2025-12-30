package com.tnong.boot.system.file.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.file.domain.dto.FileUploadDTO;
import com.tnong.boot.system.file.domain.dto.SysFileQueryDTO;
import com.tnong.boot.system.file.domain.entity.SysFile;
import com.tnong.boot.system.file.domain.vo.SysFileVO;
import com.tnong.boot.system.file.mapper.SysFileMapper;
import com.tnong.boot.system.file.service.SysFileService;
import com.tnong.boot.system.file.storage.FileStorage;
import com.tnong.boot.system.file.storage.FileStorageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl implements SysFileService {

    private final SysFileMapper sysFileMapper;
    private final FileStorageFactory fileStorageFactory;

    @Value("${file.storage.type:local}")
    private String storageType;

    @Override
    public PageResult<SysFileVO> pageList(SysFileQueryDTO query, Long tenantId) {
        List<SysFile> entities = sysFileMapper.selectPageList(query, tenantId);
        Long total = sysFileMapper.selectCount(query, tenantId);

        List<SysFileVO> records = entities.stream()
                .map(entity -> {
                    SysFileVO vo = new SysFileVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getCurrent(), query.getSize());
    }

    @Override
    public SysFileVO getById(Long id, Long tenantId) {
        SysFile file = sysFileMapper.selectById(id, tenantId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }
        SysFileVO vo = new SysFileVO();
        BeanUtils.copyProperties(file, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVO upload(MultipartFile file, FileUploadDTO dto, Long tenantId, Long currentUserId) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            String md5 = DigestUtils.md5DigestAsHex(file.getInputStream());

            SysFile existFile = sysFileMapper.selectByMd5(md5, tenantId);
            if (existFile != null) {
                log.info("文件秒传: {}", existFile.getFileName());
                SysFileVO vo = new SysFileVO();
                BeanUtils.copyProperties(existFile, vo);
                return vo;
            }

            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String objectKey = generateObjectKey(suffix);

            FileStorage storage = fileStorageFactory.getStorage();
            String url = storage.upload(file, objectKey);

            SysFile sysFile = new SysFile();
            sysFile.setTenantId(tenantId);
            sysFile.setFileName(originalFilename);
            sysFile.setFileSuffix(suffix);
            sysFile.setContentType(file.getContentType());
            sysFile.setFileSize(file.getSize());
            sysFile.setStorageType("local".equals(storageType) ? 0 : 1);
            sysFile.setBucket(null);
            sysFile.setObjectKey(objectKey);
            sysFile.setUrl(url);
            sysFile.setBizType(dto.getBizType());
            sysFile.setBizId(dto.getBizId());
            sysFile.setMd5(md5);
            sysFile.setStatus(CommonConstant.STATUS_ENABLE);
            sysFile.setRemark(dto.getRemark());
            sysFile.setCreatedUser(currentUserId);
            sysFile.setUpdatedUser(currentUserId);

            int rows = sysFileMapper.insert(sysFile);
            if (rows == 0) {
                throw new BusinessException("文件记录保存失败");
            }

            SysFileVO vo = new SysFileVO();
            BeanUtils.copyProperties(sysFile, vo);
            return vo;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public byte[] download(Long id, Long tenantId) {
        SysFile file = sysFileMapper.selectById(id, tenantId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        FileStorage storage = fileStorageFactory.getStorage(file.getStorageType());
        return storage.download(file.getObjectKey());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysFile file = sysFileMapper.selectById(id, tenantId);
        if (file == null) {
            throw new BusinessException("文件不存在");
        }

        int rows = sysFileMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }

        try {
            FileStorage storage = fileStorageFactory.getStorage(file.getStorageType());
            storage.delete(file.getObjectKey());
        } catch (Exception e) {
            log.error("物理文件删除失败: {}", file.getObjectKey(), e);
        }
    }

    private String generateObjectKey(String suffix) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return date + "/" + uuid + suffix;
    }
}
