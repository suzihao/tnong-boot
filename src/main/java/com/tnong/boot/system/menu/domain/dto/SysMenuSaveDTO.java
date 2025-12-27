package com.tnong.boot.system.menu.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单保存/更新参数
 */
@Data
public class SysMenuSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private Integer type;
    private String name;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer sort;
    private Integer visible;
    private Integer status;
    private String remark;
    private Integer version;
}
