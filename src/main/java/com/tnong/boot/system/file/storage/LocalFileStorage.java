package com.tnong.boot.system.file.storage;

import com.tnong.boot.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储实现
 */
@Slf4j
@Component("localFileStorage")
public class LocalFileStorage implements FileStorage {

    @Value("${file.storage.local.path:/data/files}")
    private String basePath;

    @Value("${file.storage.local.url-prefix:http://localhost:8080/files}")
    private String urlPrefix;

    @Override
    public String upload(MultipartFile file, String objectKey) {
        try {
            Path filePath = Paths.get(basePath, objectKey);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath.toFile());
            return getUrl(objectKey);
        } catch (Exception e) {
            log.error("本地文件上传失败: {}", objectKey, e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public String upload(InputStream inputStream, String objectKey, String contentType) {
        try {
            Path filePath = Paths.get(basePath, objectKey);
            Files.createDirectories(filePath.getParent());
            try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            return getUrl(objectKey);
        } catch (Exception e) {
            log.error("本地文件流上传失败: {}", objectKey, e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public byte[] download(String objectKey) {
        try {
            Path filePath = Paths.get(basePath, objectKey);
            return Files.readAllBytes(filePath);
        } catch (Exception e) {
            log.error("本地文件下载失败: {}", objectKey, e);
            throw new BusinessException("文件下载失败");
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            Path filePath = Paths.get(basePath, objectKey);
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            log.error("本地文件删除失败: {}", objectKey, e);
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public String getUrl(String objectKey) {
        return urlPrefix + "/" + objectKey;
    }
}
