package com.tnong.boot.system.user.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户返回对象
 */
@Data
public class SysUserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（userId）
     */
    private Long userId;
    
    /**
     * 用户主键ID
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
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
