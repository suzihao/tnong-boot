package com.tnong.boot.system.tenant.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.dto.SysTenantSaveDTO;
import com.tnong.boot.system.tenant.domain.vo.SysTenantVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysTenantServiceTest {

    @Autowired
    private SysTenantService sysTenantService;

    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveTenant() {
        SysTenantSaveDTO dto = new SysTenantSaveDTO();
        dto.setId(1001L);
        dto.setTenantCode(100L);
        dto.setName("测试租户");
        dto.setContactName("张三");
        dto.setContactPhone("13800138000");
        dto.setExpireTime(LocalDateTime.now().plusYears(1));

        Long tenantId = sysTenantService.save(dto, TEST_USER_ID);
        assertNotNull(tenantId);
        assertEquals(1001L, tenantId);
    }

    @Test
    void testSaveTenantWithoutId() {
        SysTenantSaveDTO dto = new SysTenantSaveDTO();
        dto.setTenantCode(200L);
        dto.setName("测试租户2");

        assertThrows(BusinessException.class, () -> {
            sysTenantService.save(dto, TEST_USER_ID);
        });
    }

    @Test
    void testSaveTenantWithDuplicateId() {
        SysTenantSaveDTO dto1 = new SysTenantSaveDTO();
        dto1.setId(1002L);
        dto1.setTenantCode(300L);
        dto1.setName("租户1");
        sysTenantService.save(dto1, TEST_USER_ID);

        SysTenantSaveDTO dto2 = new SysTenantSaveDTO();
        dto2.setId(1002L);
        dto2.setTenantCode(400L);
        dto2.setName("租户2");

        assertThrows(BusinessException.class, () -> {
            sysTenantService.save(dto2, TEST_USER_ID);
        });
    }

    @Test
    void testGetById() {
        SysTenantSaveDTO dto = new SysTenantSaveDTO();
        dto.setId(1003L);
        dto.setTenantCode(500L);
        dto.setName("查询测试");
        sysTenantService.save(dto, TEST_USER_ID);

        SysTenantVO tenant = sysTenantService.getById(1003L);
        assertNotNull(tenant);
        assertEquals(500L, tenant.getTenantCode());
        assertEquals("查询测试", tenant.getName());
    }

    @Test
    void testUpdateTenant() {
        SysTenantSaveDTO saveDTO = new SysTenantSaveDTO();
        saveDTO.setId(1004L);
        saveDTO.setTenantCode(600L);
        saveDTO.setName("更新前");
        sysTenantService.save(saveDTO, TEST_USER_ID);

        SysTenantVO tenant = sysTenantService.getById(1004L);

        SysTenantSaveDTO updateDTO = new SysTenantSaveDTO();
        updateDTO.setId(1004L);
        updateDTO.setName("更新后");
        updateDTO.setContactName("李四");
        updateDTO.setVersion(tenant.getVersion());

        assertDoesNotThrow(() -> {
            sysTenantService.update(updateDTO, TEST_USER_ID);
        });

        SysTenantVO updatedTenant = sysTenantService.getById(1004L);
        assertEquals("更新后", updatedTenant.getName());
        assertEquals("李四", updatedTenant.getContactName());
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysTenantSaveDTO saveDTO = new SysTenantSaveDTO();
        saveDTO.setId(1005L);
        saveDTO.setTenantCode(700L);
        saveDTO.setName("版本测试");
        sysTenantService.save(saveDTO, TEST_USER_ID);

        SysTenantSaveDTO updateDTO = new SysTenantSaveDTO();
        updateDTO.setId(1005L);
        updateDTO.setName("更新");
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysTenantService.update(updateDTO, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteTenant() {
        SysTenantSaveDTO dto = new SysTenantSaveDTO();
        dto.setId(1006L);
        dto.setTenantCode(800L);
        dto.setName("删除测试");
        sysTenantService.save(dto, TEST_USER_ID);

        SysTenantVO tenant = sysTenantService.getById(1006L);

        assertDoesNotThrow(() -> {
            sysTenantService.delete(1006L, tenant.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysTenantService.getById(1006L);
        });
    }

    @Test
    void testDeleteWithWrongVersion() {
        SysTenantSaveDTO dto = new SysTenantSaveDTO();
        dto.setId(1007L);
        dto.setTenantCode(900L);
        dto.setName("删除版本测试");
        sysTenantService.save(dto, TEST_USER_ID);

        assertThrows(OptimisticLockException.class, () -> {
            sysTenantService.delete(1007L, 999, TEST_USER_ID);
        });
    }

    @Test
    void testPageList() {
        for (int i = 1; i <= 5; i++) {
            SysTenantSaveDTO dto = new SysTenantSaveDTO();
            dto.setId(2000L + i);
            dto.setTenantCode((long) i);
            dto.setName("分页测试" + i);
            sysTenantService.save(dto, TEST_USER_ID);
        }

        SysTenantQueryDTO query = new SysTenantQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysTenantVO> result = sysTenantService.pageList(query);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }
}
