package com.tnong.boot.system.user.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.dto.SysUserSaveDTO;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.domain.vo.SysUserVO;
import com.tnong.boot.system.user.mapper.SysUserMapper;
import com.tnong.boot.system.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public PageResult<SysUserVO> pageList(SysUserQueryDTO query, Long tenantId) {
        List<SysUserVO> records = sysUserMapper.selectPageList(query, tenantId);
        Long total = sysUserMapper.selectCount(query, tenantId);
        return PageResult.of(total, records, query.getCurrent(), query.getSize());
    }

    @Override
    public SysUserVO getById(Long id, Long tenantId) {
        SysUser user = sysUserMapper.selectById(id, tenantId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        SysUserVO vo = new SysUserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysUserSaveDTO dto, Long tenantId, Long currentUserId) {
        // 校验用户名是否已存在
        SysUser existUser = sysUserMapper.selectByUsername(dto.getUsername(), tenantId);
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        // TODO: 密码加密（BCrypt）
        if (!StringUtils.hasText(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setTenantId(tenantId);
        user.setCreatedUser(currentUserId);
        user.setUpdatedUser(currentUserId);
        user.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysUserMapper.insert(user);
        if (rows == 0) {
            throw new BusinessException("新增用户失败");
        }
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysUserSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        // 查询当前数据
        SysUser dbUser = sysUserMapper.selectById(dto.getId(), tenantId);
        if (dbUser == null) {
            throw new BusinessException("用户不存在或已删除");
        }

        // 如果修改了用户名，检查是否重复
        if (StringUtils.hasText(dto.getUsername()) && !dto.getUsername().equals(dbUser.getUsername())) {
            SysUser existUser = sysUserMapper.selectByUsername(dto.getUsername(), tenantId);
            if (existUser != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setTenantId(tenantId);
        user.setUpdatedUser(currentUserId);

        // TODO: 如果需要修改密码，先进行加密

        int rows = sysUserMapper.updateByIdWithVersion(user);
        if (rows == 0) {
            throw new OptimisticLockException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long tenantId, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }

        int rows = sysUserMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }
}
