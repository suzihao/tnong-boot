package com.tnong.boot.system.job.domain.entity;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysJobLog implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private Long jobId;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private Integer status;
    private String errorMsg;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long costTime;
}
