package com.tnong.boot.system.log.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnong.boot.common.constant.BusinessType;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.log.domain.dto.SysOperLogQueryDTO;
import com.tnong.boot.system.log.domain.entity.SysOperLog;
import com.tnong.boot.system.log.domain.vo.SysOperLogVO;
import com.tnong.boot.system.log.mapper.SysOperLogMapper;
import com.tnong.boot.system.log.service.SysOperLogService;

import lombok.RequiredArgsConstructor;

/**
 * 操作日志服务实现
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl implements SysOperLogService {

    private final SysOperLogMapper sysOperLogMapper;

    @Override
    @Async
    public void save(SysOperLog operLog) {
        sysOperLogMapper.insert(operLog);
    }

    @Override
    public PageResult<SysOperLogVO> pageList(SysOperLogQueryDTO query, Long tenantId) {
        List<SysOperLog> entities = sysOperLogMapper.selectPageList(query, tenantId);
        Long total = sysOperLogMapper.selectCount(query, tenantId);

        // Entity 转 VO
        List<SysOperLogVO> records = entities.stream()
                .map(entity -> {
                    SysOperLogVO vo = new SysOperLogVO();
                    BeanUtils.copyProperties(entity, vo);
                    // 设置业务类型描述
                    vo.setBusinessTypeDesc(getBusinessTypeDesc(entity.getBusinessType()));
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysOperLogVO getById(Long id, Long tenantId) {
        SysOperLog entity = sysOperLogMapper.selectById(id, tenantId);
        if (entity == null) {
            throw new BusinessException("操作日志不存在");
        }

        SysOperLogVO vo = new SysOperLogVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setBusinessTypeDesc(getBusinessTypeDesc(entity.getBusinessType()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> ids, Long tenantId) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("删除ID列表不能为空");
        }
        sysOperLogMapper.deleteByIds(ids, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void truncate(Long tenantId) {
        sysOperLogMapper.truncate(tenantId);
    }

    /**
     * 获取业务类型描述
     */
    private String getBusinessTypeDesc(Integer businessType) {
        if (businessType == null) {
            return "";
        }
        for (BusinessType type : BusinessType.values()) {
            if (type.getCode().equals(businessType)) {
                return type.getDesc();
            }
        }
        return "";
    }
}
