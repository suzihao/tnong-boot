package com.tnong.boot.system.log.service;

import java.util.List;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.log.domain.dto.SysOperLogQueryDTO;
import com.tnong.boot.system.log.domain.entity.SysOperLog;
import com.tnong.boot.system.log.domain.vo.SysOperLogVO;

/**
 * 操作日志服务接口
 */
public interface SysOperLogService {

    /**
     * 保存操作日志
     */
    void save(SysOperLog operLog);

    /**
     * 分页查询操作日志列表
     */
    PageResult<SysOperLogVO> pageList(SysOperLogQueryDTO query, Long tenantId);

    /**
     * 根据ID查询操作日志详情
     */
    SysOperLogVO getById(Long id, Long tenantId);

    /**
     * 批量删除操作日志
     */
    void deleteByIds(List<Long> ids, Long tenantId);

    /**
     * 清空操作日志
     */
    void truncate(Long tenantId);
}
