package com.tnong.boot.system.tenant.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.dto.SysTenantSaveDTO;
import com.tnong.boot.system.tenant.domain.vo.SysTenantVO;

/**
 * 租户服务接口
 */
public interface SysTenantService {

    /**
     * 分页查询租户列表
     */
    PageResult<SysTenantVO> pageList(SysTenantQueryDTO query);

    /**
     * 根据ID查询租户详情
     */
    SysTenantVO getById(Long id);

    /**
     * 新增租户
     */
    Long save(SysTenantSaveDTO dto, Long currentUserId);

    /**
     * 更新租户
     */
    void update(SysTenantSaveDTO dto, Long currentUserId);

    /**
     * 删除租户
     */
    void delete(Long id, Integer version, Long currentUserId);
}
