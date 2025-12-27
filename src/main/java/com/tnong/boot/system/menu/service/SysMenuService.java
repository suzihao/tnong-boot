package com.tnong.boot.system.menu.service;

import com.tnong.boot.system.menu.domain.dto.SysMenuSaveDTO;
import com.tnong.boot.system.menu.domain.vo.MyMenuAndPermVO;
import com.tnong.boot.system.menu.domain.vo.SysMenuVO;

import java.util.List;

public interface SysMenuService {

    /**
     * 查询菜单树（根据用户权限过滤）
     */
    List<SysMenuVO> tree(Long tenantId, Long userId);

    /**
     * 新增菜单
     */
    Long save(SysMenuSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 更新菜单
     */
    void update(SysMenuSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 删除菜单
     */
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);

    /**
     * 当前登录用户的菜单与权限
     */
    MyMenuAndPermVO currentUserMenusAndPerms(Long tenantId, Long userId);
}
