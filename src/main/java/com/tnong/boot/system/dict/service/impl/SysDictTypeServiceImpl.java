package com.tnong.boot.system.dict.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeSaveDTO;
import com.tnong.boot.system.dict.domain.entity.SysDictType;
import com.tnong.boot.system.dict.domain.vo.SysDictTypeVO;
import com.tnong.boot.system.dict.mapper.SysDictTypeMapper;
import com.tnong.boot.system.dict.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper sysDictTypeMapper;

    @Override
    public PageResult<SysDictTypeVO> pageList(SysDictTypeQueryDTO query, Long tenantId) {
        List<SysDictType> entities = sysDictTypeMapper.selectPageList(query, tenantId);
        Long total = sysDictTypeMapper.selectCount(query, tenantId);

        List<SysDictTypeVO> records = entities.stream()
                .map(entity -> {
                    SysDictTypeVO vo = new SysDictTypeVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysDictTypeVO getById(Long id, Long tenantId) {
        SysDictType dictType = sysDictTypeMapper.selectById(id, tenantId);
        if (dictType == null) {
            throw new BusinessException("字典类型不存在");
        }
        SysDictTypeVO vo = new SysDictTypeVO();
        BeanUtils.copyProperties(dictType, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysDictTypeSaveDTO dto, Long tenantId, Long currentUserId) {
        SysDictType exist = sysDictTypeMapper.selectByTypeCode(dto.getTypeCode(), tenantId);
        if (exist != null) {
            throw new BusinessException("字典类型编码已存在");
        }

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dto, dictType);
        dictType.setTenantId(tenantId);
        dictType.setCreatedUser(currentUserId);
        dictType.setUpdatedUser(currentUserId);
        dictType.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysDictTypeMapper.insert(dictType);
        if (rows == 0) {
            throw new BusinessException("新增字典类型失败");
        }
        return dictType.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictTypeSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("字典类型ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysDictType dbDictType = sysDictTypeMapper.selectById(dto.getId(), tenantId);
        if (dbDictType == null) {
            throw new BusinessException("字典类型不存在或已删除");
        }

        if (!dto.getTypeCode().equals(dbDictType.getTypeCode())) {
            SysDictType exist = sysDictTypeMapper.selectByTypeCode(dto.getTypeCode(), tenantId);
            if (exist != null) {
                throw new BusinessException("字典类型编码已存在");
            }
        }

        SysDictType dictType = new SysDictType();
        BeanUtils.copyProperties(dto, dictType);
        dictType.setTenantId(tenantId);
        dictType.setUpdatedUser(currentUserId);

        int rows = sysDictTypeMapper.updateByIdWithVersion(dictType);
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

        int rows = sysDictTypeMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }
}
