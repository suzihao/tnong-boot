package com.tnong.boot.system.auth.service.impl;

import com.tnong.boot.common.constant.CommonConstant;
import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.util.JwtUtil;
import com.tnong.boot.common.util.PasswordUtil;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.auth.domain.dto.LoginDTO;
import com.tnong.boot.system.auth.domain.vo.LoginVO;
import com.tnong.boot.system.auth.service.AuthService;
import com.tnong.boot.system.log.domain.entity.SysLoginLog;
import com.tnong.boot.system.log.mapper.SysLoginLogMapper;
import com.tnong.boot.system.tenant.domain.entity.SysTenant;
import com.tnong.boot.system.tenant.mapper.SysTenantMapper;
import com.tnong.boot.system.user.domain.entity.SysUser;
import com.tnong.boot.system.user.mapper.SysUserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysTenantMapper sysTenantMapper;
    private final SysLoginLogMapper sysLoginLogMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        // 1. 参数校验
        if (!StringUtils.hasText(loginDTO.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (!StringUtils.hasText(loginDTO.getPassword())) {
            throw new BusinessException("密码不能为空");
        }

        // 2. 获取租户ID
        Long tenantId = getTenantId(loginDTO.getTenantCode());
        
        // 3. 查询用户
        SysUser user = sysUserMapper.selectByUsername(loginDTO.getUsername(), tenantId);
        
        // 4. 验证用户
        if (user == null) {
            recordLoginLog(null, tenantId, loginDTO.getUsername(), 0, "用户不存在", request);
            throw new BusinessException("用户名或密码错误");
        }
        
        // 5. 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 0, "密码错误", request);
            throw new BusinessException("用户名或密码错误");
        }
        
        // 6. 检查用户状态
        if (!CommonConstant.STATUS_ENABLE.equals(user.getStatus())) {
            recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 0, "用户已被禁用", request);
            throw new BusinessException("用户已被禁用");
        }
        
        // 7. 生成 Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), tenantId);
        
        // 8. 更新最后登录信息
        updateLastLogin(user, request);
        
        // 9. 记录登录成功日志
        recordLoginLog(user.getId(), tenantId, loginDTO.getUsername(), 1, "登录成功", request);
        
        // 10. 构造返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getId());
        loginVO.setTenantId(tenantId);
        loginVO.setUsername(user.getUsername());
        loginVO.setNickname(user.getNickname());
        
        return loginVO;
    }

    @Override
    public void logout() {
        // 清除用户上下文
        UserContext.clear();
        // 可以在这里记录退出日志
        log.info("用户退出登录");
    }

    /**
     * 获取租户ID
     */
    private Long getTenantId(String tenantCode) {
        // 如果没有传租户编码，使用默认租户
        if (!StringUtils.hasText(tenantCode)) {
            tenantCode = "DEFAULT";
        }
        
        SysTenant tenant = sysTenantMapper.selectByTenantCode(tenantCode);
        if (tenant == null) {
            throw new BusinessException("租户不存在");
        }
        
        if (!CommonConstant.STATUS_ENABLE.equals(tenant.getStatus())) {
            throw new BusinessException("租户已被禁用");
        }
        
        return tenant.getId();
    }

    /**
     * 更新最后登录信息
     */
    private void updateLastLogin(SysUser user, HttpServletRequest request) {
        try {
            user.setLastLoginIp(getIpAddress(request));
            // 这里可以调用 Mapper 更新最后登录时间和IP
            // sysUserMapper.updateLastLogin(user);
        } catch (Exception e) {
            log.error("更新最后登录信息失败", e);
        }
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, Long tenantId, String username, Integer status, String msg, HttpServletRequest request) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setTenantId(tenantId);
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginType(0); // 0-账号密码登录
            loginLog.setStatus(status); // 1-成功，0-失败
            loginLog.setIpAddress(getIpAddress(request));
            loginLog.setUserAgent(request.getHeader("User-Agent"));
            loginLog.setMsg(msg);
            
            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
