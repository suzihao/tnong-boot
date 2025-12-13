-- ====================================
-- Tnong Boot 数据库初始化脚本
-- ====================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `tnong_boot` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `tnong_boot`;

-- ====================================
-- 1. 租户表
-- ====================================
CREATE TABLE `sys_tenant` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '租户ID',
    `tenant_code`   VARCHAR(64)     NOT NULL COMMENT '租户编码，唯一',
    `name`          VARCHAR(128)    NOT NULL COMMENT '租户名称',
    `contact_name`  VARCHAR(64)     NULL COMMENT '联系人姓名',
    `contact_phone` VARCHAR(32)     NULL COMMENT '联系人电话',
    `contact_email` VARCHAR(128)    NULL COMMENT '联系人邮箱',
    `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `expire_time`   DATETIME        NULL COMMENT '到期时间，NULL表示无限期',
    `remark`        VARCHAR(255)    NULL COMMENT '备注',
    `delete_flag`   TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_user`  BIGINT          NULL COMMENT '创建人ID',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_user`  BIGINT          NULL COMMENT '更新人ID',
    `version`       INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- ====================================
-- 2. 部门表
-- ====================================
CREATE TABLE `sys_dept` (
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `tenant_id`      BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
    `parent_id`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示根部门',
    `name`           VARCHAR(64)     NOT NULL COMMENT '部门名称',
    `code`           VARCHAR(64)     NULL COMMENT '部门编码，租户内唯一',
    `sort`           INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `status`         TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `leader_user_id` BIGINT UNSIGNED NULL COMMENT '部门负责人用户ID',
    `phone`          VARCHAR(32)     NULL COMMENT '联系电话',
    `email`          VARCHAR(128)    NULL COMMENT '邮箱',
    `remark`         VARCHAR(255)    NULL COMMENT '备注',
    `delete_flag`    TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    `created_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_user`   BIGINT          NULL COMMENT '创建人ID',
    `updated_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_user`   BIGINT          NULL COMMENT '更新人ID',
    `version`        INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_tenant_code` (`tenant_id`, `code`),
    KEY `idx_dept_tenant` (`tenant_id`),
    KEY `idx_dept_parent` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ====================================
-- 3. 用户表
-- ====================================
CREATE TABLE `sys_user` (
    `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `tenant_id`       BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
    `dept_id`         BIGINT UNSIGNED NULL COMMENT '主部门ID',
    `username`        VARCHAR(64)     NOT NULL COMMENT '登录用户名',
    `password`        VARCHAR(255)    NOT NULL COMMENT '登录密码（BCrypt）',
    `nickname`        VARCHAR(64)     NULL COMMENT '昵称',
    `avatar`          VARCHAR(255)    NULL COMMENT '头像地址',
    `email`           VARCHAR(128)    NULL COMMENT '邮箱',
    `mobile`          VARCHAR(32)     NULL COMMENT '手机号',
    `status`          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `delete_flag`     TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    `last_login_ip`   VARCHAR(64)     NULL COMMENT '最后登录IP',
    `last_login_time` DATETIME        NULL COMMENT '最后登录时间',
    `remark`          VARCHAR(255)    NULL COMMENT '备注',
    `created_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_user`    BIGINT          NULL COMMENT '创建人ID',
    `updated_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_user`    BIGINT          NULL COMMENT '更新人ID',
    `version`         INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`),
    KEY `idx_user_tenant` (`tenant_id`),
    KEY `idx_user_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ====================================
-- 初始化测试数据
-- ====================================

-- 插入默认租户
INSERT INTO `sys_tenant` (`id`, `tenant_code`, `name`, `status`, `created_user`, `updated_user`) 
VALUES (1, 'DEFAULT', '默认租户', 1, 1, 1);

-- 插入默认部门
INSERT INTO `sys_dept` (`id`, `tenant_id`, `parent_id`, `name`, `code`, `sort`, `status`, `created_user`, `updated_user`) 
VALUES (1, 1, 0, '总公司', 'ROOT', 0, 1, 1, 1);

-- 插入测试用户（密码：123456）
-- 注意：这是临时密码，首次登录后需要通过 /api/auth/encode-password?password=123456 获取加密后的密码并更新
INSERT INTO `sys_user` (`id`, `tenant_id`, `dept_id`, `username`, `password`, `nickname`, `status`, `created_user`, `updated_user`) 
VALUES (1, 1, 1, 'admin', 'TEMP_PASSWORD', '管理员', 1, 1, 1);
