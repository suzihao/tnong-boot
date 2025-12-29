package com.tnong.boot.framework.log;

import com.tnong.boot.common.annotation.Log;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.log.domain.entity.SysOperLog;
import com.tnong.boot.system.log.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * 操作日志拦截器（基于 Spring MVC HandlerInterceptor）
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final SysOperLogService sysOperLogService;

    /**
     * 线程本地变量，存储开始时间
     */
    private final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        startTimeThreadLocal.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler, Exception ex) throws Exception {
        try {
            // 只处理带有 @Log 注解的方法
            if (!(handler instanceof HandlerMethod handlerMethod)) {
                return;
            }

            Log logAnnotation = handlerMethod.getMethodAnnotation(Log.class);
            if (logAnnotation == null) {
                return;
            }

            // 构建日志对象
            SysOperLog operLog = new SysOperLog();

            // 设置用户信息
            try {
                Long userId = UserContext.getUserId();
                Long tenantId = UserContext.getTenantId();
                String username = UserContext.getUsername();
                if (userId != null) {
                    operLog.setUserId(userId);
                    operLog.setTenantId(tenantId);
                    operLog.setUsername(username);
                }
            } catch (Exception e) {
                log.warn("获取登录用户信息失败", e);
            }

            // 设置模块和业务类型
            operLog.setModule(logAnnotation.module());
            operLog.setBusinessType(logAnnotation.businessType().getCode());

            // 设置请求信息
            operLog.setRequestMethod(request.getMethod());
            operLog.setRequestUrl(request.getRequestURI());
            operLog.setRequestIp(getIpAddr(request));
            operLog.setUserAgent(request.getHeader("User-Agent"));

            // 设置方法名
            String className = handlerMethod.getBeanType().getName();
            String methodName = handlerMethod.getMethod().getName();
            operLog.setMethod(className + "." + methodName);

            // 设置请求参数
            if (logAnnotation.saveRequestData()) {
                operLog.setRequestParams(getRequestParams(request));
            }

            // 设置操作状态和错误信息
            if (ex != null) {
                operLog.setStatus(0);
                operLog.setErrorMsg(truncateString(ex.getMessage(), 2000));
            } else {
                operLog.setStatus(1);
            }

            // 设置耗时
            Long startTime = startTimeThreadLocal.get();
            if (startTime != null) {
                operLog.setCostTime(System.currentTimeMillis() - startTime);
                startTimeThreadLocal.remove();
            }

            // 异步保存日志
            sysOperLogService.save(operLog);

        } catch (Exception e) {
            log.error("记录操作日志异常", e);
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap.isEmpty()) {
                return "";
            }
            return truncateString(parameterMap.toString(), 2000);
        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            return "";
        }
    }

    /**
     * 截断字符串
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 获取客户端真实IP
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级反向代理时，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(","));
        }
        return ip;
    }
}
