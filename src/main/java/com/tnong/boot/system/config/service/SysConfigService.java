package com.tnong.boot.system.config.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.config.domain.dto.SysConfigQueryDTO;
import com.tnong.boot.system.config.domain.dto.SysConfigSaveDTO;
import com.tnong.boot.system.config.domain.vo.SysConfigVO;

public interface SysConfigService {
    PageResult<SysConfigVO> pageList(SysConfigQueryDTO query, Long tenantId);
    SysConfigVO getById(Long id, Long tenantId);
    String getValueByKey(String configKey, Long tenantId);
    Long save(SysConfigSaveDTO dto, Long tenantId, Long currentUserId);
    void update(SysConfigSaveDTO dto, Long tenantId, Long currentUserId);
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
}
