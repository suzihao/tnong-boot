package com.tnong.boot.system.dept.domain.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 部门树形结构返回对象
 */
@Data
public class SysDeptTreeVO implements Serializable {

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
     * 部门负责人姓名
     */
    private String leaderUserName;

    /**
     * 子部门列表
     */
    private List<SysDeptTreeVO> children = new ArrayList<>();
}
