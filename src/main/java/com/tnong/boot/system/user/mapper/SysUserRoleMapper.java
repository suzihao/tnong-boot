package com.tnong.boot.system.user.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserRoleMapper {

    List<Long> selectRoleIdsByUserId(@Param("tenantId") Long tenantId,
                                     @Param("userId") Long userId);

    int insertBatch(@Param("tenantId") Long tenantId,
                    @Param("userId") Long userId,
                    @Param("roleIds") List<Long> roleIds,
                    @Param("createdUser") Long createdUser);

    int deleteByUserId(@Param("tenantId") Long tenantId,
                       @Param("userId") Long userId,
                       @Param("updatedUser") Long updatedUser);
}
