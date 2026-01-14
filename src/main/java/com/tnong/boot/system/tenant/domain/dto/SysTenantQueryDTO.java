package com.tnong.boot.system.tenant.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 租户查询参数
 */
@Data
public class SysTenantQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务租户ID
     */
    private Long id;

    /**
     * 租户名称（模糊查询）
     */
    private String name;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Long page = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    public Long getOffset() {
        return (page - 1) * size;
    }
}
