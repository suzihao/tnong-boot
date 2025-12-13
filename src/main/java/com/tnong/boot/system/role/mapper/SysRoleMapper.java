package com.tnong.boot.system.role.mapper;

import com.tnong.boot.system.role.domain.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper {
    SysRole selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    SysRole selectByCode(@Param("code") String code, @Param("tenantId") Long tenantId);
    List<SysRole> selectList(@Param("tenantId") Long tenantId);
    int insert(SysRole role);
    int updateByIdWithVersion(SysRole role);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
