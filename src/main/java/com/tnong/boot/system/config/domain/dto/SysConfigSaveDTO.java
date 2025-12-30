package com.tnong.boot.system.config.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysConfigSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String configKey;
    private String configName;
    private String configValue;
    private Integer configType;
    private Integer isSystem;
    private Integer status;
    private String remark;
    private Integer version;
}
