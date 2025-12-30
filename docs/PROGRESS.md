
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

### 3. sys_role - 角色管理 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 支持数据权限范围
- [x] sys_role_menu 角色菜单关联

### 4. sys_menu - 菜单管理 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 树形结构 + 权限关联

### 5. sys_dept - 部门管理 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 树形结构查询支持

### 6. sys_oper_log - 操作日志 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller（只读）

### 7. sys_dict - 字典管理 ✅
- [x] SysDictType Entity / DTO / VO
- [x] SysDictItem Entity / DTO / VO
- [x] Mapper + XML (两张表)
- [x] Service + ServiceImpl
- [x] Controller

### 8. sys_config - 系统配置 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller

### 9. sys_file - 文件管理 ✅
- [x] Entity / DTO / VO
- [x] Mapper + XML
- [x] Service + ServiceImpl
- [x] Controller
- [x] 文件存储策略（本地 + MinIO）
- [x] 文件秒传（MD5去重）

## 待开发模块（按优先级）

### 10. sys_user_role - 用户角色关联 🔧
- [x] Entity
- [x] Mapper
- [ ] Service + ServiceImpl
- [ ] 批量分配/取消

### 11. sys_user_dept - 用户部门关联 🔧
- [x] Entity
- [x] Mapper
- [ ] Service + ServiceImpl
- [ ] 批量分配/取消

### 12. sys_login_log - 登录日志 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller（只读）

### 13. sys_job - 定时任务 🔧
- [ ] Entity / DTO / VO
- [ ] Mapper + XML
- [ ] Service + ServiceImpl
- [ ] Controller

### 14. sys_job_log - 任务日志 🔧
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

### 角色管理
- GET /api/system/role/page - 分页查询
- GET /api/system/role/{id} - 详情
- POST /api/system/role - 新增
- PUT /api/system/role - 更新
- DELETE /api/system/role/{id} - 删除
- POST /api/system/role/assign-menus - 分配菜单权限

### 菜单管理
- GET /api/system/menu/tree - 树形列表
- GET /api/system/menu/{id} - 详情
- POST /api/system/menu - 新增
- PUT /api/system/menu - 更新
- DELETE /api/system/menu/{id} - 删除
- GET /api/system/menu/my - 获取当前用户菜单和权限

### 部门管理
- GET /api/system/dept/tree - 树形列表
- GET /api/system/dept/{id} - 详情
- POST /api/system/dept - 新增
- PUT /api/system/dept - 更新
- DELETE /api/system/dept/{id} - 删除

### 操作日志
- GET /api/system/oper-log/page - 分页查询

### 字典管理
- GET /api/system/dict/type/page - 字典类型分页查询
- GET /api/system/dict/type/{id} - 字典类型详情
- POST /api/system/dict/type - 新增字典类型
- PUT /api/system/dict/type - 更新字典类型
- DELETE /api/system/dict/type/{id} - 删除字典类型
- GET /api/system/dict/item/page - 字典项分页查询
- GET /api/system/dict/item/{id} - 字典项详情
- GET /api/system/dict/item/list/{typeCode} - 根据类型查询字典项列表
- POST /api/system/dict/item - 新增字典项
- PUT /api/system/dict/item - 更新字典项
- DELETE /api/system/dict/item/{id} - 删除字典项

### 系统配置
- GET /api/system/config/page - 分页查询
- GET /api/system/config/{id} - 详情
- GET /api/system/config/key/{configKey} - 根据配置键获取值
- POST /api/system/config - 新增
- PUT /api/system/config - 更新
- DELETE /api/system/config/{id} - 删除

### 文件管理
- GET /api/system/file/page - 分页查询
- GET /api/system/file/{id} - 详情
- POST /api/system/file/upload - 上传文件
- GET /api/system/file/download/{id} - 下载文件
- DELETE /api/system/file/{id} - 删除文件

## 下一步计划

1. ✅ 完成核心 RBAC 模块（部门、角色、菜单）
2. ✅ 开发字典管理 Service + Controller
3. ✅ 开发系统配置 Service + Controller
4. ✅ 开发文件管理（支持本地存储和MinIO）
5. 开发用户角色关联、用户部门关联
6. 开发登录日志（含 AOP 切面）
7. 开发定时任务
8. 实现多租户拦截器
9. 实现公共字段自动填充
10. 完善认证授权
11. 代码生成器
