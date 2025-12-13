package com.tnong.boot.system.config.mapper;

import com.tnong.boot.system.config.domain.entity.SysConfig;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysConfigMapper {
    SysConfig selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    SysConfig selectByKey(@Param("configKey") String configKey, @Param("tenantId") Long tenantId);
    List<SysConfig> selectList(@Param("tenantId") Long tenantId);
    int insert(SysConfig config);
    int updateByIdWithVersion(SysConfig config);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
