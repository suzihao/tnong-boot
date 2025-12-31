package com.tnong.boot.system.job.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysJobSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String jobName;
    private String jobGroup;
    private String invokeTarget;
    private String cronExpression;
    private Integer misfirePolicy;
    private Integer concurrent;
    private Integer status;
    private String remark;
    private Integer version;
}
