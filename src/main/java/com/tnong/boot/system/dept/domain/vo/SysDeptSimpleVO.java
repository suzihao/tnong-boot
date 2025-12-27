package com.tnong.boot.system.dept.domain.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 部门简单返回对象（用于下拉框等场景）
 */
@Data
public class SysDeptSimpleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;
}
