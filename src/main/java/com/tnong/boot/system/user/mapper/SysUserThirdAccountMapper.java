package com.tnong.boot.system.user.mapper;

import com.tnong.boot.system.user.domain.entity.SysUserThirdAccount;
import org.apache.ibatis.annotations.Param;

/**
 * 用户第三方账号绑定 Mapper 接口
 */
public interface SysUserThirdAccountMapper {

    /**
     * 根据类型和第三方用户ID查询绑定记录
     */
    SysUserThirdAccount selectByTypeAndThirdUserId(@Param("type") String type,
                                                   @Param("thirdUserId") String thirdUserId);

    /**
     * 插入绑定记录
     */
    int insert(SysUserThirdAccount record);

    /**
     * 更新绑定记录（带乐观锁）
     */
    int updateByIdWithVersion(SysUserThirdAccount record);

    /**
     * 逻辑删除绑定记录（带乐观锁）
     */
    int deleteById(@Param("id") Long id,
                   @Param("version") Integer version,
                   @Param("updatedUser") Long updatedUser);
}
