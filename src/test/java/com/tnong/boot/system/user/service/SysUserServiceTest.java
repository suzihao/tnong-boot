package com.tnong.boot.system.user.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.dto.SysUserSaveDTO;
import com.tnong.boot.system.user.domain.vo.SysUserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveUser() {
        SysUserSaveDTO dto = new SysUserSaveDTO();
        dto.setUsername("testuser");
        dto.setPassword("123456");
        dto.setNickname("测试用户");
        dto.setEmail("test@example.com");
        dto.setMobile("13800138000");
        dto.setStatus(1);

        Long userId = sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(userId);
        assertTrue(userId > 0);
    }

    @Test
    void testSaveUserWithDuplicateUsername() {
        SysUserSaveDTO dto1 = new SysUserSaveDTO();
        dto1.setUsername("duplicate_user");
        dto1.setPassword("123456");
        dto1.setNickname("用户1");
        dto1.setStatus(1);

        sysUserService.save(dto1, TEST_TENANT_ID, TEST_USER_ID);

        SysUserSaveDTO dto2 = new SysUserSaveDTO();
        dto2.setUsername("duplicate_user");
        dto2.setPassword("123456");
        dto2.setNickname("用户2");
        dto2.setStatus(1);

        assertThrows(BusinessException.class, () -> {
            sysUserService.save(dto2, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testGetById() {
        SysUserSaveDTO dto = new SysUserSaveDTO();
        dto.setUsername("getbyid_test");
        dto.setPassword("123456");
        dto.setNickname("查询测试");
        dto.setStatus(1);

        Long userId = sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        SysUserVO user = sysUserService.getById(userId, TEST_TENANT_ID);

        assertNotNull(user);
        assertEquals("getbyid_test", user.getUsername());
        assertEquals("查询测试", user.getNickname());
        assertNull(user.getUserCode());
    }

    @Test
    void testUpdateUser() {
        SysUserSaveDTO saveDTO = new SysUserSaveDTO();
        saveDTO.setUsername("update_test");
        saveDTO.setPassword("123456");
        saveDTO.setNickname("更新前");
        saveDTO.setStatus(1);

        Long userId = sysUserService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);
        SysUserVO user = sysUserService.getById(userId, TEST_TENANT_ID);

        SysUserSaveDTO updateDTO = new SysUserSaveDTO();
        updateDTO.setId(userId);
        updateDTO.setUsername("update_test");
        updateDTO.setNickname("更新后");
        updateDTO.setEmail("updated@example.com");
        updateDTO.setStatus(1);
        updateDTO.setVersion(user.getVersion());

        assertDoesNotThrow(() -> {
            sysUserService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });

        SysUserVO updatedUser = sysUserService.getById(userId, TEST_TENANT_ID);
        assertEquals("更新后", updatedUser.getNickname());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysUserSaveDTO saveDTO = new SysUserSaveDTO();
        saveDTO.setUsername("version_test");
        saveDTO.setPassword("123456");
        saveDTO.setNickname("版本测试");
        saveDTO.setStatus(1);

        Long userId = sysUserService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysUserSaveDTO updateDTO = new SysUserSaveDTO();
        updateDTO.setId(userId);
        updateDTO.setUsername("version_test");
        updateDTO.setNickname("更新");
        updateDTO.setStatus(1);
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysUserService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteUser() {
        SysUserSaveDTO dto = new SysUserSaveDTO();
        dto.setUsername("delete_test");
        dto.setPassword("123456");
        dto.setNickname("删除测试");
        dto.setStatus(1);

        Long userId = sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        SysUserVO user = sysUserService.getById(userId, TEST_TENANT_ID);

        assertDoesNotThrow(() -> {
            sysUserService.delete(userId, TEST_TENANT_ID, user.getVersion(), TEST_USER_ID);
        });

        assertThrows(BusinessException.class, () -> {
            sysUserService.getById(userId, TEST_TENANT_ID);
        });
    }

    @Test
    void testDeleteWithWrongVersion() {
        SysUserSaveDTO dto = new SysUserSaveDTO();
        dto.setUsername("delete_version_test");
        dto.setPassword("123456");
        dto.setNickname("删除版本测试");
        dto.setStatus(1);

        Long userId = sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        assertThrows(OptimisticLockException.class, () -> {
            sysUserService.delete(userId, TEST_TENANT_ID, 999, TEST_USER_ID);
        });
    }

    @Test
    void testPageList() {
        for (int i = 1; i <= 5; i++) {
            SysUserSaveDTO dto = new SysUserSaveDTO();
            dto.setUsername("page_test_" + i);
            dto.setPassword("123456");
            dto.setNickname("分页测试" + i);
            dto.setStatus(1);
            sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        SysUserQueryDTO query = new SysUserQueryDTO();
        query.setCurrent(1L);
        query.setSize(3L);

        PageResult<SysUserVO> result = sysUserService.pageList(query, TEST_TENANT_ID);

        assertNotNull(result);
        assertTrue(result.getTotal() >= 5);
        assertTrue(result.getRecords().size() <= 3);
    }

    @Test
    void testAssignRoles() {
        SysUserSaveDTO dto = new SysUserSaveDTO();
        dto.setUsername("role_test");
        dto.setPassword("123456");
        dto.setNickname("角色测试");
        dto.setStatus(1);

        Long userId = sysUserService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        assertDoesNotThrow(() -> {
            sysUserService.assignRoles(userId, Arrays.asList(1L, 2L), TEST_TENANT_ID, TEST_USER_ID);
        });
    }
}
