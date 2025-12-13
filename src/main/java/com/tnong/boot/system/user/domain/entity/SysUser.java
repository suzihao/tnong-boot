package com.tnong.boot.system.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 主部门ID
     */
    private Long deptId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码（BCrypt）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 删除标记：0未删除，1已删除
     */
    private Integer deleteFlag;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 备注
     */
    private String remark;

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
