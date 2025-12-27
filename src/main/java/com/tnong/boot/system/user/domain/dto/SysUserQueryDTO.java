package com.tnong.boot.system.user.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户查询参数
 */
@Data
public class SysUserQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名（模糊查询）
     */
    private String username;

    /**
     * 昵称（模糊查询）
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Long current = 1L;

    /**
     * 每页大小
     */
    private Long size = 10L;

    /**
     * 计算 LIMIT 偏移量
     */
    public Long getOffset() {
        return (current - 1) * size;
    }
}
