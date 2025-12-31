package com.tnong.boot.system.log.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.log.domain.dto.SysOperLogQueryDTO;
import com.tnong.boot.system.log.domain.vo.SysOperLogVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysOperLogServiceTest {

    @Autowired
    private SysOperLogService sysOperLogService;

    private static final Long TEST_TENANT_ID = 1L;

    @Test
    void testPageList() {
        SysOperLogQueryDTO query = new SysOperLogQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);

        PageResult<SysOperLogVO> result = sysOperLogService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 0);
    }

    @Test
    void testGetById() {
        SysOperLogQueryDTO query = new SysOperLogQueryDTO();
        query.setCurrent(1L);
        query.setSize(1L);

        PageResult<SysOperLogVO> result = sysOperLogService.pageList(query, TEST_TENANT_ID);

        if (result.getTotal() > 0) {
            Long logId = result.getRecords().get(0).getId();
            SysOperLogVO log = sysOperLogService.getById(logId, TEST_TENANT_ID);
            assertNotNull(log);
            assertEquals(logId, log.getId());
        }
    }
}
