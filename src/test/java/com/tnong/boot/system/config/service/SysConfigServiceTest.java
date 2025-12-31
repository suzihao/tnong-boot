package com.tnong.boot.system.config.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.config.domain.dto.SysConfigQueryDTO;
import com.tnong.boot.system.config.domain.dto.SysConfigSaveDTO;
import com.tnong.boot.system.config.domain.vo.SysConfigVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysConfigServiceTest {

    @Autowired
    private SysConfigService sysConfigService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveConfig() {
        SysConfigSaveDTO dto = new SysConfigSaveDTO();
        dto.setConfigKey("test.config.key");
        dto.setConfigValue("test_value");
        dto.setConfigName("测试配置");
        dto.setConfigType(1);
        dto.setIsSystem(0);

        Long configId = sysConfigService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(configId);
        assertTrue(configId > 0);
    }

    @Test
    void testSaveConfigWithDuplicateKey() {
        SysConfigSaveDTO dto1 = new SysConfigSaveDTO();
        dto1.setConfigKey("duplicate.key");
        dto1.setConfigValue("value1");
        dto1.setConfigName("配置1");
        dto1.setConfigType(1);
        sysConfigService.save(dto1, TEST_TENANT_ID, TEST_USER_ID);

        SysConfigSaveDTO dto2 = new SysConfigSaveDTO();
        dto2.setConfigKey("duplicate.key");
        dto2.setConfigValue("value2");
        dto2.setConfigName("配置2");
        dto2.setConfigType(1);

        assertThrows(BusinessException.class, () -> {
            sysConfigService.save(dto2, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testGetById() {
        SysConfigSaveDTO dto = new SysConfigSaveDTO();
        dto.setConfigKey("get.test.key");
        dto.setConfigValue("get_value");
        dto.setConfigName("查询测试");
        dto.setConfigType(1);
        Long configId = sysConfigService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysConfigVO config = sysConfigService.getById(configId, TEST_TENANT_ID);
        assertNotNull(config);
        assertEquals("get.test.key", config.getConfigKey());
        assertEquals("get_value", config.getConfigValue());
    }

    @Test
    void testGetValueByKey() {
        SysConfigSaveDTO dto = new SysConfigSaveDTO();
        dto.setConfigKey("value.test.key");
        dto.setConfigValue("test_value_123");
        dto.setConfigName("值查询测试");
        dto.setConfigType(1);
        sysConfigService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        String value = sysConfigService.getValueByKey("value.test.key", TEST_TENANT_ID);
        assertEquals("test_value_123", value);
    }

    @Test
    void testUpdateConfig() {
        SysConfigSaveDTO saveDTO = new SysConfigSaveDTO();
        saveDTO.setConfigKey("update.test.key");
        saveDTO.setConfigValue("old_value");
        saveDTO.setConfigName("更新前");
        saveDTO.setConfigType(1);
        saveDTO.setIsSystem(0);
        Long configId = sysConfigService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysConfigVO config = sysConfigService.getById(configId, TEST_TENANT_ID);

        SysConfigSaveDTO updateDTO = new SysConfigSaveDTO();
        updateDTO.setId(configId);
        updateDTO.setConfigKey("update.test.key");
        updateDTO.setConfigValue("new_value");
        updateDTO.setConfigName("更新后");
        updateDTO.setConfigType(1);
        updateDTO.setVersion(config.getVersion());

        assertDoesNotThrow(() -> {
            sysConfigService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });

        SysConfigVO updatedConfig = sysConfigService.getById(configId, TEST_TENANT_ID);
        assertEquals("new_value", updatedConfig.getConfigValue());
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysConfigSaveDTO saveDTO = new SysConfigSaveDTO();
        saveDTO.setConfigKey("version.test.key");
        saveDTO.setConfigValue("value");
        saveDTO.setConfigName("版本测试");
        saveDTO.setConfigType(1);
        Long configId = sysConfigService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysConfigSaveDTO updateDTO = new SysConfigSaveDTO();
        updateDTO.setId(configId);
        updateDTO.setConfigKey("version.test.key");
        updateDTO.setConfigValue("new_value");
        updateDTO.setConfigName("更新");
        updateDTO.setConfigType(1);
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysConfigService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteConfig() {
        SysConfigSaveDTO dto = new SysConfigSaveDTO();
        dto.setConfigKey("delete.test.key");
        dto.setConfigValue("value");
        dto.setConfigName("删除测试");
        dto.setConfigType(1);
        dto.setIsSystem(0);
        Long configId = sysConfigService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysConfigVO config = sysConfigService.getById(configId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysConfigService.delete(configId, TEST_TENANT_ID, config.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysConfigService.getById(configId, TEST_TENANT_ID);
        });
    }

    @Test
    void testPageList() {
        for (int i = 1; i <= 5; i++) {
            SysConfigSaveDTO dto = new SysConfigSaveDTO();
            dto.setConfigKey("page.test.key." + i);
            dto.setConfigValue("value" + i);
            dto.setConfigName("分页测试" + i);
            dto.setConfigType(1);
            sysConfigService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        SysConfigQueryDTO query = new SysConfigQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysConfigVO> result = sysConfigService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }
}
