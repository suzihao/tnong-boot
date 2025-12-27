-- ====================================
-- 知识库模块表结构
-- ====================================

-- 知识库目录表
CREATE TABLE `kb_category` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '目录ID',
    `tenant_id`     BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
    `parent_id`     BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父目录ID，0表示根目录',
    `name`          VARCHAR(128)    NOT NULL COMMENT '目录名称',
    `icon`          VARCHAR(64)     NULL COMMENT '图标',
    `sort`          INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `description`   VARCHAR(500)    NULL COMMENT '描述',
    `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用，0禁用',
    `delete_flag`   TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_user`  BIGINT          NULL COMMENT '创建人ID',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_user`  BIGINT          NULL COMMENT '更新人ID',
    `version`       INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库目录表';

-- 知识文档表
CREATE TABLE `kb_document` (
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文档ID',
    `tenant_id`     BIGINT UNSIGNED NOT NULL COMMENT '租户ID',
    `category_id`   BIGINT UNSIGNED NOT NULL COMMENT '目录ID',
    `title`         VARCHAR(255)    NOT NULL COMMENT '文档标题',
    `content`       LONGTEXT        NULL COMMENT 'Markdown内容',
    `tags`          VARCHAR(500)    NULL COMMENT '标签，逗号分隔',
    `sort`          INT             NOT NULL DEFAULT 0 COMMENT '排序',
    `view_count`    INT             NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `status`        TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1已发布，0草稿',
    `delete_flag`   TINYINT         NOT NULL DEFAULT 0 COMMENT '删除标记：0未删除，1已删除',
    `created_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_user`  BIGINT          NULL COMMENT '创建人ID',
    `updated_time`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_user`  BIGINT          NULL COMMENT '更新人ID',
    `version`       INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识文档表';

-- 插入测试数据
INSERT INTO `kb_category` (`id`, `tenant_id`, `parent_id`, `name`, `sort`, `status`, `created_user`, `updated_user`) 
VALUES 
(1, 1, 0, '技术文档', 1, 1, 1, 1),
(2, 1, 0, '产品手册', 2, 1, 1, 1),
(3, 1, 1, 'Java开发', 1, 1, 1, 1),
(4, 1, 1, '前端开发', 2, 1, 1, 1);

INSERT INTO `kb_document` (`id`, `tenant_id`, `category_id`, `title`, `content`, `sort`, `status`, `created_user`, `updated_user`) 
VALUES 
(1, 1, 3, 'Spring Boot快速入门', '# Spring Boot快速入门\n\n这是一个示例文档。', 1, 1, 1, 1),
(2, 1, 4, 'Vue3组件开发指南', '# Vue3组件开发指南\n\n学习如何开发Vue3组件。', 1, 1, 1, 1);
