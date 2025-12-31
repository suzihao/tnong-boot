package com.tnong.boot.system.job.mapper;

import com.tnong.boot.system.job.domain.dto.SysJobQueryDTO;
import com.tnong.boot.system.job.domain.entity.SysJob;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysJobMapper {
    SysJob selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    List<SysJob> selectPageList(@Param("query") SysJobQueryDTO query, @Param("tenantId") Long tenantId);
    Long selectCount(@Param("query") SysJobQueryDTO query, @Param("tenantId") Long tenantId);
    List<SysJob> selectEnabledJobs(@Param("tenantId") Long tenantId);
    int insert(SysJob job);
    int updateByIdWithVersion(SysJob job);
    int updateNextFireTime(@Param("id") Long id, @Param("nextFireTime") java.time.LocalDateTime nextFireTime);
    int updateLastFireTime(@Param("id") Long id, @Param("lastFireTime") java.time.LocalDateTime lastFireTime);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
