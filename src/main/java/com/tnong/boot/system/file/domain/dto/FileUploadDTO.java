package com.tnong.boot.system.file.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class FileUploadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bizType;
    private Long bizId;
    private String remark;
}
