package com.tnong.boot.common.exception;

import lombok.Getter;

/**
 * 权限不足异常
 */
@Getter
public class PermissionDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public PermissionDeniedException(String message) {
        super(message);
        this.code = 403; // HTTP 403 Forbidden
    }

    public PermissionDeniedException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public PermissionDeniedException(String message, Throwable cause) {
        super(message, cause);
        this.code = 403;
    }
}
