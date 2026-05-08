package com.tnong.boot.system.auth.service;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.util.JwtUtil;
import com.tnong.boot.common.util.PasswordUtil;
import com.tnong.boot.system.auth.domain.dto.LoginDTO;
import com.tnong.boot.system.auth.domain.vo.LoginVO;
import com.tnong.boot.system.auth.service.impl.AuthServiceImpl;
import com.tnong.boot.system.log.mapper.SysLoginLogMapper;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import com.tnong.boot.system.tenant.mapper.SysTenantMapper;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.mapper.SysUserMapper;
import com.tnong.boot.system.user.mapper.SysUserThirdAccountMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 认证服务测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SysTenantMapper sysTenantMapper;

    @Mock
    private SysLoginLogMapper sysLoginLogMapper;

    @Mock
    private SysUserThirdAccountMapper sysUserThirdAccountMapper;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthServiceImpl authService;

    private SysUser testUser;
    private SysTenant testTenant;
    private LoginDTO loginDTO;

    @BeforeEach
    void setUp() {
        // 准备测试租户
        testTenant = new SysTenant();
        testTenant.setId(1L);
        testTenant.setName("测试租户");
        testTenant.setStatus(CommonConstant.STATUS_ENABLE);

        // 准备测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUserCode(1000000001L);
        testUser.setTenantId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(PasswordUtil.encode("123456")); // BCrypt加密
        testUser.setNickname("测试用户");
        testUser.setStatus(CommonConstant.STATUS_ENABLE);

        // 准备登录DTO
        loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("123456");
        loginDTO.setTenantId(1L);

        // Mock request
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("Test-Agent");
    }

    @Test
    @DisplayName("登录成功 - 正确的用户名和密码")
    void testLogin_Success() {
        // Given
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysUserMapper.selectByUsername("testuser", 1L)).thenReturn(testUser);
        when(sysLoginLogMapper.insert(any())).thenReturn(1);

        // When
        LoginVO result = authService.login(loginDTO, request);

        // Then
        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(testUser.getUsername(), result.getUsername());
        assertEquals(testUser.getNickname(), result.getNickname());
        assertEquals(1L, result.getTenantId());

        // 验证Token有效性
        assertTrue(JwtUtil.validateToken(result.getToken()));
        assertEquals(testUser.getId(), JwtUtil.getUserId(result.getToken()));

        // 验证方法调用
        verify(sysTenantMapper).selectById(1L);
        verify(sysUserMapper).selectByUsername("testuser", 1L);
        verify(sysLoginLogMapper, times(1)).insert(any()); // 只记录成功日志
    }

    @Test
    @DisplayName("登录失败 - 用户名为空")
    void testLogin_EmptyUsername() {
        // Given
        loginDTO.setUsername("");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("用户名不能为空", exception.getMessage());
        verify(sysUserMapper, never()).selectByUsername(anyString(), anyLong());
    }

    @Test
    @DisplayName("登录失败 - 密码为空")
    void testLogin_EmptyPassword() {
        // Given
        loginDTO.setPassword("");

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("密码不能为空", exception.getMessage());
        verify(sysUserMapper, never()).selectByUsername(anyString(), anyLong());
    }

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLogin_UserNotFound() {
        // Given
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysUserMapper.selectByUsername("testuser", 1L)).thenReturn(null);
        when(sysLoginLogMapper.insert(any())).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
        verify(sysLoginLogMapper).insert(any()); // 记录失败日志
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLogin_WrongPassword() {
        // Given
        loginDTO.setPassword("wrongpassword");
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysUserMapper.selectByUsername("testuser", 1L)).thenReturn(testUser);
        when(sysLoginLogMapper.insert(any())).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("用户名或密码错误", exception.getMessage());
        verify(sysLoginLogMapper).insert(any()); // 记录失败日志
    }

    @Test
    @DisplayName("登录失败 - 用户已被禁用")
    void testLogin_UserDisabled() {
        // Given
        testUser.setStatus(CommonConstant.STATUS_DISABLE);
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysUserMapper.selectByUsername("testuser", 1L)).thenReturn(testUser);
        when(sysLoginLogMapper.insert(any())).thenReturn(1);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("用户已被禁用", exception.getMessage());
        verify(sysLoginLogMapper).insert(any()); // 记录失败日志
    }

    @Test
    @DisplayName("登录失败 - 租户不存在")
    void testLogin_TenantNotFound() {
        // Given
        when(sysTenantMapper.selectById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("租户不存在", exception.getMessage());
        verify(sysUserMapper, never()).selectByUsername(anyString(), anyLong());
    }

    @Test
    @DisplayName("登录失败 - 租户已被禁用")
    void testLogin_TenantDisabled() {
        // Given
        testTenant.setStatus(CommonConstant.STATUS_DISABLE);
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.login(loginDTO, request);
        });

        assertEquals("租户已被禁用", exception.getMessage());
        verify(sysUserMapper, never()).selectByUsername(anyString(), anyLong());
    }

    @Test
    @DisplayName("登录成功 - 使用默认租户")
    void testLogin_DefaultTenant() {
        // Given
        loginDTO.setTenantId(null); // 不传租户ID
        when(sysTenantMapper.selectById(1L)).thenReturn(testTenant);
        when(sysUserMapper.selectByUsername("testuser", 1L)).thenReturn(testUser);
        when(sysLoginLogMapper.insert(any())).thenReturn(1);

        // When
        LoginVO result = authService.login(loginDTO, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getTenantId()); // 使用默认租户ID=1
        verify(sysTenantMapper).selectById(1L);
    }

    @Test
    @DisplayName("退出登录 - 成功")
    void testLogout_Success() {
        // When & Then
        assertDoesNotThrow(() -> {
            authService.logout();
        });
    }

    @Test
    @DisplayName("密码验证 - BCrypt算法正确性")
    void testPasswordVerification() {
        // Given
        String rawPassword = "123456";
        String encodedPassword = PasswordUtil.encode(rawPassword);

        // When
        boolean matches = PasswordUtil.matches(rawPassword, encodedPassword);

        // Then
        assertTrue(matches);
        assertFalse(PasswordUtil.matches("wrongpassword", encodedPassword));
    }

    @Test
    @DisplayName("Token生成和验证 - 正确性")
    void testTokenGeneration() {
        // Given
        Long userId = 1L;
        String username = "testuser";
        Long tenantId = 1L;

        // When
        String token = JwtUtil.generateToken(userId, username, tenantId);

        // Then
        assertNotNull(token);
        assertTrue(JwtUtil.validateToken(token));
        assertEquals(userId, JwtUtil.getUserId(token));
        assertEquals(username, JwtUtil.getUsername(token));
        assertEquals(tenantId, JwtUtil.getTenantId(token));
    }
}
