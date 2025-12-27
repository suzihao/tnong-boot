package com.tnong.boot.system.tenant.mapper;

import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 租户 Mapper 接口
 */
public interface SysTenantMapper {

    /**
     * 根据ID查询租户
     */
    SysTenant selectById(@Param("id") Long id);

    /**
     * 根据业务租户ID查询
     */
    SysTenant selectByTenantId(@Param("tenantId") Long tenantId);

    /**
     * 分页查询租户列表
     */
    List<SysTenant> selectPageList(@Param("query") SysTenantQueryDTO query);

    /**
     * 查询总数
     */
    Long selectCount(@Param("query") SysTenantQueryDTO query);

    /**
     * 插入租户
     */
    int insert(SysTenant tenant);

    /**
     * 更新租户（带乐观锁）
     */
    int updateByIdWithVersion(SysTenant tenant);

    /**
     * 逻辑删除租户（带乐观锁）
     */
    int deleteById(@Param("id") Long id, @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
