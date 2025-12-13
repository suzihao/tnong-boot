# 登录功能使用文档

## 功能说明

已实现完整的用户登录功能，包括：
- ✅ JWT Token 认证
- ✅ 密码 BCrypt 加密
- ✅ 登录日志记录
- ✅ 用户上下文管理
- ✅ Token 自动验证

## API 接口

### 1. 用户登录

**接口**: `POST /api/auth/login`

**请求示例**:
```json
{
  "username": "admin",
  "password": "123456",
  "tenantCode": "DEFAULT"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "tenantId": 1,
    "username": "admin",
    "nickname": "管理员"
  },
  "timestamp": 1702345678901
}
```

### 2. 用户退出

**接口**: `POST /api/auth/logout`

**请求头**: `Authorization: Bearer {token}`

### 3. 获取当前用户信息

**接口**: `GET /api/auth/info`

**请求头**: `Authorization: Bearer {token}`

## 使用方式

### 1. 登录获取 Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "123456"
  }'
```

### 2. 使用 Token 访问接口

```bash
curl -X GET http://localhost:8080/api/system/user/page \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 核心组件说明

### 1. JwtUtil - JWT 工具类
- 生成 Token
- 解析 Token
- 验证 Token 有效性
- 从 Token 中提取用户信息

### 2. PasswordUtil - 密码工具类
- BCrypt 密码加密
- 密码验证

### 3. UserContext - 用户上下文
- ThreadLocal 存储当前登录用户
- 获取当前用户ID、租户ID、用户名

### 4. JwtAuthenticationFilter - JWT 过滤器
- 拦截所有请求
- 从请求头提取 Token
- 验证 Token 并设置用户上下文

### 5. AuthService - 认证服务
- 用户登录逻辑
- 密码验证
- Token 生成
- 登录日志记录

## 安全特性

1. **密码加密**: 使用 BCrypt 加密，不可逆
2. **Token 机制**: JWT Token，7天有效期
3. **无状态认证**: 不依赖 Session
4. **自动过期**: Token 过期自动失效
5. **登录日志**: 记录所有登录尝试

## 代码示例

### 在 Controller 中获取当前用户

```java
@GetMapping("/page")
public Result<PageResult<SysUserVO>> page(SysUserQueryDTO query) {
    // 从上下文获取租户ID和用户ID
    Long tenantId = UserContext.getTenantId();
    Long currentUserId = UserContext.getUserId();
    
    PageResult<SysUserVO> pageResult = sysUserService.pageList(query, tenantId);
    return Result.success(pageResult);
}
```

### 在 Service 中使用

```java
@Service
public class SomeService {
    
    public void someMethod() {
        // 获取当前登录用户信息
        Long userId = UserContext.getUserId();
        Long tenantId = UserContext.getTenantId();
        String username = UserContext.getUsername();
        
        // 业务逻辑...
    }
}
```

## 配置说明

### JWT 密钥配置

当前密钥硬编码在 `JwtUtil` 中，生产环境建议移到配置文件：

```yaml
jwt:
  secret: your-secret-key-here
  expiration: 604800000  # 7天（毫秒）
```

### Security 白名单

在 `SecurityConfig` 中配置不需要认证的接口：

```java
.requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
```

## 数据库初始化

需要先执行初始化脚本创建测试用户：

```sql
-- 插入测试用户（密码：123456，已BCrypt加密）
INSERT INTO `sys_user` (
    `id`, `tenant_id`, `dept_id`, `username`, `password`, 
    `nickname`, `status`, `created_user`, `updated_user`
) VALUES (
    1, 1, 1, 'admin', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 
    '管理员', 1, 1, 1
);
```

## 注意事项

1. **Token 存储**: 前端需要将 Token 存储在 localStorage 或 sessionStorage
2. **请求头格式**: `Authorization: Bearer {token}`
3. **Token 刷新**: 当前未实现 Token 刷新机制，过期需重新登录
4. **并发登录**: 同一账号可多处登录，每次登录生成新 Token

## 后续优化建议

1. 实现 Token 刷新机制
2. 实现单点登录（踢出其他设备）
3. 实现验证码功能
4. 实现登录失败次数限制
5. 实现在线用户管理
6. 完善登录日志的浏览器和操作系统解析

---

更新时间：2025-12-13
