# Tnong Boot - 企业级脚手架项目

## 项目介绍

基于 Spring Boot + MyBatis 的企业级多租户 SaaS 脚手架，支持 RBAC 权限模型、乐观锁并发控制、软删除等企业级特性。

## 技术栈

- **JDK**: 25
- **Spring Boot**: 4.0.0
- **MyBatis**: 4.0.0
- **MySQL**: 8.x
- **其他**: Lombok

## 项目结构

```
com.tnong.boot
├── common                    # 通用工具 & 基础模型
│   ├── constant              # 通用常量
│   ├── enums                 # 通用枚举
│   ├── exception             # 全局/业务异常
│   ├── util                  # 工具类
│   └── web                   # 统一返回体、分页对象
│
├── framework                 # 框架层
│   ├── mybatis               # MyBatis 配置与拦截器
│   ├── tenant                # 多租户上下文
│   ├── security              # 安全相关
│   ├── log                   # AOP 操作日志
│   ├── job                   # 定时任务配置
│   └── web                   # 全局异常处理、拦截器
│
├── system                    # 系统管理领域
│   ├── user                  # 用户、部门、租户、角色、菜单
│   ├── dict                  # 字典
│   ├── config                # 系统配置
│   ├── log                   # 日志
│   ├── file                  # 文件
│   └── job                   # 定时任务
│
└── biz                       # 业务模块（后续扩展）
```

## 核心特性

### 1. 多租户隔离
- 所有业务表带 `tenant_id` 字段
- MyBatis 拦截器自动添加租户条件
- 租户上下文管理

### 2. 乐观锁并发控制
- 所有业务表带 `version` 字段
- 更新/删除时自动校验版本号
- 并发冲突自动抛异常

### 3. 软删除
- 所有业务表带 `delete_flag` 字段
- 逻辑删除，数据可恢复
- 查询自动过滤已删除数据

### 4. 统一审计
- `created_time` / `created_user`：创建时间/创建人
- `updated_time` / `updated_user`：更新时间/更新人

## 快速开始

### 1. 环境要求

- JDK 25
- MySQL 8.x
- Maven 3.6+

### 2. 数据库初始化

执行初始化脚本：
```bash
mysql -u root -p < docs/sql/init.sql
```

数据库名：`tnong_boot`
默认用户：`admin` / `123456`

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tnong_boot?...
    username: root
    password: 你的密码
```

### 4. 启动项目

```bash
./mvnw spring-boot:run
```

或使用 IDE 直接运行 `TnongBootApplication` 主类。

### 5. 测试接口

项目启动后，访问：http://localhost:8080

#### 用户管理接口

- 分页查询：`GET /api/system/user/page?current=1&size=10`
- 查询详情：`GET /api/system/user/{id}`
- 新增用户：`POST /api/system/user`
- 更新用户：`PUT /api/system/user`
- 删除用户：`DELETE /api/system/user/{id}?version=0`

## 已完成模块

### ✅ sys_user 用户模块

完整的 CRUD 示例，包含：
- Entity / DTO / VO 分层
- Mapper 接口 + XML（多租户、软删、乐观锁）
- Service 层业务逻辑
- Controller REST 接口
- 全局异常处理

## 待完成模块

- [x] 租户管理（sys_tenant）
- [ ] 部门管理（sys_dept）
- [x] 角色管理（sys_role）
- [x] 菜单管理（sys_menu）
- [ ] 字典管理（sys_dict_type / sys_dict_item）
- [ ] 系统配置（sys_config）
- [ ] 操作日志（sys_oper_log）
- [ ] 登录日志（sys_login_log）
- [ ] 文件管理（sys_file）
- [ ] 定时任务（sys_job）

## 开发规范

### 1. 命名约定

- 实体类：`SysUser`
- Mapper 接口：`SysUserMapper`
- Service 接口：`SysUserService`
- Service 实现：`SysUserServiceImpl`
- Controller：`SysUserController`
- XML 文件：`SysUserMapper.xml`

### 2. 包结构约定

每个模块按照以下结构组织：
```
system/user/
├── controller
├── service
│   └── impl
├── mapper
└── domain
    ├── entity
    ├── dto
    └── vo
```

### 3. XML SQL 规范

- 所有查询必须带 `tenant_id` 和 `delete_flag` 条件
- 更新/删除必须校验 `version` 字段
- 使用 `<sql>` 标签复用 SQL 片段

### 4. 乐观锁使用

更新示例：
```xml
UPDATE sys_user
SET nickname = #{nickname},
    version = version + 1,
    updated_time = NOW()
WHERE id = #{id}
  AND tenant_id = #{tenantId}
  AND version = #{version}
  AND delete_flag = 0
```

返回 0 行时抛出 `OptimisticLockException`。

## 注意事项

1. **Spring Security 已暂时放开**：所有接口无需认证，生产环境需配置。
2. **租户ID 暂时硬编码**：Controller 中 `tenantId` 写死为 1，后续需从上下文获取。
3. **密码加密待实现**：用户保存/更新时需使用 BCrypt 加密密码。
4. **多租户拦截器待开发**：当前在 XML 中手动添加租户条件，后续可用拦截器自动处理。

## 后续计划

1. 实现其他系统管理模块
2. 开发多租户 MyBatis 拦截器
3. 完善 Spring Security 认证授权
4. 添加密码加密工具类
5. 实现公共字段自动填充
6. 开发代码生成器

## 联系方式

如有问题，请提交 Issue。
