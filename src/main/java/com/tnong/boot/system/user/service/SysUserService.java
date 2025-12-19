package com.tnong.boot.system.user.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.dto.SysUserSaveDTO;
import com.tnong.boot.system.user.domain.vo.SysUserVO;

/**
 * 用户服务接口
 */
public interface SysUserService {

    /**
     * 分页查询用户列表
     */
    PageResult<SysUserVO> pageList(SysUserQueryDTO query, Long tenantId);

    /**
     * 根据ID查询用户详情
     */
    SysUserVO getById(Long id, Long tenantId);

    /**
     * 新增用户
     */
    Long save(SysUserSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 更新用户
     */
    void update(SysUserSaveDTO dto, Long tenantId, Long currentUserId);

    /**
     * 删除用户
     */
    void delete(Long id, Long tenantId, Integer version, Long currentUserId);

    /**
     * 查询用户已分配的角色ID列表
     */
    java.util.List<Long> getUserRoleIds(Long userId, Long tenantId);

    /**
     * 为用户分配角色
     */
    void assignRoles(Long userId, java.util.List<Long> roleIds, Long tenantId, Long currentUserId);
}
