package com.tnong.boot.system.file.storage;

import com.tnong.boot.common.exception.BusinessException;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * MinIO对象存储实现
 */
@Slf4j
@Component("minioFileStorage")
@ConditionalOnProperty(name = "file.storage.type", havingValue = "minio")
public class MinioFileStorage implements FileStorage {

    @Value("${file.storage.minio.endpoint}")
    private String endpoint;

    @Value("${file.storage.minio.access-key}")
    private String accessKey;

    @Value("${file.storage.minio.secret-key}")
    private String secretKey;

    @Value("${file.storage.minio.bucket}")
    private String bucket;

    private MinioClient minioClient;

    private MinioClient getClient() {
        if (minioClient == null) {
            minioClient = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();
        }
        return minioClient;
    }

    @Override
    public String upload(MultipartFile file, String objectKey) {
        try {
            getClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return getUrl(objectKey);
        } catch (Exception e) {
            log.error("MinIO文件上传失败: {}", objectKey, e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public String upload(InputStream inputStream, String objectKey, String contentType) {
        try {
            getClient().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
            return getUrl(objectKey);
        } catch (Exception e) {
            log.error("MinIO文件流上传失败: {}", objectKey, e);
            throw new BusinessException("文件上传失败");
        }
    }

    @Override
    public byte[] download(String objectKey) {
        try (InputStream stream = getClient().getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        )) {
            return stream.readAllBytes();
        } catch (Exception e) {
            log.error("MinIO文件下载失败: {}", objectKey, e);
            throw new BusinessException("文件下载失败");
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            getClient().removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO文件删除失败: {}", objectKey, e);
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public String getUrl(String objectKey) {
        return endpoint + "/" + bucket + "/" + objectKey;
    }
}
