package com.tnong.boot.system.role.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色保存/更新参数
 */
@Data
public class SysRoleSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色主键ID（更新时必填）
     */
    private Long id;

    /**
     * 角色编码（如 ADMIN）
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 数据范围：0全部，1本部门，2本部门及子部门，3仅本人
     */
    private Integer dataScope;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态：1启用，0禁用
     */
    private Integer status;

    /**
     * 是否系统内置：1是，0否
     */
    private Integer isSystem;

    /**
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号（更新时必填）
     */
    private Integer version;
}
