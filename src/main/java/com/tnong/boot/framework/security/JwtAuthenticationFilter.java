package com.tnong.boot.framework.security;

import com.tnong.boot.common.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 从请求头中获取 Token
        String token = getTokenFromRequest(request);
        
        if (StringUtils.hasText(token) && JwtUtil.validateToken(token)) {
            // Token 有效，解析并设置用户上下文
            LoginUser loginUser = new LoginUser();
            loginUser.setId(JwtUtil.getUserId(token));
            loginUser.setTenantId(JwtUtil.getTenantId(token));
            loginUser.setUsername(JwtUtil.getUsername(token));
            loginUser.setToken(token);
            
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
