package com.tnong.boot.system.role.mapper;

import com.tnong.boot.system.role.domain.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMenuMapper {

    List<Long> selectMenuIdsByRoleId(@Param("tenantId") Long tenantId,
                                     @Param("roleId") Long roleId);

    int insertBatch(@Param("tenantId") Long tenantId,
                    @Param("roleId") Long roleId,
                    @Param("menuIds") List<Long> menuIds,
                    @Param("createdUser") Long createdUser);

    int deleteByRoleId(@Param("tenantId") Long tenantId,
                       @Param("roleId") Long roleId,
                       @Param("updatedUser") Long updatedUser);
}
