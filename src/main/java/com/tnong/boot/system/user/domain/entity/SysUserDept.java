package com.tnong.boot.system.user.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * 用户-部门关联实体（多部门支持）
 */
@Data
public class SysUserDept implements Serializable {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 是否主部门：1是，0否
     */
    private Integer mainFlag;

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
     * 乐观锁版本号
     */
    private Integer version;
}
