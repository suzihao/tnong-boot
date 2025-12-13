package com.tnong.boot.system.dict.mapper;

import com.tnong.boot.system.dict.domain.dto.SysDictItemQueryDTO;
import com.tnong.boot.system.dict.domain.entity.SysDictItem;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysDictItemMapper {
    SysDictItem selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);
    List<SysDictItem> selectByTypeCode(@Param("typeCode") String typeCode, @Param("tenantId") Long tenantId);
    List<SysDictItem> selectPageList(@Param("query") SysDictItemQueryDTO query, @Param("tenantId") Long tenantId);
    Long selectCount(@Param("query") SysDictItemQueryDTO query, @Param("tenantId") Long tenantId);
    int insert(SysDictItem dictItem);
    int updateByIdWithVersion(SysDictItem dictItem);
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
