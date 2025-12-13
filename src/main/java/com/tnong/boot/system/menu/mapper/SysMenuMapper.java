package com.tnong.boot.system.menu.mapper;

import com.tnong.boot.system.menu.domain.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysMenuMapper {
    SysMenu selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    List<SysMenu> selectList(@Param("tenantId") Long tenantId);
    List<SysMenu> selectByParentId(@Param("parentId") Long parentId, @Param("tenantId") Long tenantId);
    List<SysMenu> selectByRoleId(@Param("roleId") Long roleId, @Param("tenantId") Long tenantId);
    int insert(SysMenu menu);
    int updateByIdWithVersion(SysMenu menu);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
