package com.tnong.boot.system.file.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysFileQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private Integer storageType;
    private String bizType;
    private Long bizId;
    private Long current = 1L;
    private Long size = 10L;

    public Long getOffset() {
        return (current - 1) * size;
    }
}
