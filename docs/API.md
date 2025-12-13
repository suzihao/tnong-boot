# Tnong Boot API 接口文档

基础地址：`http://localhost:8080`

## 通用说明

### 统一返回格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1702345678901
}
```

### 分页返回格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "records": [],
    "current": 1,
    "size": 10
  },
  "timestamp": 1702345678901
}
```

---

## 1. 租户管理 (/api/system/tenant)

### 1.1 分页查询租户列表

**接口**: `GET /api/system/tenant/page`

**参数**:
- `tenantCode`: 租户编码（可选）
- `name`: 租户名称（可选，模糊查询）
- `status`: 状态（可选，1启用/0禁用）
- `current`: 当前页，默认1
- `size`: 每页大小，默认10

**响应示例**:
```json
{
  "code": 200,
  "data": {
    "total": 1,
    "records": [{
      "id": 1,
      "tenantCode": "DEFAULT",
      "name": "默认租户",
      "status": 1,
      "createdTime": "2025-12-13T10:00:00",
      "version": 0
    }],
    "current": 1,
    "size": 10
  }
}
```

### 1.2 查询租户详情

**接口**: `GET /api/system/tenant/{id}`

### 1.3 新增租户

**接口**: `POST /api/system/tenant`

**请求体**:
```json
{
  "tenantCode": "COMPANY_A",
  "name": "A公司",
  "contactName": "张三",
  "contactPhone": "13800138000",
  "status": 1
}
```

### 1.4 更新租户

**接口**: `PUT /api/system/tenant`

**请求体**:
```json
{
  "id": 1,
  "name": "A公司（已修改）",
  "status": 1,
  "version": 0
}
```

### 1.5 删除租户

**接口**: `DELETE /api/system/tenant/{id}?version=0`

---

## 2. 用户管理 (/api/system/user)

### 2.1 分页查询用户列表

**接口**: `GET /api/system/user/page`

**参数**:
- `username`: 用户名（可选，模糊查询）
- `nickname`: 昵称（可选，模糊查询）
- `mobile`: 手机号（可选）
- `deptId`: 部门ID（可选）
- `status`: 状态（可选）
- `current`: 当前页，默认1
- `size`: 每页大小，默认10

### 2.2 查询用户详情

**接口**: `GET /api/system/user/{id}`

### 2.3 新增用户

**接口**: `POST /api/system/user`

**请求体**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "deptId": 1,
  "mobile": "13800138000",
  "email": "zhangsan@example.com",
  "status": 1
}
```

### 2.4 更新用户

**接口**: `PUT /api/system/user`

**请求体**:
```json
{
  "id": 1,
  "nickname": "张三（已修改）",
  "mobile": "13800138001",
  "version": 0
}
```

### 2.5 删除用户

**接口**: `DELETE /api/system/user/{id}?version=0`

---

## 3. 字典管理

### 3.1 字典类型 (/api/system/dict/type)

#### 分页查询
`GET /api/system/dict/type/page`

参数：
- `typeCode`: 字典类型编码
- `name`: 类型名称（模糊）
- `status`: 状态

#### 新增字典类型
`POST /api/system/dict/type`

```json
{
  "typeCode": "USER_STATUS",
  "name": "用户状态",
  "status": 1
}
```

### 3.2 字典项 (/api/system/dict/item)

#### 按类型查询字典项
`GET /api/system/dict/item/list?typeCode=USER_STATUS`

#### 新增字典项
`POST /api/system/dict/item`

```json
{
  "typeCode": "USER_STATUS",
  "itemValue": "1",
  "itemLabel": "正常",
  "sort": 1,
  "status": 1
}
```

---

## 4. 角色管理 (/api/system/role)

### 查询角色列表
`GET /api/system/role/list`

### 新增角色
`POST /api/system/role`

```json
{
  "code": "ADMIN",
  "name": "管理员",
  "dataScope": 0,
  "sort": 1,
  "status": 1
}
```

**dataScope 说明**:
- 0: 全部数据
- 1: 本部门数据
- 2: 本部门及子部门数据
- 3: 仅本人数据

---

## 5. 部门管理 (/api/system/dept)

### 查询部门树
`GET /api/system/dept/tree`

### 新增部门
`POST /api/system/dept`

```json
{
  "parentId": 0,
  "name": "技术部",
  "code": "TECH",
  "sort": 1,
  "status": 1
}
```

---

## 6. 菜单管理 (/api/system/menu)

### 查询菜单树
`GET /api/system/menu/tree`

### 新增菜单
`POST /api/system/menu`

```json
{
  "parentId": 0,
  "type": 1,
  "name": "系统管理",
  "path": "/system",
  "icon": "setting",
  "sort": 1,
  "visible": 1,
  "status": 1
}
```

**type 说明**:
- 1: 目录
- 2: 菜单
- 3: 按钮

---

## 7. 系统配置 (/api/system/config)

### 查询配置列表
`GET /api/system/config/list`

### 根据Key获取配置
`GET /api/system/config/key/{configKey}`

### 新增配置
`POST /api/system/config`

```json
{
  "configKey": "sys.upload.maxSize",
  "configName": "上传文件最大大小",
  "configValue": "10485760",
  "configType": 0,
  "status": 1
}
```

---

## 错误码说明

- `200`: 成功
- `400`: 参数错误
- `401`: 未授权
- `403`: 无权限
- `404`: 资源不存在
- `500`: 服务器错误

## 乐观锁使用说明

所有更新和删除操作都需要传递 `version` 参数：

1. 查询时获取 version
2. 更新/删除时传递 version
3. 如果返回错误"数据已被其他人修改"，需要重新查询后再操作

**示例**:
```
1. GET /api/system/user/1  => {id:1, name:"张三", version:0}
2. PUT /api/system/user    => {id:1, name:"李四", version:0}
3. 成功后 version 自动变为 1
```

## 多租户说明

**当前版本**：所有接口中的 `tenantId` 均为硬编码（值为1），实际项目中应从登录上下文获取。

**后续改进**：
1. 实现 JWT 认证
2. 从 Token 中解析 tenantId
3. 通过拦截器自动注入到查询条件

---

更新时间：2025-12-13
