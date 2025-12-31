package com.tnong.boot.system.role.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.role.domain.dto.RoleMenuAssignDTO;
import com.tnong.boot.system.role.domain.dto.SysRoleSaveDTO;
import com.tnong.boot.system.role.domain.vo.SysRoleVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysRoleServiceTest {

    @Autowired
    private SysRoleService sysRoleService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveRole() {
        SysRoleSaveDTO dto = new SysRoleSaveDTO();
        dto.setCode("test_role");
        dto.setName("测试角色");
        dto.setDataScope(1);
        dto.setStatus(1);

        Long roleId = sysRoleService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(roleId);
        assertTrue(roleId > 0);
    }

    @Test
    void testSaveRoleWithDuplicateCode() {
        SysRoleSaveDTO dto1 = new SysRoleSaveDTO();
        dto1.setCode("duplicate_role");
        dto1.setName("角色1");
        dto1.setDataScope(1);
        sysRoleService.save(dto1, TEST_TENANT_ID, TEST_USER_ID);

        SysRoleSaveDTO dto2 = new SysRoleSaveDTO();
        dto2.setCode("duplicate_role");
        dto2.setName("角色2");
        dto2.setDataScope(1);

        assertThrows(BusinessException.class, () -> {
            sysRoleService.save(dto2, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateRole() {
        SysRoleSaveDTO saveDTO = new SysRoleSaveDTO();
        saveDTO.setCode("update_role");
        saveDTO.setName("更新前");
        saveDTO.setDataScope(1);
        Long roleId = sysRoleService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        List<SysRoleVO> roles = sysRoleService.list(TEST_TENANT_ID);
        SysRoleVO role = roles.stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElse(null);
        assertNotNull(role);

        SysRoleSaveDTO updateDTO = new SysRoleSaveDTO();
        updateDTO.setId(roleId);
        updateDTO.setName("更新后");
        updateDTO.setRemark("测试备注");
        updateDTO.setVersion(role.getVersion());

        assertDoesNotThrow(() -> {
            sysRoleService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateRoleCodeNotAllowed() {
        SysRoleSaveDTO saveDTO = new SysRoleSaveDTO();
        saveDTO.setCode("code_test");
        saveDTO.setName("编码测试");
        saveDTO.setDataScope(1);
        Long roleId = sysRoleService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        List<SysRoleVO> roles = sysRoleService.list(TEST_TENANT_ID);
        SysRoleVO role = roles.stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElse(null);
        assertNotNull(role);

        SysRoleSaveDTO updateDTO = new SysRoleSaveDTO();
        updateDTO.setId(roleId);
        updateDTO.setCode("new_code");
        updateDTO.setName("编码测试");
        updateDTO.setVersion(role.getVersion());

        assertThrows(BusinessException.class, () -> {
            sysRoleService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysRoleSaveDTO saveDTO = new SysRoleSaveDTO();
        saveDTO.setCode("version_test");
        saveDTO.setName("版本测试");
        saveDTO.setDataScope(1);
        Long roleId = sysRoleService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysRoleSaveDTO updateDTO = new SysRoleSaveDTO();
        updateDTO.setId(roleId);
        updateDTO.setName("更新");
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysRoleService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteRole() {
        SysRoleSaveDTO dto = new SysRoleSaveDTO();
        dto.setCode("delete_test");
        dto.setName("删除测试");
        dto.setDataScope(1);
        Long roleId = sysRoleService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        List<SysRoleVO> roles = sysRoleService.list(TEST_TENANT_ID);
        SysRoleVO role = roles.stream()
                .filter(r -> r.getId().equals(roleId))
                .findFirst()
                .orElse(null);
        assertNotNull(role);

        assertDoesNotThrow(() -> {
            sysRoleService.delete(roleId, TEST_TENANT_ID, role.getVersion(), TEST_USER_ID);
        });

        List<SysRoleVO> afterDelete = sysRoleService.list(TEST_TENANT_ID);
        boolean exists = afterDelete.stream().anyMatch(r -> r.getId().equals(roleId));
        assertFalse(exists);
    }

    @Test
    void testDeleteWithWrongVersion() {
        SysRoleSaveDTO dto = new SysRoleSaveDTO();
        dto.setCode("delete_version_test");
        dto.setName("删除版本测试");
        dto.setDataScope(1);
        Long roleId = sysRoleService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        assertThrows(OptimisticLockException.class, () -> {
            sysRoleService.delete(roleId, TEST_TENANT_ID, 999, TEST_USER_ID);
        });
    }

    @Test
    void testListRoles() {
        for (int i = 1; i <= 3; i++) {
            SysRoleSaveDTO dto = new SysRoleSaveDTO();
            dto.setCode("list_test_" + i);
            dto.setName("列表测试" + i);
            dto.setDataScope(1);
            sysRoleService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        }

        List<SysRoleVO> roles = sysRoleService.list(TEST_TENANT_ID);
        assertNotNull(roles);
        assertTrue(roles.size() >= 3);
    }

    @Test
    void testAssignMenus() {
        SysRoleSaveDTO dto = new SysRoleSaveDTO();
        dto.setCode("menu_test");
        dto.setName("菜单测试");
        dto.setDataScope(1);
        Long roleId = sysRoleService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        RoleMenuAssignDTO assignDTO = new RoleMenuAssignDTO();
        assignDTO.setMenuIds(Arrays.asList(1L, 2L, 3L));

        assertDoesNotThrow(() -> {
            sysRoleService.assignMenus(roleId, TEST_TENANT_ID, assignDTO, TEST_USER_ID);
        });
    }
}
