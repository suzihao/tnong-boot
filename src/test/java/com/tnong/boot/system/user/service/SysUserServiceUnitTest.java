package com.tnong.boot.system.user.service;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.util.PasswordUtil;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.user.domain.dto.SysUserQueryDTO;
import com.tnong.boot.system.user.domain.dto.SysUserSaveDTO;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.domain.vo.SysUserVO;
import com.tnong.boot.system.user.mapper.SysUserMapper;
import com.tnong.boot.system.user.mapper.SysUserRoleMapper;
import com.tnong.boot.system.user.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试（使用Mock）
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务单元测试")
class SysUserServiceUnitTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysUserRoleMapper sysUserRoleMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;
    private SysUserSaveDTO saveDTO;
    private Long tenantId = 1L;
    private Long currentUserId = 1L;

    @BeforeEach
    void setUp() {
        // 准备测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUserCode(1000000001L);
        testUser.setTenantId(tenantId);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtil.encode("123456"));
        testUser.setNickname("测试用户");
        testUser.setMobile("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(CommonConstant.STATUS_ENABLE);
        testUser.setVersion(0);

        // 准备保存DTO
        saveDTO = new SysUserSaveDTO();
        saveDTO.setUsername("newuser");
        saveDTO.setPassword("123456");
        saveDTO.setNickname("新用户");
        saveDTO.setMobile("13900139000");
        saveDTO.setEmail("newuser@example.com");
    }

    @Test
    @DisplayName("分页查询 - 成功")
    void testPageList_Success() {
        // Given
        SysUserQueryDTO query = new SysUserQueryDTO();
        query.setPage(1L);
        query.setSize(10L);

        SysUserVO vo = new SysUserVO();
        vo.setId(1L);
        vo.setUsername("testuser");
        vo.setNickname("测试用户");

        when(sysUserMapper.selectPageList(query, tenantId)).thenReturn(Collections.singletonList(vo));
        when(sysUserMapper.selectCount(query, tenantId)).thenReturn(1L);

        // When
        PageResult<SysUserVO> result = sysUserService.pageList(query, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("testuser", result.getRecords().get(0).getUsername());

        verify(sysUserMapper).selectPageList(query, tenantId);
        verify(sysUserMapper).selectCount(query, tenantId);
    }

    @Test
    @DisplayName("根据ID查询 - 成功")
    void testGetById_Success() {
        // Given
        when(sysUserMapper.selectById(1L, tenantId)).thenReturn(testUser);

        // When
        SysUserVO result = sysUserService.getById(1L, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getNickname(), result.getNickname());

        verify(sysUserMapper).selectById(1L, tenantId);
    }

    @Test
    @DisplayName("根据ID查询 - 用户不存在")
    void testGetById_NotFound() {
        // Given
        when(sysUserMapper.selectById(999L, tenantId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.getById(999L, tenantId);
        });

        assertEquals("用户不存在", exception.getMessage());
        verify(sysUserMapper).selectById(999L, tenantId);
    }

    @Test
    @DisplayName("新增用户 - 成功")
    void testSave_Success() {
        // Given
        when(sysUserMapper.selectByUsername("newuser", tenantId)).thenReturn(null);
        when(sysUserMapper.insert(any(SysUser.class))).thenAnswer(invocation -> {
            SysUser user = invocation.getArgument(0);
            user.setId(2L); // 模拟数据库生成ID
            return 1;
        });

        // When
        Long userId = sysUserService.save(saveDTO, tenantId, currentUserId);

        // Then
        assertNotNull(userId);
        assertEquals(2L, userId);

        verify(sysUserMapper).selectByUsername("newuser", tenantId);
        verify(sysUserMapper).insert(argThat(user -> {
            // 验证密码已加密
            assertTrue(user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$"));
            // 验证其他字段
            assertEquals("newuser", user.getUsername());
            assertEquals("新用户", user.getNickname());
            assertEquals(tenantId, user.getTenantId());
            assertEquals(currentUserId, user.getCreatedUser());
            assertEquals(CommonConstant.STATUS_ENABLE, user.getStatus());
            assertNotNull(user.getUserCode()); // 雪花ID已生成
            return true;
        }));
    }

    @Test
    @DisplayName("新增用户 - 用户名已存在")
    void testSave_UsernameExists() {
        // Given
        when(sysUserMapper.selectByUsername("newuser", tenantId)).thenReturn(testUser);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.save(saveDTO, tenantId, currentUserId);
        });

        assertEquals("用户名已存在", exception.getMessage());
        verify(sysUserMapper).selectByUsername("newuser", tenantId);
        verify(sysUserMapper, never()).insert(any());
    }

    @Test
    @DisplayName("新增用户 - 密码为空")
    void testSave_EmptyPassword() {
        // Given
        saveDTO.setPassword("");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysUserService.save(saveDTO, tenantId, currentUserId);
        });

        assertEquals("密码不能为空", exception.getMessage());
        verify(sysUserMapper, never()).insert(any());
    }

    @Test
    @DisplayName("更新用户 - 成功")
    void testUpdate_Success() {
        // Given
        saveDTO.setId(1L);
        saveDTO.setVersion(0);
        saveDTO.setNickname("更新后的昵称");

        when(sysUserMapper.selectById(1L, tenantId)).thenReturn(testUser);
        when(sysUserMapper.updateByIdWithVersion(any(SysUser.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> {
            sysUserService.update(saveDTO, tenantId, currentUserId);
        });

        // Then
        verify(sysUserMapper).selectById(1L, tenantId);
        verify(sysUserMapper).updateByIdWithVersion(argThat(user -> {
            assertEquals(1L, user.getId());
            assertEquals("更新后的昵称", user.getNickname());
            assertEquals(tenantId, user.getTenantId());
            assertEquals(currentUserId, user.getUpdatedUser());
            return true;
        }));
    }

    @Test
    @DisplayName("更新用户 - 乐观锁冲突")
    void testUpdate_OptimisticLockConflict() {
        // Given
        saveDTO.setId(1L);
        saveDTO.setVersion(0);

        when(sysUserMapper.selectById(1L, tenantId)).thenReturn(testUser);
        when(sysUserMapper.updateByIdWithVersion(any(SysUser.class))).thenReturn(0); // 更新失败

        // When & Then
        assertThrows(OptimisticLockException.class, () -> {
            sysUserService.update(saveDTO, tenantId, currentUserId);
        });

        verify(sysUserMapper).updateByIdWithVersion(any());
    }

    @Test
    @DisplayName("删除用户 - 成功")
    void testDelete_Success() {
        // Given
        when(sysUserMapper.deleteById(1L, tenantId, 0, currentUserId)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> {
            sysUserService.delete(1L, tenantId, 0, currentUserId);
        });

        // Then
        verify(sysUserMapper).deleteById(1L, tenantId, 0, currentUserId);
    }

    @Test
    @DisplayName("删除用户 - 乐观锁冲突")
    void testDelete_OptimisticLockConflict() {
        // Given
        when(sysUserMapper.deleteById(1L, tenantId, 0, currentUserId)).thenReturn(0);

        // When & Then
        OptimisticLockException exception = assertThrows(OptimisticLockException.class, () -> {
            sysUserService.delete(1L, tenantId, 0, currentUserId);
        });

        assertEquals("删除失败，数据可能已被修改", exception.getMessage());
        verify(sysUserMapper).deleteById(1L, tenantId, 0, currentUserId);
    }

    @Test
    @DisplayName("查询用户角色 - 成功")
    void testGetUserRoleIds_Success() {
        // Given
        List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
        when(sysUserRoleMapper.selectRoleIdsByUserId(tenantId, 1L)).thenReturn(roleIds);

        // When
        List<Long> result = sysUserService.getUserRoleIds(1L, tenantId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.containsAll(roleIds));

        verify(sysUserRoleMapper).selectRoleIdsByUserId(tenantId, 1L);
    }

    @Test
    @DisplayName("分配角色 - 成功")
    void testAssignRoles_Success() {
        // Given
        List<Long> roleIds = Arrays.asList(1L, 2L, 3L);
        when(sysUserRoleMapper.deleteByUserId(tenantId, 1L, currentUserId)).thenReturn(2);
        when(sysUserRoleMapper.insertBatch(tenantId, 1L, roleIds, currentUserId)).thenReturn(3);

        // When
        assertDoesNotThrow(() -> {
            sysUserService.assignRoles(1L, roleIds, tenantId, currentUserId);
        });

        // Then
        verify(sysUserRoleMapper).deleteByUserId(tenantId, 1L, currentUserId);
        verify(sysUserRoleMapper).insertBatch(tenantId, 1L, roleIds, currentUserId);
    }

    @Test
    @DisplayName("分配角色 - 清空角色")
    void testAssignRoles_ClearRoles() {
        // Given
        when(sysUserRoleMapper.deleteByUserId(tenantId, 1L, currentUserId)).thenReturn(2);

        // When
        assertDoesNotThrow(() -> {
            sysUserService.assignRoles(1L, Collections.emptyList(), tenantId, currentUserId);
        });

        // Then
        verify(sysUserRoleMapper).deleteByUserId(tenantId, 1L, currentUserId);
        verify(sysUserRoleMapper, never()).insertBatch(anyLong(), anyLong(), anyList(), anyLong());
    }
}
