package com.tnong.boot.system.job.domain.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysJobLogQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long jobId;
    private String jobName;
    private Integer status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long page = 1L;
    private Long size = 10L;

    public Long getOffset() {
        return (page - 1) * size;
    }
}
