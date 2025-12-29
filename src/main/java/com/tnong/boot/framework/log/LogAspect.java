package com.tnong.boot.framework.log;

import com.tnong.boot.common.annotation.Log;
import com.tnong.boot.framework.security.LoginUser;
import com.tnong.boot.framework.security.UserContext;
import com.tnong.boot.system.log.domain.entity.SysOperLog;
import com.tnong.boot.system.log.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tools.jackson.databind.ObjectMapper;

/**
 * 操作日志 AOP 切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final SysOperLogService sysOperLogService;
    private final ObjectMapper objectMapper;

    /**
     * 线程本地变量，存储开始时间
     */
    private final ThreadLocal<Long> startTimeThreadLocal = new ThreadLocal<>();

    /**
     * 前置通知，记录开始时间
     */
    @Before("@annotation(logAnnotation)")
    public void doBefore(JoinPoint joinPoint, Log logAnnotation) {
        startTimeThreadLocal.set(System.currentTimeMillis());
    }

    /**
     * 正常返回后通知
     */
    @AfterReturning(pointcut = "@annotation(logAnnotation)", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Log logAnnotation, Object result) {
        handleLog(joinPoint, logAnnotation, null, result);
    }

    /**
     * 异常后通知
     */
    @AfterThrowing(pointcut = "@annotation(logAnnotation)", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Log logAnnotation, Exception e) {
        handleLog(joinPoint, logAnnotation, e, null);
    }

    /**
     * 处理日志
     */
    private void handleLog(JoinPoint joinPoint, Log logAnnotation, Exception e, Object result) {
        try {
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

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
            } catch (Exception ex) {
                log.warn("获取登录用户信息失败", ex);
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
            String className = joinPoint.getTarget().getClass().getName();
            String methodName = joinPoint.getSignature().getName();
            operLog.setMethod(className + "." + methodName);

            // 设置请求参数
            if (logAnnotation.saveRequestData()) {
                operLog.setRequestParams(getRequestParams(joinPoint));
            }

            // 设置响应结果
            if (logAnnotation.saveResponseData() && result != null) {
                operLog.setResponseData(truncateString(toJsonString(result), 2000));
            }

            // 设置操作状态和错误信息
            if (e != null) {
                operLog.setStatus(0);
                operLog.setErrorMsg(truncateString(e.getMessage(), 2000));
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

        } catch (Exception ex) {
            log.error("记录操作日志异常", ex);
        }
    }

    /**
     * 获取请求参数
     */
    private String getRequestParams(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return "";
            }
            return truncateString(toJsonString(args), 2000);
        } catch (Exception e) {
            log.warn("获取请求参数失败", e);
            return "";
        }
    }

    /**
     * 对象转 JSON 字符串
     */
    private String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
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
