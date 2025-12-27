package com.tnong.boot.system.dept.domain.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * 部门查询参数
 */
@Data
public class SysDeptQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门名称（模糊查询）
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 父部门ID（查询直接子部门）
     */
    private Long parentId;
}
