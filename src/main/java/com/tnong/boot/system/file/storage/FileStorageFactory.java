package com.tnong.boot.system.file.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 文件存储工厂
 */
@Component
public class FileStorageFactory {

    @Value("${file.storage.type:local}")
    private String storageType;

    @Autowired
    private Map<String, FileStorage> storageMap;

    public FileStorage getStorage() {
        String beanName = storageType + "FileStorage";
        FileStorage storage = storageMap.get(beanName);
        if (storage == null) {
            throw new IllegalArgumentException("不支持的存储类型: " + storageType);
        }
        return storage;
    }

    public FileStorage getStorage(Integer type) {
        String typeName = type == 0 ? "local" : "minio";
        String beanName = typeName + "FileStorage";
        return storageMap.get(beanName);
    }
}
