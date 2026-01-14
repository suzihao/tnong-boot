package com.tnong.boot.system.dict.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictItemQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictItemSaveDTO;
import com.tnong.boot.system.dict.domain.entity.SysDictItem;
import com.tnong.boot.system.dict.domain.vo.SysDictItemVO;
import com.tnong.boot.system.dict.mapper.SysDictItemMapper;
import com.tnong.boot.system.dict.service.SysDictItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysDictItemServiceImpl implements SysDictItemService {

    private final SysDictItemMapper sysDictItemMapper;

    @Override
    public PageResult<SysDictItemVO> pageList(SysDictItemQueryDTO query, Long tenantId) {
        List<SysDictItem> entities = sysDictItemMapper.selectPageList(query, tenantId);
        Long total = sysDictItemMapper.selectCount(query, tenantId);

        List<SysDictItemVO> records = entities.stream()
                .map(entity -> {
                    SysDictItemVO vo = new SysDictItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        return PageResult.of(total, records, query.getPage(), query.getSize());
    }

    @Override
    public SysDictItemVO getById(Long id, Long tenantId) {
        SysDictItem dictItem = sysDictItemMapper.selectById(id, tenantId);
        if (dictItem == null) {
            throw new BusinessException("字典项不存在");
        }
        SysDictItemVO vo = new SysDictItemVO();
        BeanUtils.copyProperties(dictItem, vo);
        return vo;
    }

    @Override
    public List<SysDictItemVO> listByTypeCode(String typeCode, Long tenantId) {
        List<SysDictItem> entities = sysDictItemMapper.selectByTypeCode(typeCode, tenantId);
        return entities.stream()
                .map(entity -> {
                    SysDictItemVO vo = new SysDictItemVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(SysDictItemSaveDTO dto, Long tenantId, Long currentUserId) {
        SysDictItem dictItem = new SysDictItem();
        BeanUtils.copyProperties(dto, dictItem);
        dictItem.setTenantId(tenantId);
        dictItem.setCreatedUser(currentUserId);
        dictItem.setUpdatedUser(currentUserId);
        dictItem.setStatus(CommonConstant.STATUS_ENABLE);

        int rows = sysDictItemMapper.insert(dictItem);
        if (rows == 0) {
            throw new BusinessException("新增字典项失败");
        }
        return dictItem.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysDictItemSaveDTO dto, Long tenantId, Long currentUserId) {
        if (dto.getId() == null) {
            throw new BusinessException("字典项ID不能为空");
        }
        if (dto.getVersion() == null) {
            throw new BusinessException("版本号不能为空");
        }

        SysDictItem dbDictItem = sysDictItemMapper.selectById(dto.getId(), tenantId);
        if (dbDictItem == null) {
            throw new BusinessException("字典项不存在或已删除");
        }

        SysDictItem dictItem = new SysDictItem();
        BeanUtils.copyProperties(dto, dictItem);
        dictItem.setTenantId(tenantId);
        dictItem.setUpdatedUser(currentUserId);

        int rows = sysDictItemMapper.updateByIdWithVersion(dictItem);
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

        int rows = sysDictItemMapper.deleteById(id, tenantId, version, currentUserId);
        if (rows == 0) {
            throw new OptimisticLockException("删除失败，数据可能已被修改");
        }
    }
}
