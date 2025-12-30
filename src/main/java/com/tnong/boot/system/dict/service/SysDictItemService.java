package com.tnong.boot.system.dict.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictItemQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictItemSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictItemVO;

import java.util.List;

public interface SysDictItemService {
    PageResult<SysDictItemVO> pageList(SysDictItemQueryDTO query, Long tenantId);
    SysDictItemVO getById(Long id, Long tenantId);
    List<SysDictItemVO> listByTypeCode(String typeCode, Long tenantId);
    Long save(SysDictItemSaveDTO dto, Long tenantId, Long currentUserId);
    void update(SysDictItemSaveDTO dto, Long tenantId, Long currentUserId);
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
}
