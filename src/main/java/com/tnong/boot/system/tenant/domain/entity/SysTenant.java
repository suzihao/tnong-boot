package com.tnong.boot.system.tenant.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户实体
 */
@Data
public class SysTenant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户ID（物理主键）
     */
    private Long id;

    /**
     * 业务租户ID（雪花算法生成，对外使用，唯一标识）
     */
    private Long tenantId;

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
     * 到期时间，NULL表示无限期
     */
    private LocalDateTime expireTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标记：0未删除，1已删除
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 创建人ID
     */
    private Long createdUser;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 更新人ID
     */
    private Long updatedUser;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
