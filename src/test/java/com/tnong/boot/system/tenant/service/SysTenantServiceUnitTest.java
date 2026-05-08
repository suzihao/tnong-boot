package com.tnong.boot.system.tenant.service;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.exception.OptimisticLockException;
import com.tnong.boot.common.web.PageResult;
import com.tnong.boot.system.tenant.domain.dto.SysTenantQueryDTO;
import com.tnong.boot.system.tenant.domain.dto.SysTenantSaveDTO;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import com.tnong.boot.system.tenant.domain.vo.SysTenantVO;
import com.tnong.boot.system.tenant.mapper.SysTenantMapper;
import com.tnong.boot.system.tenant.service.impl.SysTenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 租户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("租户服务单元测试")
class SysTenantServiceUnitTest {

    @Mock
    private SysTenantMapper sysTenantMapper;

    @InjectMocks
    private SysTenantServiceImpl sysTenantService;

    private SysTenant testTenant;
    private SysTenantSaveDTO saveDTO;
    private Long currentUserId = 1L;

    @BeforeEach
    void setUp() {
        // 准备测试租户
        testTenant = new SysTenant();
        testTenant.setId(1L);
        testTenant.setTenantCode(1L);
        testTenant.setName("测试租户");
        testTenant.setContactName("张三");
        testTenant.setContactPhone("13800138000");
        testTenant.setContactEmail("test@example.com");
        testTenant.setStatus(CommonConstant.STATUS_ENABLE);
        testTenant.setExpireTime(LocalDateTime.now().plusYears(1));
        testTenant.setVersion(0);

        // 准备保存DTO
        saveDTO = new SysTenantSaveDTO();
        saveDTO.setId(2L);
        saveDTO.setName("新租户");
        saveDTO.setContactName("李四");
        saveDTO.setContactPhone("13900139000");
        saveDTO.setContactEmail("new@example.com");
    }

    @Test
    @DisplayName("分页查询 - 成功")
    void testPageList_Success() {
        // Given
        SysTenantQueryDTO query = new SysTenantQueryDTO();
        query.setPage(1L);
        query.setSize(10L);

        when(sysTenantMapper.selectPageList(query)).thenReturn(Collections.singletonList(testTenant));
        when(sysTenantMapper.selectCount(query)).thenReturn(1L);

        // When
        PageResult<SysTenantVO> result = sysTenantService.pageList(query);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals("测试租户", result.getRecords().get(0).getName());

        verify(sysTenantMapper).selectPageList(query);
        verify(sysTenantMapper).selectCount(query);
    }

    @Test
    @DisplayName("根据ID查询 - 成功")
    void testGetById_Success() {
        // Given
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);

        // When
        SysTenantVO result = sysTenantService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testTenant.getId(), result.getId());
        assertEquals(testTenant.getName(), result.getName());
        assertEquals(testTenant.getContactName(), result.getContactName());

        verify(sysTenantMapper).selectById(1L);
    }

    @Test
    @DisplayName("根据ID查询 - 租户不存在")
    void testGetById_NotFound() {
        // Given
        when(sysTenantMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.getById(999L);
        });

        assertEquals("租户不存在", exception.getMessage());
        verify(sysTenantMapper).selectById(999L);
    }

    @Test
    @DisplayName("新增租户 - 成功")
    void testSave_Success() {
        // Given
        when(sysTenantMapper.selectById(2L)).thenReturn(null);
        when(sysTenantMapper.insert(any(SysTenant.class))).thenReturn(1);

        // When
        Long tenantId = sysTenantService.save(saveDTO, currentUserId);

        // Then
        assertNotNull(tenantId);
        assertEquals(2L, tenantId);

        verify(sysTenantMapper).selectById(2L);
        verify(sysTenantMapper).insert(argThat(tenant -> {
            assertEquals(2L, tenant.getId());
            assertEquals("新租户", tenant.getName());
            assertEquals("李四", tenant.getContactName());
            assertEquals(currentUserId, tenant.getCreatedUser());
            assertEquals(currentUserId, tenant.getUpdatedUser());
            assertEquals(CommonConstant.STATUS_ENABLE, tenant.getStatus());
            return true;
        }));
    }

    @Test
    @DisplayName("新增租户 - ID为空")
    void testSave_NullId() {
        // Given
        saveDTO.setId(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.save(saveDTO, currentUserId);
        });

        assertEquals("租户ID不能为空", exception.getMessage());
        verify(sysTenantMapper, never()).insert(any());
    }

    @Test
    @DisplayName("新增租户 - ID已存在")
    void testSave_IdExists() {
        // Given
        when(sysTenantMapper.selectById(2L)).thenReturn(testTenant);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.save(saveDTO, currentUserId);
        });

        assertEquals("租户ID已存在", exception.getMessage());
        verify(sysTenantMapper).selectById(2L);
        verify(sysTenantMapper, never()).insert(any());
    }

    @Test
    @DisplayName("更新租户 - 成功")
    void testUpdate_Success() {
        // Given
        saveDTO.setId(1L);
        saveDTO.setVersion(0);
        saveDTO.setName("更新后的租户名");

        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysTenantMapper.updateByIdWithVersion(any(SysTenant.class))).thenReturn(1);

        // When
        assertDoesNotThrow(() -> {
            sysTenantService.update(saveDTO, currentUserId);
        });

        // Then
        verify(sysTenantMapper).selectById(1L);
        verify(sysTenantMapper).updateByIdWithVersion(argThat(tenant -> {
            assertEquals(1L, tenant.getId());
            assertEquals("更新后的租户名", tenant.getName());
            assertEquals(currentUserId, tenant.getUpdatedUser());
            assertNull(tenant.getTenantCode()); // 确保不修改tenantCode
            return true;
        }));
    }

    @Test
    @DisplayName("更新租户 - ID为空")
    void testUpdate_NullId() {
        // Given
        saveDTO.setId(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.update(saveDTO, currentUserId);
        });

        assertEquals("租户ID不能为空", exception.getMessage());
        verify(sysTenantMapper, never()).updateByIdWithVersion(any());
    }

    @Test
    @DisplayName("更新租户 - 版本号为空")
    void testUpdate_NullVersion() {
        // Given
        saveDTO.setId(1L);
        saveDTO.setVersion(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.update(saveDTO, currentUserId);
        });

        assertEquals("版本号不能为空", exception.getMessage());
        verify(sysTenantMapper, never()).updateByIdWithVersion(any());
    }

    @Test
    @DisplayName("更新租户 - 租户不存在")
    void testUpdate_TenantNotFound() {
        // Given
        saveDTO.setId(999L);
        saveDTO.setVersion(0);

        when(sysTenantMapper.selectById(999L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.update(saveDTO, currentUserId);
        });

        assertEquals("租户不存在或已删除", exception.getMessage());
        verify(sysTenantMapper).selectById(999L);
        verify(sysTenantMapper, never()).updateByIdWithVersion(any());
    }

    @Test
    @DisplayName("更新租户 - 乐观锁冲突")
    void testUpdate_OptimisticLockConflict() {
        // Given
        saveDTO.setId(1L);
        saveDTO.setVersion(0);

        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysTenantMapper.updateByIdWithVersion(any(SysTenant.class))).thenReturn(0); // 更新失败

        // When & Then
        assertThrows(OptimisticLockException.class, () -> {
            sysTenantService.update(saveDTO, currentUserId);
        });

        verify(sysTenantMapper).updateByIdWithVersion(any());
    }

    @Test
    @DisplayName("删除租户 - 成功")
    void testDelete_Success() {
        // Given
        when(sysTenantMapper.deleteById(1L, 0, currentUserId)).thenReturn(1);

        // When
        assertDoesNotThrow(() -> {
            sysTenantService.delete(1L, 0, currentUserId);
        });

        // Then
        verify(sysTenantMapper).deleteById(1L, 0, currentUserId);
    }

    @Test
    @DisplayName("删除租户 - 版本号为空")
    void testDelete_NullVersion() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            sysTenantService.delete(1L, null, currentUserId);
        });

        assertEquals("版本号不能为空", exception.getMessage());
        verify(sysTenantMapper, never()).deleteById(anyLong(), anyInt(), anyLong());
    }

    @Test
    @DisplayName("删除租户 - 乐观锁冲突")
    void testDelete_OptimisticLockConflict() {
        // Given
        when(sysTenantMapper.deleteById(1L, 0, currentUserId)).thenReturn(0);

        // When & Then
        OptimisticLockException exception = assertThrows(OptimisticLockException.class, () -> {
            sysTenantService.delete(1L, 0, currentUserId);
        });

        assertEquals("删除失败，数据可能已被修改", exception.getMessage());
        verify(sysTenantMapper).deleteById(1L, 0, currentUserId);
    }
}
