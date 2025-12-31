package com.tnong.boot.system.job.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobQueryDTO;
import com.tnong.boot.system.job.domain.dto.SysJobSaveDTO;
import com.tnong.boot.system.job.domain.vo.SysJobVO;

public interface SysJobService {
    PageResult<SysJobVO> pageList(SysJobQueryDTO query, Long tenantId);
    SysJobVO getById(Long id, Long tenantId);
    Long save(SysJobSaveDTO dto, Long tenantId, Long currentUserId);
    void update(SysJobSaveDTO dto, Long tenantId, Long currentUserId);
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);
    void changeStatus(Long id, Integer status, Long tenantId, Long currentUserId);
    void runOnce(Long id, Long tenantId);
}
