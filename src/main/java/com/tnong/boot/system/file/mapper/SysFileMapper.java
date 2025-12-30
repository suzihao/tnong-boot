package com.tnong.boot.system.file.mapper;

import com.tnong.boot.system.file.domain.dto.SysFileQueryDTO;
import com.tnong.boot.system.file.domain.entity.SysFile;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysFileMapper {
    SysFile selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    SysFile selectByMd5(@Param("md5") String md5, @Param("tenantId") Long tenantId);
    List<SysFile> selectPageList(@Param("query") SysFileQueryDTO query, @Param("tenantId") Long tenantId);
    Long selectCount(@Param("query") SysFileQueryDTO query, @Param("tenantId") Long tenantId);
    int insert(SysFile file);
    int updateByIdWithVersion(SysFile file);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
