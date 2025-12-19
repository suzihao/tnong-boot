package com.tnong.boot.system.menu.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.menu.domain.dto.SysMenuSaveDTO;
import com.tnong.boot.system.menu.domain.entity.SysMenu;
import com.tnong.boot.system.menu.domain.vo.MyMenuAndPermVO;
import com.tnong.boot.system.menu.domain.vo.SysMenuVO;
import com.tnong.boot.system.menu.mapper.SysMenuMapper;
import com.tnong.boot.system.menu.service.SysMenuService;
import com.tnong.boot.system.role.mapper.SysRoleMenuMapper;
import com.tnong.boot.system.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper sysMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenuVO> tree(Long tenantId, Long userId) {
        // 1. 查询用户的角色
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(tenantId, userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 通过角色查询菜单ID
        Set<Long> menuIdSet = new HashSet<>();
        for (Long roleId : roleIds) {
            List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(tenantId, roleId);
            if (menuIds != null) {
                menuIdSet.addAll(menuIds);
            }
        }
        if (menuIdSet.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. 查询菜单详情
        List<Long> menuIdList = new ArrayList<>(menuIdSet);
        List<SysMenu> menus = sysMenuMapper.selectByIds(menuIdList, tenantId);

        // 4. 构建树（包含所有类型：目录/菜单/按钮）
        return buildTree(menus, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysMenuSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() != null) {
            throw new BusinessException("新增菜单时不允许携带ID");
        }
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        menu.setTenantId(tenantId);

        // 按类型处理权限标识：仅按钮(type=3)需要唯一的 perms，其它类型不写 perms
        Integer type = menu.getType();
        if (type == null) {
            throw new BusinessException("菜单类型不能为空");
        }
        if (!Objects.equals(type, 3)) {
            // 目录/菜单不占用权限标识
            menu.setPerms(null);
        } else {
            if (menu.getPerms() == null || menu.getPerms().trim().isEmpty()) {
                throw new BusinessException("按钮必须配置权限标识");
            }
            menu.setPerms(menu.getPerms().trim());
        }

        menu.setDeleteFlag(CommonConstant.DELETE_FLAG_NORMAL);
        menu.setCreatedUser(currentUserId);
        menu.setUpdatedUser(currentUserId);
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(CommonConstant.STATUS_ENABLE);
        }
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        int rows = sysMenuMapper.insert(menu);
        if (rows == 0) {
            throw new BusinessException("新增菜单失败");
        }
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysMenuSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("菜单ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }
        SysMenu dbMenu = sysMenuMapper.selectById(dto.getId(), tenantId);
        if (dbMenu == null) {
            throw new BusinessException("菜单不存在或已删除");
        }
        SysMenu menu = new SysMenu();
        BeanUtils.copyProperties(dto, menu);
        menu.setTenantId(tenantId);

        // 按类型处理权限标识规则同新增
        Integer type = menu.getType();
        if (type == null) {
            throw new BusinessException("菜单类型不能为空");
        }
        if (!Objects.equals(type, 3)) {
            menu.setPerms(null);
        } else {
            if (menu.getPerms() == null || menu.getPerms().trim().isEmpty()) {
                throw new BusinessException("按钮必须配置权限标识");
            }
            menu.setPerms(menu.getPerms().trim());
        }

        menu.setUpdatedUser(currentUserId);
        int rows = sysMenuMapper.updateByIdWithVersion(menu);
        if (rows == 0) {
            throw new OptimisticLockException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }
        int rows = sysMenuMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }

    @Override
    public MyMenuAndPermVO currentUserMenusAndPerms(Long tenantId, Long userId) {
        // 1. 查询用户的角色
        List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(tenantId, userId);
        if (roleIds == null || roleIds.isEmpty()) {
            MyMenuAndPermVO vo = new MyMenuAndPermVO();
            vo.setMenus(Collections.emptyList());
            vo.setPerms(Collections.emptyList());
            return vo;
        }

        // 2. 通过角色查询菜单ID
        Set<Long> menuIdSet = new HashSet<>();
        for (Long roleId : roleIds) {
            List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(tenantId, roleId);
            if (menuIds != null) {
                menuIdSet.addAll(menuIds);
            }
        }
        if (menuIdSet.isEmpty()) {
            MyMenuAndPermVO vo = new MyMenuAndPermVO();
            vo.setMenus(Collections.emptyList());
            vo.setPerms(Collections.emptyList());
            return vo;
        }

        // 3. 查询菜单详情
        List<Long> menuIdList = new ArrayList<>(menuIdSet);
        List<SysMenu> menus = sysMenuMapper.selectByIds(menuIdList, tenantId);

        // 4. 过滤启用且可见的目录/菜单，构建树
        List<SysMenu> enabledMenus = menus.stream()
                .filter(m -> Objects.equals(m.getStatus(), CommonConstant.STATUS_ENABLE))
                .collect(Collectors.toList());
        List<SysMenuVO> menuTree = buildTree(enabledMenus, 0L);

        // 5. 收集按钮权限标识
        List<String> perms = menus.stream()
                .filter(m -> Objects.equals(m.getType(), 3))
                .map(SysMenu::getPerms)
                .filter(p -> p != null && !p.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        MyMenuAndPermVO vo = new MyMenuAndPermVO();
        vo.setMenus(menuTree);
        vo.setPerms(perms);
        return vo;
    }

    private List<SysMenuVO> buildTree(List<SysMenu> menus, Long rootParentId) {
        Map<Long, SysMenuVO> idMap = new HashMap<>();
        for (SysMenu menu : menus) {
            SysMenuVO vo = new SysMenuVO();
            BeanUtils.copyProperties(menu, vo);
            vo.setChildren(new ArrayList<>());
            idMap.put(menu.getId(), vo);
        }

        List<SysMenuVO> roots = new ArrayList<>();
        for (SysMenu menu : menus) {
            SysMenuVO vo = idMap.get(menu.getId());
            Long parentId = menu.getParentId();
            if (parentId == null || Objects.equals(parentId, rootParentId)) {
                roots.add(vo);
            } else {
                SysMenuVO parent = idMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    roots.add(vo);
                }
            }
        }

        // 排序
        roots.sort(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        for (SysMenuVO root : roots) {
            if (root.getChildren() != null && !root.getChildren().isEmpty()) {
                root.getChildren().sort(Comparator.comparing(SysMenuVO::getSort, Comparator.nullsLast(Integer::compareTo)));
            }
        }
        return roots;
    }
}
