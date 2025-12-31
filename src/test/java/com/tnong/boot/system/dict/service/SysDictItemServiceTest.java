package com.tnong.boot.system.dict.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.dict.domain.dto.SysDictItemQueryDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictItemSaveDTO;
import com.tnong.boot.system.dict.domain.dto.SysDictTypeSaveDTO;
import com.tnong.boot.system.dict.domain.vo.SysDictItemVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysDictItemServiceTest {

    @Autowired
    private SysDictItemService sysDictItemService;

    @Autowired
    private SysDictTypeService sysDictTypeService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    private Long createTestDictType() {
        SysDictTypeSaveDTO typeDTO = new SysDictTypeSaveDTO();
        typeDTO.setTypeCode("test_dict_type");
        typeDTO.setName("测试字典类型");
        typeDTO.setStatus(1);
        return sysDictTypeService.save(typeDTO, TEST_TENANT_ID, TEST_USER_ID);
    }

    @Test
    void testSaveDictItem() {
        Long typeId = createTestDictType();

        SysDictItemSaveDTO dto = new SysDictItemSaveDTO();
        dto.setTypeCode("test_dict_type");
        dto.setItemLabel("测试项");
        dto.setItemValue("test_value");
        dto.setSort(1);
        dto.setStatus(1);

        Long itemId = sysDictItemService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(itemId);
        assertTrue(itemId > 0);
    }

    @Test
    void testGetById() {
        Long typeId = createTestDictType();

        SysDictItemSaveDTO dto = new SysDictItemSaveDTO();
        dto.setTypeCode("test_dict_type");
        dto.setItemLabel("查询测试");
        dto.setItemValue("get_value");
        dto.setSort(1);
        dto.setStatus(1);
        Long itemId = sysDictItemService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDictItemVO item = sysDictItemService.getById(itemId, TEST_TENANT_ID);
        assertNotNull(item);
        assertEquals("get_value", item.getItemValue());
    }

    @Test
    void testUpdateDictItem() {
        Long typeId = createTestDictType();

        SysDictItemSaveDTO saveDTO = new SysDictItemSaveDTO();
        saveDTO.setTypeCode("test_dict_type");
        saveDTO.setItemLabel("更新前");
        saveDTO.setItemValue("old_value");
        saveDTO.setSort(1);
        saveDTO.setStatus(1);
        Long itemId = sysDictItemService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDictItemVO item = sysDictItemService.getById(itemId, TEST_TENANT_ID);

        SysDictItemSaveDTO updateDTO = new SysDictItemSaveDTO();
        updateDTO.setId(itemId);
        updateDTO.setTypeCode("test_dict_type");
        updateDTO.setItemLabel("更新后");
        updateDTO.setItemValue("new_value");
        updateDTO.setSort(1);
        updateDTO.setVersion(item.getVersion());

        assertDoesNotThrow(() -> {
            sysDictItemService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });

        SysDictItemVO updatedItem = sysDictItemService.getById(itemId, TEST_TENANT_ID);
        assertEquals("更新后", updatedItem.getItemLabel());
    }

    @Test
    void testUpdateWithWrongVersion() {
        Long typeId = createTestDictType();

        SysDictItemSaveDTO saveDTO = new SysDictItemSaveDTO();
        saveDTO.setTypeCode("test_dict_type");
        saveDTO.setItemLabel("版本测试");
        saveDTO.setItemValue("value");
        saveDTO.setSort(1);
        saveDTO.setStatus(1);
        Long itemId = sysDictItemService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDictItemSaveDTO updateDTO = new SysDictItemSaveDTO();
        updateDTO.setId(itemId);
        updateDTO.setTypeCode("test_dict_type");
        updateDTO.setItemLabel("更新");
        updateDTO.setItemValue("value");
        updateDTO.setSort(1);
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysDictItemService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteDictItem() {
        Long typeId = createTestDictType();

        SysDictItemSaveDTO dto = new SysDictItemSaveDTO();
        dto.setTypeCode("test_dict_type");
        dto.setItemLabel("删除测试");
        dto.setItemValue("delete_value");
        dto.setSort(1);
        dto.setStatus(1);
        Long itemId = sysDictItemService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDictItemVO item = sysDictItemService.getById(itemId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysDictItemService.delete(itemId, TEST_TENANT_ID, item.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysDictItemService.getById(itemId, TEST_TENANT_ID);
        });
    }

    @Test
    void testPageList() {
        Long typeId = createTestDictType();

        for (int i = 1; i <= 5; i++) {
            SysDictItemSaveDTO dto = new SysDictItemSaveDTO();
            dto.setTypeCode("test_dict_type");
            dto.setItemLabel("分页测试" + i);
            dto.setItemValue("page_value_" + i);
            dto.setSort(i);
            dto.setStatus(1);
            sysDictItemService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        SysDictItemQueryDTO query = new SysDictItemQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysDictItemVO> result = sysDictItemService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }

    @Test
    void testListByTypeCode() {
        Long typeId = createTestDictType();

        for (int i = 1; i <= 3; i++) {
            SysDictItemSaveDTO dto = new SysDictItemSaveDTO();
            dto.setTypeCode("test_dict_type");
            dto.setItemLabel("类型查询" + i);
            dto.setItemValue("type_value_" + i);
            dto.setSort(i);
            dto.setStatus(1);
            sysDictItemService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        List<SysDictItemVO> items = sysDictItemService.listByTypeCode("test_dict_type", TEST_TENANT_ID);
        assertNotNull(items);
        assertTrue(items.size() >= 3);
    }
}
