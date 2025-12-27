package com.tnong.boot.system.tenant.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户保存/更新参数
 */
@Data
public class SysTenantSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID（更新时必填）
     */
    private Long id;

    /**
     * 业务租户ID（更新时必填）
     */
    private Long tenantCode;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 联系人邮箱
     */
    private String contactEmail;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 到期时间
     */
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号（更新时必填）
     */
    private Integer version;
}
