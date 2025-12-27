package com.tnong.boot.system.dict.mapper;

import com.tnong.boot.system.dict.domain.dto.SysDictTypeQueryDTO;
import com.tnong.boot.system.dict.domain.entity.SysDictType;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysDictTypeMapper {
    SysDictType selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    SysDictType selectByTypeCode(@Param("typeCode") String typeCode, @Param("tenantId") Long tenantId);
    List<SysDictType> selectPageList(@Param("query") SysDictTypeQueryDTO query, @Param("tenantId") Long tenantId);
    Long selectCount(@Param("query") SysDictTypeQueryDTO query, @Param("tenantId") Long tenantId);
    int insert(SysDictType dictType);
    int updateByIdWithVersion(SysDictType dictType);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
