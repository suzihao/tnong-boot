package com.tnong.boot.system.dept.domain.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 部门保存/更新参数
 */
@Data
public class SysDeptSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID（更新时必填）
     */
    private Long id;

    /**
     * 父部门ID，0表示根部门
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码，租户内唯一
     */
    private String code;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 部门负责人用户ID
     */
    private Long leaderUserId;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号（更新时必填）
     */
    private Integer version;
}
