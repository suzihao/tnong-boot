package com.tnong.boot.system.log.domain.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 操作日志查询参数
 */
@Data
public class SysOperLogQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模块名称
     */
    private String module;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 业务类型
     */
    private Integer businessType;

    /**
     * 操作状态：1成功，0失败
     */
    private Integer status;

    /**
     * 开始时间
     */
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 当前页码
     */
    private Long page = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 计算 LIMIT 偏移量
     */
    public Long getOffset() {
        return (page - 1) * size;
    }
}
