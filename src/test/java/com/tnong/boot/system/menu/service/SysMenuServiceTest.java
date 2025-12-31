package com.tnong.boot.system.menu.service;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.system.menu.domain.dto.SysMenuSaveDTO;
import com.tnong.boot.system.menu.domain.vo.MyMenuAndPermVO;
import com.tnong.boot.system.menu.domain.vo.SysMenuVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.tnong.tnongboot.TnongBootApplication.class)
@Transactional
class SysMenuServiceTest {

    @Autowired
    private SysMenuService sysMenuService;

    private static final Long TEST_TENANT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @Test
    void testSaveDirectoryMenu() {
        SysMenuSaveDTO dto = new SysMenuSaveDTO();
        dto.setName("测试目录");
        dto.setType(1);
        dto.setParentId(0L);
        dto.setSort(1);

        Long menuId = sysMenuService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(menuId);
        assertTrue(menuId > 0);
    }

    @Test
    void testSaveButtonWithPerms() {
        SysMenuSaveDTO dto = new SysMenuSaveDTO();
        dto.setName("测试按钮");
        dto.setType(3);
        dto.setPerms("test:button:add");
        dto.setParentId(0L);
        dto.setSort(1);

        Long menuId = sysMenuService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(menuId);
        assertTrue(menuId > 0);
    }

    @Test
    void testSaveButtonWithoutPerms() {
        SysMenuSaveDTO dto = new SysMenuSaveDTO();
        dto.setName("测试按钮");
        dto.setType(3);
        dto.setParentId(0L);

        assertThrows(BusinessException.class, () -> {
            sysMenuService.save(dto, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateMenu() {
        SysMenuSaveDTO saveDTO = new SysMenuSaveDTO();
        saveDTO.setName("更新前");
        saveDTO.setType(1);
        saveDTO.setParentId(0L);
        Long menuId = sysMenuService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        List<SysMenuVO> menus = sysMenuService.tree(TEST_TENANT_ID, TEST_USER_ID);
        SysMenuVO menu = findMenuById(menus, menuId);
        assertNotNull(menu);

        SysMenuSaveDTO updateDTO = new SysMenuSaveDTO();
        updateDTO.setId(menuId);
        updateDTO.setName("更新后");
        updateDTO.setType(1);
        updateDTO.setVersion(menu.getVersion());

        assertDoesNotThrow(() -> {
            sysMenuService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testUpdateWithWrongVersion() {
        SysMenuSaveDTO saveDTO = new SysMenuSaveDTO();
        saveDTO.setName("版本测试");
        saveDTO.setType(1);
        saveDTO.setParentId(0L);
        Long menuId = sysMenuService.save(saveDTO, TEST_TENANT_ID, TEST_USER_ID);

        SysMenuSaveDTO updateDTO = new SysMenuSaveDTO();
        updateDTO.setId(menuId);
        updateDTO.setName("更新");
        updateDTO.setType(1);
        updateDTO.setVersion(999);

        assertThrows(OptimisticLockException.class, () -> {
            sysMenuService.update(updateDTO, TEST_TENANT_ID, TEST_USER_ID);
        });
    }

    @Test
    void testDeleteMenu() {
        SysMenuSaveDTO dto = new SysMenuSaveDTO();
        dto.setName("删除测试");
        dto.setType(1);
        dto.setParentId(0L);
        Long menuId = sysMenuService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        List<SysMenuVO> menus = sysMenuService.tree(TEST_TENANT_ID, TEST_USER_ID);
        SysMenuVO menu = findMenuById(menus, menuId);
        assertNotNull(menu);

        assertDoesNotThrow(() -> {
            sysMenuService.delete(menuId, TEST_TENANT_ID, menu.getVersion(), TEST_USER_ID);
        });
    }

    @Test
    void testDeleteWithWrongVersion() {
        SysMenuSaveDTO dto = new SysMenuSaveDTO();
        dto.setName("删除版本测试");
        dto.setType(1);
        dto.setParentId(0L);
        Long menuId = sysMenuService.save(dto, TEST_TENANT_ID, TEST_USER_ID);

        assertThrows(OptimisticLockException.class, () -> {
            sysMenuService.delete(menuId, TEST_TENANT_ID, 999, TEST_USER_ID);
        });
    }

    @Test
    void testTreeStructure() {
        SysMenuSaveDTO parent = new SysMenuSaveDTO();
        parent.setName("父菜单");
        parent.setType(1);
        parent.setParentId(0L);
        parent.setSort(1);
        Long parentId = sysMenuService.save(parent, TEST_TENANT_ID, TEST_USER_ID);

        SysMenuSaveDTO child = new SysMenuSaveDTO();
        child.setName("子菜单");
        child.setType(2);
        child.setParentId(parentId);
        child.setSort(1);
        sysMenuService.save(child, TEST_TENANT_ID, TEST_USER_ID);

        List<SysMenuVO> tree = sysMenuService.tree(TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(tree);
        assertTrue(tree.size() > 0);
    }

    @Test
    void testCurrentUserMenusAndPerms() {
        MyMenuAndPermVO result = sysMenuService.currentUserMenusAndPerms(TEST_TENANT_ID, TEST_USER_ID);
        assertNotNull(result);
        assertNotNull(result.getMenus());
        assertNotNull(result.getPerms());
    }

    private SysMenuVO findMenuById(List<SysMenuVO> menus, Long id) {
        for (SysMenuVO menu : menus) {
            if (menu.getId().equals(id)) {
                return menu;
            }
            if (menu.getChildren() != null) {
                SysMenuVO found = findMenuById(menu.getChildren(), id);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
