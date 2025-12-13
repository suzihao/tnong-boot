# Tnong Boot 开发进度

## 已完成模块 ✅

### 1. sys_user - 用户管理 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 支持多租户、软删除、乐观锁

### 2. sys_tenant - 租户管理 ✅  
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 支持软删除、乐观锁

### 3. sys_dict (字典管理) ✅
- [x] SysDictType Entity / DTO
- [x] SysDictItem Entity / DTO
- [x] Mapper + XML (两张表)
- [x] 支持多租户、软删除、乐观锁

### 4. sys_role - 角色管理 ✅
- [x] Entity
- [x] Mapper + XML
- [x] 支持数据权限范围

### 5. sys_dept - 部门管理 ✅
- [x] Entity
- [x] Mapper + XML
- [x] 树形结构查询支持

### 6. sys_menu - 菜单管理 ✅
- [x] Entity
- [x] Mapper + XML
- [x] 树形结构 + 权限关联

### 7. sys_config - 系统配置 ✅
- [x] Entity
- [x] Mapper + XML

## 待开发模块（按优先级）

### 3. sys_dept - 部门管理 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML（树形结构查询）
- [ ] Service + ServiceImpl
- [ ] Controller
- [ ] 树形结构支持

### 4. sys_role - 角色管理 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller
- [ ] 数据权限范围

### 5. sys_menu - 菜单管理 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML（树形结构）
- [ ] Service + ServiceImpl
- [ ] Controller
- [ ] 树形菜单 + 权限标识

### 6. sys_user_role - 用户角色关联 🔧
- [ ] Entity / DTO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] 批量分配/取消

### 7. sys_role_menu - 角色菜单关联 🔧
- [ ] Entity / DTO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] 批量分配权限

### 8. sys_dict_type - 字典类型 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller

### 9. sys_dict_item - 字典项 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller

### 10. sys_config - 系统配置 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller

### 11. sys_login_log - 登录日志 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller（只读）

### 12. sys_oper_log - 操作日志 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller（只读）
- [ ] AOP 切面自动记录

### 13. sys_file - 文件管理 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller
- [ ] 文件上传/下载

### 14. sys_job - 定时任务 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller

### 15. sys_job_log - 任务日志 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller（只读）

## 数据库脚本状态

- [x] sys_tenant 表结构
- [x] sys_dept 表结构
- [x] sys_user 表结构
- [x] sys_role 表结构（完整）
- [x] sys_menu 表结构（完整）
- [x] sys_user_role 表结构（完整）
- [x] sys_role_menu 表结构（完整）
- [x] sys_user_dept 表结构（完整）
- [x] sys_dict_type 表结构（完整）
- [x] sys_dict_item 表结构（完整）
- [x] sys_config 表结构（完整）
- [x] sys_login_log 表结构（完整）
- [x] sys_oper_log 表结构（完整）
- [x] sys_file 表结构（完整）
- [x] sys_job 表结构（完整）
- [x] sys_job_log 表结构（完整）

## 接口地址

### 租户管理
- GET /api/system/tenant/page - 分页查询
- GET /api/system/tenant/{id} - 详情
- POST /api/system/tenant - 新增
- PUT /api/system/tenant - 更新
- DELETE /api/system/tenant/{id} - 删除

### 用户管理
- GET /api/system/user/page - 分页查询
- GET /api/system/user/{id} - 详情
- POST /api/system/user - 新增
- PUT /api/system/user - 更新
- DELETE /api/system/user/{id} - 删除

## 下一步计划

1. 完成核心 RBAC 模块（部门、角色、菜单）
2. 开发字典管理
3. 开发系统配置
4. 开发日志模块（含 AOP 切面）
5. 开发文件管理
6. 开发定时任务
7. 实现多租户拦截器
8. 实现公共字段自动填充
9. 完善认证授权
10. 代码生成器
