package com.tnong.boot.system.job.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysJobQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String jobName;
    private String jobGroup;
    private Integer status;
    private Long page = 1L;
    private Long size = 10L;

    public Long getOffset() {
        return (page - 1) * size;
    }
}
