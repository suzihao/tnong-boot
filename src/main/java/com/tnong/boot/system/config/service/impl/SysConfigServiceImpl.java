package com.tnong.boot.system.config.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.config.domain.dto.SysConfigQueryDTO;
import com.tnong.boot.system.config.domain.dto.SysConfigSaveDTO;
import com.tnong.boot.system.config.domain.entity.SysConfig;
import com.tnong.boot.system.config.domain.vo.SysConfigVO;
import com.tnong.boot.system.config.mapper.SysConfigMapper;
import com.tnong.boot.system.config.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final SysConfigMapper sysConfigMapper;

    @Override
    public PageResult<SysConfigVO> pageList(SysConfigQueryDTO query, Long tenantId) {
        List<SysConfig> entities = sysConfigMapper.selectPageList(query, tenantId);
        Long total = sysConfigMapper.selectCount(query, tenantId);

        List<SysConfigVO> records = entities.stream()
                .map(entity -> {
                    SysConfigVO vo = new SysConfigVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysConfigVO getById(Long id, Long tenantId) {
        SysConfig config = sysConfigMapper.selectById(id, tenantId);
        if (config == null) {
            throw new BusinessException("配置不存在");
        }
        SysConfigVO vo = new SysConfigVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    @Override
    public String getValueByKey(String configKey, Long tenantId) {
        SysConfig config = sysConfigMapper.selectByKey(configKey, tenantId);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysConfigSaveDTO dto, Long tenantId, Long currentUserId) {
        SysConfig exist = sysConfigMapper.selectByKey(dto.getConfigKey(), tenantId);
        if (exist != null) {
            throw new BusinessException("配置键已存在");
        }

        SysConfig config = new SysConfig();
        BeanUtils.copyProperties(dto, config);
        config.setTenantId(tenantId);
        config.setCreatedUser(currentUserId);
        config.setUpdatedUser(currentUserId);
        config.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysConfigMapper.insert(config);
        if (rows == 0) {
            throw new BusinessException("新增配置失败");
        }
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysConfigSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("配置ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysConfig dbConfig = sysConfigMapper.selectById(dto.getId(), tenantId);
        if (dbConfig == null) {
            throw new BusinessException("配置不存在或已删除");
        }

        if (dbConfig.getIsSystem() == 1) {
            throw new BusinessException("系统内置配置不允许修改");
        }

        if (!dto.getConfigKey().equals(dbConfig.getConfigKey())) {
            SysConfig exist = sysConfigMapper.selectByKey(dto.getConfigKey(), tenantId);
            if (exist != null) {
                throw new BusinessException("配置键已存在");
            }
        }

        SysConfig config = new SysConfig();
        BeanUtils.copyProperties(dto, config);
        config.setTenantId(tenantId);
        config.setUpdatedUser(currentUserId);

        int rows = sysConfigMapper.updateByIdWithVersion(config);
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

        SysConfig dbConfig = sysConfigMapper.selectById(id, tenantId);
        if (dbConfig != null && dbConfig.getIsSystem() == 1) {
            throw new BusinessException("系统内置配置不允许删除");
        }

        int rows = sysConfigMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }
}
