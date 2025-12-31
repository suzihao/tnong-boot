package com.tnong.boot.system.job.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobLogQueryDTO;
import com.tnong.boot.system.job.domain.vo.SysJobLogVO;

public interface SysJobLogService {
    PageResult<SysJobLogVO> pageList(SysJobLogQueryDTO query, Long tenantId);
    SysJobLogVO getById(Long id, Long tenantId);
}
