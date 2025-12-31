package com.tnong.boot.system.job.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.job.domain.dto.SysJobQueryDTO;
import com.tnong.boot.system.job.domain.dto.SysJobSaveDTO;
import com.tnong.boot.system.job.domain.vo.SysJobVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysJobServiceTest {

    @Autowired
    private SysJobService sysJobService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveJob() {
        SysJobSaveDTO dto = new SysJobSaveDTO();
        dto.setJobName("测试任务");
        dto.setJobGroup("TEST");
        dto.setInvokeTarget("testBean.testMethod");
        dto.setCronExpression("0 0 0 * * ?");
        dto.setMisfirePolicy(0);
        dto.setConcurrent(0);
        dto.setStatus(0);

        Long jobId = sysJobService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(jobId);
        assertTrue(jobId > 0);
    }

    @Test
    void testGetById() {
        SysJobSaveDTO dto = new SysJobSaveDTO();
        dto.setJobName("查询测试");
        dto.setJobGroup("TEST");
        dto.setInvokeTarget("testBean.method");
        dto.setCronExpression("0 0 0 * * ?");
        dto.setMisfirePolicy(0);
        dto.setConcurrent(0);
        dto.setStatus(0);
        Long jobId = sysJobService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysJobVO job = sysJobService.getById(jobId, TEST_TENANT_ID);
        assertNotNull(job);
        assertEquals("查询测试", job.getJobName());
    }

    @Test
    void testUpdateJob() {
        SysJobSaveDTO saveDTO = new SysJobSaveDTO();
        saveDTO.setJobName("更新前");
        saveDTO.setJobGroup("TEST");
        saveDTO.setInvokeTarget("testBean.method");
        saveDTO.setCronExpression("0 0 0 * * ?");
        saveDTO.setMisfirePolicy(0);
        saveDTO.setConcurrent(0);
        saveDTO.setStatus(0);
        Long jobId = sysJobService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysJobVO job = sysJobService.getById(jobId, TEST_TENANT_ID);

        SysJobSaveDTO updateDTO = new SysJobSaveDTO();
        updateDTO.setId(jobId);
        updateDTO.setJobName("更新后");
        updateDTO.setJobGroup("TEST");
        updateDTO.setInvokeTarget("testBean.method");
        updateDTO.setCronExpression("0 0 1 * * ?");
        updateDTO.setVersion(job.getVersion());

        assertDoesNotThrow(() -> {
            sysJobService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });

        SysJobVO updatedJob = sysJobService.getById(jobId, TEST_TENANT_ID);
        assertEquals("更新后", updatedJob.getJobName());
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysJobSaveDTO saveDTO = new SysJobSaveDTO();
        saveDTO.setJobName("版本测试");
        saveDTO.setJobGroup("TEST");
        saveDTO.setInvokeTarget("testBean.method");
        saveDTO.setCronExpression("0 0 0 * * ?");
        saveDTO.setMisfirePolicy(0);
        saveDTO.setConcurrent(0);
        saveDTO.setStatus(0);
        Long jobId = sysJobService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysJobSaveDTO updateDTO = new SysJobSaveDTO();
        updateDTO.setId(jobId);
        updateDTO.setJobName("更新");
        updateDTO.setJobGroup("TEST");
        updateDTO.setInvokeTarget("testBean.method");
        updateDTO.setCronExpression("0 0 0 * * ?");
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysJobService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteJob() {
        SysJobSaveDTO dto = new SysJobSaveDTO();
        dto.setJobName("删除测试");
        dto.setJobGroup("TEST");
        dto.setInvokeTarget("testBean.method");
        dto.setCronExpression("0 0 0 * * ?");
        dto.setMisfirePolicy(0);
        dto.setConcurrent(0);
        dto.setStatus(0);
        Long jobId = sysJobService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysJobVO job = sysJobService.getById(jobId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysJobService.delete(jobId, TEST_TENANT_ID, job.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysJobService.getById(jobId, TEST_TENANT_ID);
        });
    }

    @Test
    void testPageList() {
        for (int i = 1; i <= 5; i++) {
            SysJobSaveDTO dto = new SysJobSaveDTO();
            dto.setJobName("分页测试" + i);
            dto.setJobGroup("TEST");
            dto.setInvokeTarget("testBean.method" + i);
            dto.setCronExpression("0 0 " + i + " * * ?");
            dto.setMisfirePolicy(0);
            dto.setConcurrent(0);
            dto.setStatus(0);
            sysJobService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        SysJobQueryDTO query = new SysJobQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysJobVO> result = sysJobService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }
}
