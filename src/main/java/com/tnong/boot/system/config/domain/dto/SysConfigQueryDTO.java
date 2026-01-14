package com.tnong.boot.system.config.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysConfigQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String configKey;
    private String configName;
    private Integer configType;
    private Integer status;
    private Long page = 1L;
    private Long size = 10L;

    public Long getOffset() {
        return (page - 1) * size;
    }
}
