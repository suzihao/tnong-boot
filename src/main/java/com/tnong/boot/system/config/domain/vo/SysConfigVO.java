package com.tnong.boot.system.config.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysConfigVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private String configKey;
    private String configName;
    private String configValue;
    private Integer configType;
    private Integer isSystem;
    private Integer status;
    private String remark;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
