
package com.tnong.boot.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 *
 * 使用示例：
 * 1. 单个权限：@RequirePermission("system:user:add")
 * 2. 多个权限AND：@RequirePermission(value = {"system:user:add", "system:user:edit"}, logical = Logical.AND)
 * 3. 多个权限OR：@RequirePermission(value = {"system:user:add", "system:user:edit"}, logical = Logical.OR)
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限标识
     * 支持单个或多个权限
     */
    String[] value();

    /**
     * 多个权限的逻辑关系
     * AND: 需要拥有所有权限
     * OR: 只需拥有其中一个权限
     */
    Logical logical() default Logical.AND;

    /**
     * 逻辑枚举
     */
    enum Logical {
        /**
         * 且（需要拥有所有权限）
         */
        AND,

        /**
         * 或（只需拥有其中一个权限）
         */
        OR
    }
}
