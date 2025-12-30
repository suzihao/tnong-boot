package com.tnong.boot.system.file.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private String fileName;
    private String fileSuffix;
    private String contentType;
    private Long fileSize;
    private Integer storageType;
    private String bucket;
    private String objectKey;
    private String url;
    private String bizType;
    private Long bizId;
    private String md5;
    private Integer status;
    private String remark;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
