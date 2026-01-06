package com.tnong.boot.framework.security;

import com.tnong.boot.system.menu.domain.vo.MyMenuAndPermVO;
import com.tnong.boot.system.menu.service.SysMenuService;
import com.tnong.boot.system.role.domain.entity.SysRole;
import com.tnong.boot.system.role.mapper.SysRoleMapper;
import com.tnong.boot.system.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限服务
 * 负责加载用户权限和判断超级管理员
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysMenuService sysMenuService;

    /**
     * 超级管理员角色编码
     */
    private static final String SUPER_ADMIN_ROLE_CODE = "ADMIN";

    /**
     * 获取用户权限标识集合
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return 权限标识集合
     */
    public Set<String> getUserPermissions(Long tenantId, Long userId) {
        try {
            MyMenuAndPermVO menuAndPerm = sysMenuService.currentUserMenusAndPerms(tenantId, userId);
            return new HashSet<>(menuAndPerm.getPerms());
        } catch (Exception e) {
            log.error("获取用户权限失败, tenantId={}, userId={}", tenantId, userId, e);
            return new HashSet<>();
        }
    }

    /**
     * 判断用户是否为超级管理员
     * 超级管理员判断标准：拥有角色code='SUPER_ADMIN'的角色
     *
     * @param tenantId 租户ID
     * @param userId   用户ID
     * @return true-是超级管理员，false-不是
     */
    public Boolean isSuperAdmin(Long tenantId, Long userId) {
        try {
            // 1. 查询用户的角色ID列表
            List<Long> roleIds = sysUserRoleMapper.selectRoleIdsByUserId(tenantId, userId);
            if (roleIds == null || roleIds.isEmpty()) {
                return false;
            }

            // 2. 查询角色详情，检查是否有超级管理员角色
            for (Long roleId : roleIds) {
                SysRole role = sysRoleMapper.selectById(roleId, tenantId);
                if (role != null && SUPER_ADMIN_ROLE_CODE.equals(role.getCode())) {
                    log.debug("用户[{}]拥有超级管理员角色", userId);
                  return true;
                }
            }

            return false;
        } catch (Exception e) {
            log.error("判断超级管理员失败, tenantId={}, userId={}", tenantId, userId, e);
            return false;
        }
    }
}
