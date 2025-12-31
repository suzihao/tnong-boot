package com.tnong.boot.system.dict.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictTypeVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysDictTypeServiceTest {

    @Autowired
    private SysDictTypeService sysDictTypeService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveDictType() {
        SysDictTypeSaveDTO dto = new SysDictTypeSaveDTO();
        dto.setTypeCode("test_type");
        dto.setName("测试字典");
        dto.setStatus(1);

        Long typeId = sysDictTypeService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(typeId);
        assertTrue(typeId > 0);
    }

    @Test
    void testSaveDictTypeWithDuplicateCode() {
        SysDictTypeSaveDTO dto1 = new SysDictTypeSaveDTO();
        dto1.setTypeCode("dup_type");
        dto1.setName("字典1");
        dto1.setStatus(1);
        sysDictTypeService.save(dto1, TEST_TENANT_ID, TEST_USER_ID);

        SysDictTypeSaveDTO dto2 = new SysDictTypeSaveDTO();
        dto2.setTypeCode("dup_type");
        dto2.setName("字典2");
        dto2.setStatus(1);

        assertThrows(BusinessException.class, () -> {
            sysDictTypeService.save(dto2, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testGetById() {
        SysDictTypeSaveDTO dto = new SysDictTypeSaveDTO();
        dto.setTypeCode("get_type");
        dto.setName("查询测试");
        dto.setStatus(1);
        Long typeId = sysDictTypeService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDictTypeVO dictType = sysDictTypeService.getById(typeId, TEST_TENANT_ID);
        assertNotNull(dictType);
        assertEquals("get_type", dictType.getTypeCode());
    }

    @Test
    void testUpdateDictType() {
        SysDictTypeSaveDTO saveDTO = new SysDictTypeSaveDTO();
        saveDTO.setTypeCode("update_type");
        saveDTO.setName("更新前");
        saveDTO.setStatus(1);
        Long typeId = sysDictTypeService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDictTypeVO dictType = sysDictTypeService.getById(typeId, TEST_TENANT_ID);

        SysDictTypeSaveDTO updateDTO = new SysDictTypeSaveDTO();
        updateDTO.setId(typeId);
        updateDTO.setName("更新后");
        updateDTO.setVersion(dictType.getVersion());

        assertDoesNotThrow(() -> {
            sysDictTypeService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysDictTypeSaveDTO saveDTO = new SysDictTypeSaveDTO();
        saveDTO.setTypeCode("version_type");
        saveDTO.setName("版本测试");
        saveDTO.setStatus(1);
        Long typeId = sysDictTypeService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDictTypeSaveDTO updateDTO = new SysDictTypeSaveDTO();
        updateDTO.setId(typeId);
        updateDTO.setName("更新");
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysDictTypeService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteDictType() {
        SysDictTypeSaveDTO dto = new SysDictTypeSaveDTO();
        dto.setTypeCode("delete_type");
        dto.setName("删除测试");
        dto.setStatus(1);
        Long typeId = sysDictTypeService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDictTypeVO dictType = sysDictTypeService.getById(typeId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysDictTypeService.delete(typeId, TEST_TENANT_ID, dictType.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysDictTypeService.getById(typeId, TEST_TENANT_ID);
        });
    }

    @Test
    void testPageList() {
        for (int i = 1; i <= 5; i++) {
            SysDictTypeSaveDTO dto = new SysDictTypeSaveDTO();
            dto.setTypeCode("page_type_" + i);
            dto.setName("分页测试" + i);
            dto.setStatus(1);
            sysDictTypeService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        SysDictTypeQueryDTO query = new SysDictTypeQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysDictTypeVO> result = sysDictTypeService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }
}
