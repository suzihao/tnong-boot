package com.tnong.boot.system.dict.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictTypeVO;

public interface SysDictTypeService {
    PageResult<SysDictTypeVO> pageList(SysDictTypeQueryDTO query, Long tenantId);
    SysDictTypeVO getById(Long id, Long tenantId);
    Long save(SysDictTypeSaveDTO dto, Long tenantId, Long currentUserId);
    void update(SysDictTypeSaveDTO dto, Long tenantId, Long currentUserId);
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
}
