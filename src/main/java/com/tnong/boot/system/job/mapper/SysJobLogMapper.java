package com.tnong.boot.system.job.mapper;

import com.tnong.boot.system.job.domain.dto.SysJobLogQueryDTO;
import com.tnong.boot.system.job.domain.entity.SysJobLog;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysJobLogMapper {
    SysJobLog selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    List<SysJobLog> selectPageList(@Param("query") SysJobLogQueryDTO query, @Param("tenantId") Long tenantId);
    Long selectCount(@Param("query") SysJobLogQueryDTO query, @Param("tenantId") Long tenantId);
    int insert(SysJobLog jobLog);
}
