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
                              `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '租户ID（物理主键）',
                              `tenant_code` bigint NOT NULL COMMENT '业务租户ID（雪花算法生成，对外使用，唯一标识）',
                              `name` varchar(128) NOT NULL COMMENT '租户名称',
                              `contact_name` varchar(64) DEFAULT NULL COMMENT '联系人姓名',
                              `contact_phone` varchar(32) DEFAULT NULL COMMENT '联系人电话',
                              `contact_email` varchar(128) DEFAULT NULL COMMENT '联系人邮箱',
                              `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1启用，0禁用',
                              `expire_time` datetime DEFAULT NULL COMMENT '到期时间，NULL表示无限期',
                              `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                              `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0未删除，1已删除',
                              `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `created_user` bigint DEFAULT NULL COMMENT '创建人ID',
                              `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `updated_user` bigint DEFAULT NULL COMMENT '更新人ID',
                              `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_tenant_code` (`tenant_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户表';

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
                            `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                            `user_code` bigint NOT NULL COMMENT '业务用户ID（雪花算法生成，对外使用，唯一标识）',
                            `tenant_id` bigint unsigned NOT NULL COMMENT '租户ID',
                            `dept_id` bigint unsigned DEFAULT NULL COMMENT '主部门ID',
                            `username` varchar(64) NOT NULL COMMENT '登录用户名',
                            `password` varchar(255) NOT NULL COMMENT '登录密码（BCrypt）',
                            `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
                            `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址',
                            `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
                            `mobile` varchar(32) DEFAULT NULL COMMENT '手机号',
                            `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1启用，0禁用',
                            `delete_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0未删除，1已删除',
                            `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
                            `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
                            `remark` varchar(255) DEFAULT NULL COMMENT '备注',
                            `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `created_user` bigint DEFAULT NULL COMMENT '创建人ID',
                            `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updated_user` bigint DEFAULT NULL COMMENT '更新人ID',
                            `version` int NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_tenant_username` (`tenant_id`,`username`),
                            UNIQUE KEY `uk_user_id` (`user_code`),
                            KEY `idx_user_tenant` (`tenant_id`),
                            KEY `idx_user_dept` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';


CREATE TABLE sys_user_third_account (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '本地用户ID，对应sys_user.id',
    type            VARCHAR(32)     NOT NULL COMMENT '账号类型: wecom / beisen / ...',
    third_user_id   VARCHAR(128)    NOT NULL COMMENT '第三方用户唯一标识，如企业微信userId',
    third_union_id  VARCHAR(128)    NULL COMMENT '第三方unionId（如有）',
    extra_info      JSON            NULL COMMENT '冗余一些第三方信息，如邮箱/工号/部门快照',
    created_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    created_user    BIGINT          NULL COMMENT '创建人',
    updated_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    updated_user    BIGINT          NULL COMMENT '更新人',
    version         INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    delete_flag     TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除：0正常，1删除',
    UNIQUE KEY uk_type_third_user (type, third_user_id),
    KEY idx_user_id (user_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT='用户第三方账号绑定表';

USE `tnong_boot`;

-- ====================================
-- 4. 角色表
-- ====================================
CREATE TABLE `sys_role` (
                            `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
                            `tenant_id`    BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
                            `code`         VARCHAR(64)     NOT NULL COMMENT '角色编码（如 ADMIN）',
                            `name`         VARCHAR(64)     NOT NULL COMMENT '角色名称',
                            `data_scope`   TINYINT         NOT NULL DEFAULT 0 COMMENT '数据范围：0全部，1本部门，2本部门及子部门，3仅本人',
                            `sort`         INT             NOT NULL DEFAULT 0 COMMENT '排序',
                            `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
                            `is_system`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否系统内置：1是，0否',
                            `remark`       VARCHAR(255)    NULL COMMENT '备注',
                            `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                            `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `created_user` BIGINT          NULL COMMENT '创建人ID',
                            `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updated_user` BIGINT          NULL COMMENT '更新人ID',
                            `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `code`),
                            KEY `idx_role_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ====================================
-- 5. 菜单/权限表
-- ====================================
CREATE TABLE `sys_menu` (
                            `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '菜单/权限ID',
                            `tenant_id`    BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
                            `parent_id`    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示根',
                            `type`         TINYINT         NOT NULL COMMENT '类型：1目录，2菜单，3按钮',
                            `name`         VARCHAR(64)     NOT NULL COMMENT '菜单名称',
                            `path`         VARCHAR(128)    NULL COMMENT '路由路径',
                            `component`    VARCHAR(128)    NULL COMMENT '前端组件路径',
                            `perms`        VARCHAR(128)    NULL COMMENT '权限标识（如 user:list）',
                            `icon`         VARCHAR(64)     NULL COMMENT '图标',
                            `sort`         INT             NOT NULL DEFAULT 0 COMMENT '排序',
                            `visible`      TINYINT         NOT NULL DEFAULT 1 COMMENT '是否可见：1可见，0隐藏',
                            `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
                            `remark`       VARCHAR(255)    NULL COMMENT '备注',
                            `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                            `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `created_user` BIGINT          NULL COMMENT '创建人ID',
                            `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updated_user` BIGINT          NULL COMMENT '更新人ID',
                            `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                            PRIMARY KEY (`id`),
                            KEY `idx_menu_tenant` (`tenant_id`),
                            KEY `idx_menu_parent` (`parent_id`),
                            UNIQUE KEY `uk_tenant_perms` (`tenant_id`, `perms`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单与权限表';

-- ====================================
-- 6. 用户-角色关联表
-- ====================================
CREATE TABLE `sys_user_role` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
                                 `user_id`      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                                 `role_id`      BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
                                 `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                                 `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `created_user` BIGINT          NULL COMMENT '创建人ID',
                                 `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_role` (`tenant_id`, `user_id`, `role_id`),
                                 KEY `idx_user_role_user` (`user_id`),
                                 KEY `idx_user_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ====================================
-- 7. 角色-菜单/权限关联表
-- ====================================
CREATE TABLE `sys_role_menu` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
                                 `role_id`      BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
                                 `menu_id`      BIGINT UNSIGNED NOT NULL COMMENT '菜单/权限ID',
                                 `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                                 `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `created_user` BIGINT          NULL COMMENT '创建人ID',
                                 `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_role_menu` (`tenant_id`, `role_id`, `menu_id`),
                                 KEY `idx_role_menu_role` (`role_id`),
                                 KEY `idx_role_menu_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单/权限关联表';

-- ====================================
-- 8. 用户-部门关联表（多部门）
-- ====================================
CREATE TABLE `sys_user_dept` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
                                 `user_id`      BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
                                 `dept_id`      BIGINT UNSIGNED NOT NULL COMMENT '部门ID',
                                 `main_flag`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否主部门：1是，0否',
                                 `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                                 `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `created_user` BIGINT          NULL COMMENT '创建人ID',
                                 `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_user_dept` (`tenant_id`, `user_id`, `dept_id`),
                                 KEY `idx_user_dept_user` (`user_id`),
                                 KEY `idx_user_dept_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-部门关联表';

-- ====================================
-- 9. 字典类型表
-- ====================================
CREATE TABLE `sys_dict_type` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，0表示平台通用',
                                 `type_code`    VARCHAR(64)     NOT NULL COMMENT '字典类型编码',
                                 `name`         VARCHAR(64)     NOT NULL COMMENT '字典类型名称',
                                 `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
                                 `remark`       VARCHAR(255)    NULL COMMENT '备注',
                                 `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                                 `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `created_user` BIGINT          NULL COMMENT '创建人ID',
                                 `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `updated_user` BIGINT          NULL COMMENT '更新人ID',
                                 `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_dict_type_tenant_code` (`tenant_id`, `type_code`),
                                 KEY `idx_dict_type_tenant_status` (`tenant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典类型表';

-- ====================================
-- 10. 字典项表
-- ====================================
CREATE TABLE `sys_dict_item` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，0表示平台通用',
                                 `type_code`    VARCHAR(64)     NOT NULL COMMENT '所属字典类型编码',
                                 `item_value`   VARCHAR(64)     NOT NULL COMMENT '字典项值',
                                 `item_label`   VARCHAR(128)    NOT NULL COMMENT '字典项显示名称',
                                 `css_class`    VARCHAR(64)     NULL COMMENT '样式扩展（前端自用）',
                                 `color`        VARCHAR(32)     NULL COMMENT '颜色扩展（前端自用）',
                                 `sort`         INT             NOT NULL DEFAULT 0 COMMENT '排序',
                                 `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
                                 `remark`       VARCHAR(255)    NULL COMMENT '备注',
                                 `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                                 `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `created_user` BIGINT          NULL COMMENT '创建人ID',
                                 `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 `updated_user` BIGINT          NULL COMMENT '更新人ID',
                                 `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_dict_item_value` (`tenant_id`, `type_code`, `item_value`),
                                 KEY `idx_dict_item_type` (`tenant_id`, `type_code`, `status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据字典项表';

-- ====================================
-- 11. 系统配置表
-- ====================================
CREATE TABLE `sys_config` (
                              `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                              `tenant_id`    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，0表示平台通用',
                              `config_key`   VARCHAR(128)    NOT NULL COMMENT '配置键',
                              `config_name`  VARCHAR(128)    NOT NULL COMMENT '配置名称',
                              `config_value` TEXT            NOT NULL COMMENT '配置值',
                              `config_type`  TINYINT         NOT NULL DEFAULT 0 COMMENT '配置类型：0系统参数，1业务参数',
                              `is_system`    TINYINT         NOT NULL DEFAULT 0 COMMENT '是否系统内置：1是，0否',
                              `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
                              `remark`       VARCHAR(255)    NULL COMMENT '备注',
                              `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                              `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              `created_user` BIGINT          NULL COMMENT '创建人ID',
                              `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              `updated_user` BIGINT          NULL COMMENT '更新人ID',
                              `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                              PRIMARY KEY (`id`),
                              UNIQUE KEY `uk_config_tenant_key` (`tenant_id`, `config_key`),
                              KEY `idx_config_tenant_status` (`tenant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ====================================
-- 12. 登录日志表
-- ====================================
CREATE TABLE `sys_login_log` (
                                 `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `tenant_id`    BIGINT UNSIGNED NULL COMMENT '租户ID',
                                 `user_id`      BIGINT UNSIGNED NULL COMMENT '用户ID',
                                 `username`     VARCHAR(64)     NULL COMMENT '登录账号',
                                 `login_type`   TINYINT         NOT NULL DEFAULT 0 COMMENT '登录方式：0账号密码，1短信验证码，2三方登录等',
                                 `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '登录状态：1成功，0失败',
                                 `ip_address`   VARCHAR(64)     NULL COMMENT '登录IP',
                                 `user_agent`   VARCHAR(255)    NULL COMMENT 'User-Agent',
                                 `os`           VARCHAR(128)    NULL COMMENT '操作系统',
                                 `browser`      VARCHAR(128)    NULL COMMENT '浏览器',
                                 `msg`          VARCHAR(255)    NULL COMMENT '提示消息',
                                 `login_time`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
                                 PRIMARY KEY (`id`),
                                 KEY `idx_login_tenant_user` (`tenant_id`, `user_id`),
                                 KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ====================================
-- 13. 操作日志表
-- ====================================
CREATE TABLE `sys_oper_log` (
                                `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                `tenant_id`      BIGINT UNSIGNED NULL COMMENT '租户ID',
                                `user_id`        BIGINT UNSIGNED NULL COMMENT '操作用户ID',
                                `username`       VARCHAR(64)     NULL COMMENT '操作用户名',
                                `module`         VARCHAR(128)    NULL COMMENT '模块标题',
                                `business_type`  TINYINT         NOT NULL DEFAULT 0 COMMENT '业务类型：0其它，1新增，2修改，3删除，4导入，5导出等',
                                `request_method` VARCHAR(16)     NULL COMMENT '请求方式：GET、POST等',
                                `request_url`    VARCHAR(255)    NULL COMMENT '请求URL',
                                `request_ip`     VARCHAR(64)     NULL COMMENT '请求IP',
                                `user_agent`     VARCHAR(255)    NULL COMMENT 'User-Agent',
                                `method`         VARCHAR(255)    NULL COMMENT '调用方法名',
                                `request_params` TEXT            NULL COMMENT '请求参数',
                                `response_data`  TEXT            NULL COMMENT '响应结果（可截断）',
                                `status`         TINYINT         NOT NULL DEFAULT 1 COMMENT '操作状态：1成功，0失败',
                                `error_msg`      VARCHAR(2000)   NULL COMMENT '错误消息',
                                `cost_time`      BIGINT          NULL COMMENT '耗时毫秒',
                                `oper_time`      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                PRIMARY KEY (`id`),
                                KEY `idx_oper_tenant_user` (`tenant_id`, `user_id`),
                                KEY `idx_oper_time` (`oper_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ====================================
-- 14. 文件信息表
-- ====================================
CREATE TABLE `sys_file` (
                            `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `tenant_id`    BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，0表示平台通用',
                            `file_name`    VARCHAR(255)    NOT NULL COMMENT '原始文件名',
                            `file_suffix`  VARCHAR(32)     NULL COMMENT '文件后缀',
                            `content_type` VARCHAR(128)    NULL COMMENT '文件Content-Type',
                            `file_size`    BIGINT          NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
                            `storage_type` TINYINT         NOT NULL DEFAULT 0 COMMENT '存储方式：0本地，1对象存储，2自定义等',
                            `bucket`       VARCHAR(128)    NULL COMMENT '存储桶/目录',
                            `object_key`   VARCHAR(255)    NOT NULL COMMENT '存储对象键',
                            `url`          VARCHAR(512)    NULL COMMENT '文件访问URL',
                            `biz_type`     VARCHAR(64)     NULL COMMENT '业务类型标识',
                            `biz_id`       BIGINT          NULL COMMENT '业务ID',
                            `md5`          VARCHAR(64)     NULL COMMENT '文件MD5，用于秒传/去重',
                            `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1正常，0禁用',
                            `remark`       VARCHAR(255)    NULL COMMENT '备注',
                            `delete_flag`  TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                            `created_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `created_user` BIGINT          NULL COMMENT '创建人ID',
                            `updated_time` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `updated_user` BIGINT          NULL COMMENT '更新人ID',
                            `version`      INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                            PRIMARY KEY (`id`),
                            KEY `idx_file_tenant_biz` (`tenant_id`, `biz_type`, `biz_id`),
                            KEY `idx_file_md5` (`md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- ====================================
-- 15. 定时任务配置表
-- ====================================
CREATE TABLE `sys_job` (
                           `id`              BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                           `tenant_id`       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID，0表示平台级任务',
                           `job_name`        VARCHAR(64)     NOT NULL COMMENT '任务名称',
                           `job_group`       VARCHAR(64)     NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
                           `invoke_target`   VARCHAR(255)    NOT NULL COMMENT '调用目标字符串（如 beanName.method 或 HTTP URL）',
                           `cron_expression` VARCHAR(64)     NOT NULL COMMENT 'cron表达式',
                           `misfire_policy`  TINYINT         NOT NULL DEFAULT 0 COMMENT '错失执行策略：0默认，1立即执行，2执行一次，3放弃执行',
                           `concurrent`      TINYINT         NOT NULL DEFAULT 0 COMMENT '是否并发执行：1允许，0禁止',
                           `status`          TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0暂停',
                           `next_fire_time`  DATETIME        NULL COMMENT '下次执行时间',
                           `last_fire_time`  DATETIME        NULL COMMENT '上次执行时间',
                           `remark`          VARCHAR(255)    NULL COMMENT '备注',
                           `delete_flag`     TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
                           `created_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           `created_user`    BIGINT          NULL COMMENT '创建人ID',
                           `updated_time`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           `updated_user`    BIGINT          NULL COMMENT '更新人ID',
                           `version`         INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
                           PRIMARY KEY (`id`),
                           UNIQUE KEY `uk_job_tenant_name_group` (`tenant_id`, `job_name`, `job_group`),
                           KEY `idx_job_status` (`tenant_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务配置表';

-- ====================================
-- 16. 定时任务执行日志表
-- ====================================
CREATE TABLE `sys_job_log` (
                               `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `tenant_id`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '租户ID',
                               `job_id`         BIGINT UNSIGNED NULL COMMENT '任务ID',
                               `job_name`       VARCHAR(64)     NULL COMMENT '任务名称',
                               `job_group`      VARCHAR(64)     NULL COMMENT '任务分组',
                               `invoke_target`  VARCHAR(255)    NULL COMMENT '调用目标字符串',
                               `status`         TINYINT         NOT NULL DEFAULT 1 COMMENT '执行状态：1成功，0失败',
                               `error_msg`      VARCHAR(2000)   NULL COMMENT '错误信息',
                               `start_time`     DATETIME        NOT NULL COMMENT '开始时间',
                               `end_time`       DATETIME        NOT NULL COMMENT '结束时间',
                               `cost_time`      BIGINT          NULL COMMENT '耗时毫秒',
                               PRIMARY KEY (`id`),
                               KEY `idx_job_log_job` (`tenant_id`, `job_id`),
                               KEY `idx_job_log_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行日志表';

-- ====================================
-- 初始化测试数据
-- ====================================

-- 插入默认租户
INSERT INTO `sys_tenant` (`id`, `tenant_id`, `name`, `status`, `created_user`, `updated_user`) 
VALUES (1, 1000000000, '默认租户', 1, 1, 1);

-- 插入默认部门
INSERT INTO `sys_dept` (`id`, `tenant_id`, `parent_id`, `name`, `code`, `sort`, `status`, `created_user`, `updated_user`) 
VALUES (1, 1, 0, '总公司', 'ROOT', 0, 1, 1, 1);

-- 插入测试用户（密码：123456）
-- 注意：这是临时密码，首次登录后需要通过 /api/auth/encode-password?password=123456 获取加密后的密码并更新
INSERT INTO `sys_user` (`id`, `tenant_id`, `dept_id`, `username`, `password`, `nickname`, `status`, `created_user`, `updated_user`) 
VALUES (1, 1, 1, 'admin', 'TEMP_PASSWORD', '管理员', 1, 1, 1);
