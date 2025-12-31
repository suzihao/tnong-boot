package com.tnong.boot.system.job.service;

import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobLogQueryDTO;
import com.tnong.boot.system.job.domain.vo.SysJobLogVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysJobLogServiceTest {

    @Autowired
    private SysJobLogService sysJobLogService;

    private static final Long TEST_TENANT_ID = 1L;

    @Test
    void testPageList() {
        SysJobLogQueryDTO query = new SysJobLogQueryDTO();
        query.setCurrent(1L);
        query.setSize(10L);

        PageResult<SysJobLogVO> result = sysJobLogService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertNotNull(result.getRecords());
        assertTrue(result.getTotal() >= 0);
    }

    @Test
    void testGetById() {
        SysJobLogQueryDTO query = new SysJobLogQueryDTO();
        query.setCurrent(1L);
        query.setSize(1L);

        PageResult<SysJobLogVO> result = sysJobLogService.pageList(query, TEST_TENANT_ID);

        if (result.getTotal() > 0) {
            Long logId = result.getRecords().get(0).getId();
            SysJobLogVO log = sysJobLogService.getById(logId, TEST_TENANT_ID);
            assertNotNull(log);
            assertEquals(logId, log.getId());
        }
    }
}
