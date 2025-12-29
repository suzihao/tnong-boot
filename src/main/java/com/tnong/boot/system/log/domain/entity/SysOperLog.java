package com.tnong.boot.system.log.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 操作日志实体
 */
@Data
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 模块标题
     */
    private String module;

    /**
     * 业务类型：0其它，1新增，2修改，3删除，4导入，5导出等
     */
    private Integer businessType;

    /**
     * 请求方式：GET、POST等
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * User-Agent
     */
    private String userAgent;

    /**
     * 调用方法名
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果（可截断）
     */
    private String responseData;

    /**
     * 操作状态：1成功，0失败
     */
    private Integer status;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 耗时毫秒
     */
    private Long costTime;

    /**
     * 操作时间
     */
    private LocalDateTime operTime;
}
