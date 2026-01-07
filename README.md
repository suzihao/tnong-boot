# Tnong Boot - 天农企业级脚手架项目

## 项目介绍

基于 Spring Boot + MyBatis 的企业级多租户 SaaS 脚手架，支持 RBAC 权限模型、乐观锁并发控制、软删除等企业级特性。

## 设计思路

- 基于内部沟通的桥梁**企业微信**为基础，围绕**企业微信账号**搭建单点登录的快速开发脚手架。以最低维护成本的方式为业务赋能。
- 基础版减少中间件的使用，通过后端-前端-数据库几个部分快速构建企业级应用。通过单点登录优化不同系统间的用户体验，随着业务发展渐进式拆分微服务或者分布式系统。
- 通过租户、RBAC、单体定时任务、文件上传服务支撑业务的快速开发。
- 如果存在纯后端处理的IO密集型或者计算密集型业务，单独使用微服务开发。核心业务逻辑通过系统对外提供服务。
- 为什么重新开发脚手架？ 因为大部分开源脚手架都过于复杂，集成了大量中间件，导致维护成本过高，技术版本也落后。使用最新的技术栈，充分利用版本升级带来的性能提升。
- 项目适配一个开源前端后台，也可以自己集成其他的后台界面。

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

com.tnong.boot.TnongBootApplication 启动类
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
- [x] 部门管理（sys_dept）
- [x] 角色管理（sys_role）
- [x] 菜单管理（sys_menu）
- [x] 字典管理（sys_dict_type / sys_dict_item）
- [x] 系统配置（sys_config）
- [x] 操作日志（sys_oper_log）
- [x] 登录日志（sys_login_log）
- [x] 文件管理（sys_file）
- [x] 定时任务（sys_job）

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
```sql
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

