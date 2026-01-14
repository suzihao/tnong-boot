package com.tnong.boot.system.tenant.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.dto.SysTenantSaveDTO;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import com.tnong.boot.system.tenant.domain.vo.SysTenantVO;
import com.tnong.boot.system.tenant.mapper.SysTenantMapper;
import com.tnong.boot.system.tenant.service.SysTenantService;

import lombok.RequiredArgsConstructor;

/**
 * 租户服务实现
 */
@Service
@RequiredArgsConstructor
public class SysTenantServiceImpl implements SysTenantService {

    private final SysTenantMapper sysTenantMapper;

    @Override
    public PageResult<SysTenantVO> pageList(SysTenantQueryDTO query) {
        List<SysTenant> entities = sysTenantMapper.selectPageList(query);
        Long total = sysTenantMapper.selectCount(query);
        
        // Entity 转 VO
        List<SysTenantVO> records = entities.stream()
                .map(entity -> {
                    SysTenantVO vo = new SysTenantVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();
        
        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysTenantVO getById(Long id) {
        SysTenant tenant = sysTenantMapper.selectById(id);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        SysTenantVO vo = new SysTenantVO();
        BeanUtils.copyProperties(tenant, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysTenantSaveDTO dto, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("租户ID不能为空");
        }

        SysTenant existTenant = sysTenantMapper.selectById(dto.getId());
        if (existTenant != null) {
            throw new BusinessException("租户ID已存在");
        }

        SysTenant tenant = new SysTenant();
        BeanUtils.copyProperties(dto, tenant);
        tenant.setCreatedUser(currentUserId);
        tenant.setUpdatedUser(currentUserId);
        tenant.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysTenantMapper.insert(tenant);
        if (rows == 0) {
            throw new BusinessException("新增租户失败");
        }
        return tenant.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysTenantSaveDTO dto, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("租户ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysTenant dbTenant = sysTenantMapper.selectById(dto.getId());
        if (dbTenant == null) {
            throw new BusinessException("租户不存在或已删除");
        }

        // 租户ID不允许修改
        if (dto.getId() != null && !dto.getId().equals(dbTenant.getId())) {
            throw new BusinessException("租户ID不允许修改");
        }

        SysTenant tenant = new SysTenant();
        BeanUtils.copyProperties(dto, tenant);
        tenant.setTenantCode(null); // 确保不修改tenantCode
        tenant.setUpdatedUser(currentUserId);

        int rows = sysTenantMapper.updateByIdWithVersion(tenant);
        if (rows == 0) {
            throw new OptimisticLockException();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Integer version, Long currentUserId) {
        if (version == null) {
            throw new BusinessException("版本号不能为空");
        }

        int rows = sysTenantMapper.deleteById(id, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }
}
