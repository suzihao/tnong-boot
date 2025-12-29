package com.tnong.boot.system.log.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.tnong.boot.system.log.domain.dto.SysOperLogQueryDTO;
import com.tnong.boot.system.log.domain.entity.SysOperLog;

/**
 * 操作日志 Mapper 接口
 */
public interface SysOperLogMapper {

    /**
     * 插入操作日志
     */
    int insert(SysOperLog operLog);

    /**
     * 分页查询操作日志列表
     */
    List<SysOperLog> selectPageList(@Param("query") SysOperLogQueryDTO query, @Param("tenantId") Long tenantId);

    /**
     * 查询总数
     */
    Long selectCount(@Param("query") SysOperLogQueryDTO query, @Param("tenantId") Long tenantId);

    /**
     * 根据ID查询
     */
    SysOperLog selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<Long> ids, @Param("tenantId") Long tenantId);

    /**
     * 清空日志
     */
    int truncate(@Param("tenantId") Long tenantId);
}
