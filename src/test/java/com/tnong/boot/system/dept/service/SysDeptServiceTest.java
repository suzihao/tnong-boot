package com.tnong.boot.system.dept.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.dept.domain.dto.SysDeptQueryDTO;
import com.tnong.boot.system.dept.domain.dto.SysDeptSaveDTO;
import com.tnong.boot.system.dept.domain.vo.SysDeptSimpleVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptTreeVO;
import com.tnong.boot.system.dept.domain.vo.SysDeptVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysDeptServiceTest {

    @Autowired
    private SysDeptService sysDeptService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveDept() {
        SysDeptSaveDTO dto = new SysDeptSaveDTO();
        dto.setCode("TEST_DEPT");
        dto.setName("测试部门");
        dto.setParentId(0L);
        dto.setSort(1);
        dto.setStatus(1);

        Long deptId = sysDeptService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(deptId);
        assertTrue(deptId > 0);
    }

    @Test
    void testSaveDeptWithDuplicateCode() {
        SysDeptSaveDTO dto1 = new SysDeptSaveDTO();
        dto1.setCode("DUP_DEPT");
        dto1.setName("部门1");
        dto1.setParentId(0L);
        sysDeptService.save(dto1, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptSaveDTO dto2 = new SysDeptSaveDTO();
        dto2.setCode("DUP_DEPT");
        dto2.setName("部门2");
        dto2.setParentId(0L);

        assertThrows(BusinessException.class, () -> {
            sysDeptService.save(dto2, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testGetById() {
        SysDeptSaveDTO dto = new SysDeptSaveDTO();
        dto.setCode("GET_DEPT");
        dto.setName("查询测试");
        dto.setParentId(0L);
        Long deptId = sysDeptService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptVO dept = sysDeptService.getById(deptId, TEST_TENANT_ID);
        assertNotNull(dept);
        assertEquals("GET_DEPT", dept.getCode());
        assertEquals("查询测试", dept.getName());
    }

    @Test
    void testUpdateDept() {
        SysDeptSaveDTO saveDTO = new SysDeptSaveDTO();
        saveDTO.setCode("UPDATE_DEPT");
        saveDTO.setName("更新前");
        saveDTO.setParentId(0L);
        Long deptId = sysDeptService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptVO dept = sysDeptService.getById(deptId, TEST_TENANT_ID);

        SysDeptSaveDTO updateDTO = new SysDeptSaveDTO();
        updateDTO.setId(deptId);
        updateDTO.setName("更新后");
        updateDTO.setVersion(dept.getVersion());

        assertDoesNotThrow(() -> {
            sysDeptService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });

        SysDeptVO updatedDept = sysDeptService.getById(deptId, TEST_TENANT_ID);
        assertEquals("更新后", updatedDept.getName());
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysDeptSaveDTO saveDTO = new SysDeptSaveDTO();
        saveDTO.setCode("VERSION_DEPT");
        saveDTO.setName("版本测试");
        saveDTO.setParentId(0L);
        Long deptId = sysDeptService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptSaveDTO updateDTO = new SysDeptSaveDTO();
        updateDTO.setId(deptId);
        updateDTO.setName("更新");
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysDeptService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteDept() {
        SysDeptSaveDTO dto = new SysDeptSaveDTO();
        dto.setCode("DELETE_DEPT");
        dto.setName("删除测试");
        dto.setParentId(0L);
        Long deptId = sysDeptService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptVO dept = sysDeptService.getById(deptId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysDeptService.delete(deptId, TEST_TENANT_ID, dept.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysDeptService.getById(deptId, TEST_TENANT_ID);
        });
    }

    @Test
    void testDeleteWithWrongVersion() {
        SysDeptSaveDTO dto = new SysDeptSaveDTO();
        dto.setCode("DEL_VER_DEPT");
        dto.setName("删除版本测试");
        dto.setParentId(0L);
        Long deptId = sysDeptService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        assertThrows(OptimisticLockException.class, () -> {
            sysDeptService.delete(deptId, TEST_TENANT_ID, 999, TEST_USER_ID);
        });
    }

    @Test
    void testTreeStructure() {
        SysDeptSaveDTO parent = new SysDeptSaveDTO();
        parent.setCode("PARENT_DEPT");
        parent.setName("父部门");
        parent.setParentId(0L);
        Long parentId = sysDeptService.save(parent, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptSaveDTO child = new SysDeptSaveDTO();
        child.setCode("CHILD_DEPT");
        child.setName("子部门");
        child.setParentId(parentId);
        sysDeptService.save(child, TEST_TENANT_ID, TEST_USER_ID);

        List<SysDeptTreeVO> tree = sysDeptService.tree(TEST_TENANT_ID);
        assertNotNull(tree);
        assertTrue(tree.size() > 0);
    }

    @Test
    void testList() {
        SysDeptSaveDTO dto = new SysDeptSaveDTO();
        dto.setCode("LIST_DEPT");
        dto.setName("列表测试");
        dto.setParentId(0L);
        sysDeptService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        SysDeptQueryDTO query = new SysDeptQueryDTO();
        List<SysDeptVO> list = sysDeptService.list(query, TEST_TENANT_ID);
        assertNotNull(list);
        assertTrue(list.size() > 0);
    }

    @Test
    void testSimpleList() {
        List<SysDeptSimpleVO> list = sysDeptService.simpleList(TEST_TENANT_ID);
        assertNotNull(list);
    }
}
