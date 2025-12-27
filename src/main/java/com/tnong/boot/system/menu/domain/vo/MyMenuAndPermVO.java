package com.tnong.boot.system.menu.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 当前用户菜单与权限
 */
@Data
public class MyMenuAndPermVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单树
     */
    private List<SysMenuVO> menus;

    /**
     * 按钮权限标识集合
     */
    private List<String> perms;
}
