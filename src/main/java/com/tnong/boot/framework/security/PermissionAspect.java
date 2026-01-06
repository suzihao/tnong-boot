package com.tnong.boot.framework.security;

import com.tnong.boot.common.annotation.RequirePermission;
import com.tnong.boot.common.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

/**
 * 权限校验切面
 * 拦截所有标注了@RequirePermission注解的方法，进行权限校验
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    /**
     * 前置通知：在方法执行前进行权限校验
     */
    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        // 1. 获取当前登录用户
        LoginUser loginUser = UserContext.get();
        if (loginUser == null) {
            throw new PermissionDeniedException("用户未登录");
        }

        // 2. 超级管理员跳过权限校验
        if (Boolean.TRUE.equals(loginUser.getIsSuperAdmin())) {
            log.debug("超级管理员[{}]访问方法[{}]，跳过权限校验",
                    loginUser.getUsername(), joinPoint.getSignature().toShortString());
            return;
        }

        // 3. 获取需要的权限
        String[] requiredPermissions = requirePermission.value();
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            return; // 无需权限，直接放行
        }

        // 4. 获取用户拥有的权限
        Set<String> userPermissions = loginUser.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            log.warn("用户[{}]访问方法[{}]权限不足，用户无任何权限",
                    loginUser.getUsername(), joinPoint.getSignature().toShortString());
            throw new PermissionDeniedException("权限不足，无法访问");
        }

        // 5. 根据逻辑关系进行权限校验
        boolean hasPermission = false;
        if (requirePermission.logical() == RequirePermission.Logical.AND){
            // AND逻辑：需要拥有所有权限
            hasPermission = userPermissions.containsAll(Arrays.asList(requiredPermissions));
        } else {
            // OR逻辑：只需拥有其中一个权限
            hasPermission = Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);
        }

        // 6. 权限校验失败，抛出异常
        if (!hasPermission) {
            log.warn("用户[{}]访问方法[{}]权限不足，需要权限：{}，拥有权限：{}",
                    loginUser.getUsername(),
                    joinPoint.getSignature().toShortString(),
                    Arrays.toString(requiredPermissions),
                    userPermissions);
            throw new PermissionDeniedException("权限不足，无法访问");
        }

        log.debug("用户[{}]访问方法[{}]权限校验通过",
          loginUser.getUsername(), joinPoint.getSignature().toShortString());
    }
}
