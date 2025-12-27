package com.tnong.boot.system.log.mapper;

import com.tnong.boot.system.log.domain.entity.SysLoginLog;
import org.apache.ibatis.annotations.Param;

/**
 * 登录日志 Mapper
 */
public interface SysLoginLogMapper {
    
    /**
     * 插入登录日志
     */
    int insert(SysLoginLog loginLog);
}
