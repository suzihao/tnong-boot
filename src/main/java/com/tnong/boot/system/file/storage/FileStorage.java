package com.tnong.boot.system.file.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

/**
 * 文件存储策略接口
 */
public interface FileStorage {

    /**
     * 上传文件
     * @param file 文件
     * @param objectKey 对象键（存储路径）
     * @return 访问URL
     */
    String upload(MultipartFile file, String objectKey);

    /**
     * 上传文件流
     * @param inputStream 输入流
     * @param objectKey 对象键
     * @param contentType 内容类型
     * @return 访问URL
     */
    String upload(InputStream inputStream, String objectKey, String contentType);

    /**
     * 下载文件
     * @param objectKey 对象键
     * @return 文件字节数组
     */
    byte[] download(String objectKey);

    /**
     * 删除文件
     * @param objectKey 对象键
     */
    void delete(String objectKey);

    /**
     * 获取文件访问URL
     * @param objectKey 对象键
     * @return 访问URL
     */
    String getUrl(String objectKey);
}
