package com.tnong.boot.common.exception;

/**
 * 乐观锁异常（并发冲突）
 */
public class OptimisticLockException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OptimisticLockException() {
        super("数据已被其他人修改，请刷新后重试");
    }

    public OptimisticLockException(String message) {
        super(message);
    }
}
