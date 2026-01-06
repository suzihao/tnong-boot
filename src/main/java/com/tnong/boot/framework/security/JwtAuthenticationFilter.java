package com.tnong.boot.framework.security;

import com.tnong.boot.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * JWT 认证过滤器
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final PermissionService permissionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 从请求头中获取 Token
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && JwtUtil.validateToken(token)) {
            // Token 有效，解析并设置用户上下文
            LoginUser loginUser = new LoginUser();
            Long userId = JwtUtil.getUserId(token);
            Long tenantId = JwtUtil.getTenantId(token);
            loginUser.setId(userId);
            loginUser.setTenantId(tenantId);
            loginUser.setUsername(JwtUtil.getUsername(token));
            loginUser.setToken(token);

            // 加载用户权限
            try {
                Set<String> permissions = permissionService.getUserPermissions(tenantId, userId);
                Boolean isSuperAdmin = permissionService.isSuperAdmin(tenantId, userId);
                loginUser.setPermissions(permissions);
                loginUser.setIsSuperAdmin(isSuperAdmin);
            } catch (Exception e) {
                // 权限加载失败不影响认证，但权限集合为空
                loginUser.setPermissions(new HashSet<>());
                loginUser.setIsSuperAdmin(false);
            }

            UserContext.set(loginUser);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清除上下文
            UserContext.clear();
        }
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
