package com.tnong.boot.system.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 用户-部门关联 Mapper 接口
 */
public interface SysUserDeptMapper {

    /**
     * 查询用户的所有部门ID列表
     */
    List<Long> selectDeptIdsByUserId(@Param("tenantId") Long tenantId,
                                     @Param("userId") Long userId);

    /**
     * 查询用户的主部门ID
     */
    Long selectMainDeptIdByUserId(@Param("tenantId") Long tenantId,
                                  @Param("userId") Long userId);

    /**
     * 查询部门下的用户ID列表
     */
    List<Long> selectUserIdsByDeptId(@Param("tenantId") Long tenantId,
                                     @Param("deptId") Long deptId);

    /**
     * 批量插入用户-部门关联
     */
    int insertBatch(@Param("tenantId") Long tenantId,
                    @Param("userId") Long userId,
                    @Param("deptIds") List<Long> deptIds,
                    @Param("mainDeptId") Long mainDeptId,
                    @Param("createdUser") Long createdUser);

    /**
     * 删除用户的所有部门关联
     */
    int deleteByUserId(@Param("tenantId") Long tenantId,
                       @Param("userId") Long userId,
                       @Param("updatedUser") Long updatedUser);

    /**
     * 删除部门的所有用户关联
     */
    int deleteByDeptId(@Param("tenantId") Long tenantId,
                       @Param("deptId") Long deptId,
                       @Param("updatedUser") Long updatedUser);
}
