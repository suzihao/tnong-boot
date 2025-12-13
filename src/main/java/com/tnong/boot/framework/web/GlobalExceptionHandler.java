package com.tnong.boot.framework.web;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail("系统异常，请联系管理员");
    }
}
