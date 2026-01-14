package com.tnong.boot.system.job.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobQueryDTO;
import com.tnong.boot.system.job.domain.dto.SysJobSaveDTO;
import com.tnong.boot.system.job.domain.entity.SysJob;
import com.tnong.boot.system.job.domain.vo.SysJobVO;
import com.tnong.boot.system.job.mapper.SysJobMapper;
import com.tnong.boot.system.job.scheduler.ScheduleManager;
import com.tnong.boot.system.job.service.SysJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysJobServiceImpl implements SysJobService {

    private final SysJobMapper sysJobMapper;
    private final ScheduleManager scheduleManager;

    @Override
    public PageResult<SysJobVO> pageList(SysJobQueryDTO query, Long tenantId) {
        List<SysJob> entities = sysJobMapper.selectPageList(query, tenantId);
        Long total = sysJobMapper.selectCount(query, tenantId);

        List<SysJobVO> records = entities.stream()
                .map(entity -> {
                    SysJobVO vo = new SysJobVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysJobVO getById(Long id, Long tenantId) {
        SysJob job = sysJobMapper.selectById(id, tenantId);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        SysJobVO vo = new SysJobVO();
        BeanUtils.copyProperties(job, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysJobSaveDTO dto, Long tenantId, Long currentUserId) {
        SysJob job = new SysJob();
        BeanUtils.copyProperties(dto, job);
        job.setTenantId(tenantId);
        job.setCreatedUser(currentUserId);
        job.setUpdatedUser(currentUserId);
        job.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysJobMapper.insert(job);
        if (rows == 0) {
            throw new BusinessException("新增任务失败");
        }

        if (job.getStatus() == 1) {
            scheduleManager.startJob(job);
        }

        return job.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysJobSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("任务ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysJob dbJob = sysJobMapper.selectById(dto.getId(), tenantId);
        if (dbJob == null) {
            throw new BusinessException("任务不存在或已删除");
        }

        SysJob job = new SysJob();
        BeanUtils.copyProperties(dto, job);
        job.setTenantId(tenantId);
        job.setUpdatedUser(currentUserId);

        int rows = sysJobMapper.updateByIdWithVersion(job);
        if (rows == 0) {
            throw new OptimisticLockException();
        }

        scheduleManager.restartJob(job);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }

        scheduleManager.stopJob(id);

        int rows = sysJobMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status, Long tenantId, Long currentUserId) {
        SysJob job = sysJobMapper.selectById(id, tenantId);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }

        job.setStatus(status);
        job.setUpdatedUser(currentUserId);
        sysJobMapper.updateByIdWithVersion(job);

        if (status == 1) {
            scheduleManager.startJob(job);
        } else {
            scheduleManager.stopJob(id);
        }
    }

    @Override
    public void runOnce(Long id, Long tenantId) {
        SysJob job = sysJobMapper.selectById(id, tenantId);
        if (job == null) {
            throw new BusinessException("任务不存在");
        }
        scheduleManager.runOnce(job);
    }
}
