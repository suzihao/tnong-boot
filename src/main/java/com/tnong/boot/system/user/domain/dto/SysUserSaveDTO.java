package com.tnong.boot.system.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户保存/更新参数
 */
@Data
public class SysUserSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（更新时必填）
     */
    private Long id;

    /**
     * 主部门ID
     */
    private Long deptId;

    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码（新增时必填，更新时可选）
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
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号（更新时必填）
     */
    private Integer version;
}
