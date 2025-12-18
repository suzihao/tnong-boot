package com.tnong.boot.system.user.mapper;

import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.domain.vo.SysUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper 接口
 */
public interface SysUserMapper {

    /**
     * 根据ID查询用户
     */
    SysUser selectById(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username, @Param("tenantId") Long tenantId);

    /**
     * 根据工号ID查询用户
     */
    SysUser selectByEmployeeId(@Param("employeeId") String employeeId, @Param("tenantId") Long tenantId);

    /**
     * 根据邮箱查询用户
     */
    SysUser selectByEmail(@Param("email") String email, @Param("tenantId") Long tenantId);

    /**
     * 根据手机号查询用户
     */
    SysUser selectByMobile(@Param("mobile") String mobile, @Param("tenantId") Long tenantId);

    /**
     * 分页查询用户列表
     */
    List<SysUserVO> selectPageList(@Param("query") SysUserQueryDTO query, @Param("tenantId") Long tenantId);

    /**
     * 查询总数
     */
    Long selectCount(@Param("query") SysUserQueryDTO query, @Param("tenantId") Long tenantId);

    /**
     * 插入用户
     */
    int insert(SysUser user);

    /**
     * 更新用户（带乐观锁）
     */
    int updateByIdWithVersion(SysUser user);

    /**
     * 逻辑删除用户（带乐观锁）
     */
    int deleteById(@Param("id") Long id, @Param("tenantId") Long tenantId, 
                   @Param("version") Integer version, @Param("updatedUser") Long updatedUser);
}
