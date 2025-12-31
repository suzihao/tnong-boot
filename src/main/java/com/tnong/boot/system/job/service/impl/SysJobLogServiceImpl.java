package com.tnong.boot.system.job.service.impl;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobLogQueryDTO;
import com.tnong.boot.system.job.domain.entity.SysJobLog;
import com.tnong.boot.system.job.domain.vo.SysJobLogVO;
import com.tnong.boot.system.job.mapper.SysJobLogMapper;
import com.tnong.boot.system.job.service.SysJobLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysJobLogServiceImpl implements SysJobLogService {

    private final SysJobLogMapper sysJobLogMapper;

    @Override
    public PageResult<SysJobLogVO> pageList(SysJobLogQueryDTO query, Long tenantId) {
        List<SysJobLog> entities = sysJobLogMapper.selectPageList(query, tenantId);
        Long total = sysJobLogMapper.selectCount(query, tenantId);

        List<SysJobLogVO> records = entities.stream()
                .map(entity -> {
                    SysJobLogVO vo = new SysJobLogVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getCurrent(), query.getSize());
    }

    @Override
    public SysJobLogVO getById(Long id, Long tenantId) {
        SysJobLog jobLog = sysJobLogMapper.selectById(id, tenantId);
        if (jobLog == null) {
            throw new BusinessException("任务日志不存在");
        }
        SysJobLogVO vo = new SysJobLogVO();
        BeanUtils.copyProperties(jobLog, vo);
        return vo;
    }
}
