# 注册登录接口说明

## 数据库配置

### 1. 创建数据库

执行 `src/main/resources/sql/schema.sql` 文件创建数据库和表：

```sql
CREATE DATABASE IF NOT EXISTS demo_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE demo_db;
-- 然后执行表创建语句
```

### 2. 修改数据库连接配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root  # 修改为你的 MySQL 用户名
    password: root  # 修改为你的 MySQL 密码
```

## API 接口

### 1. 用户注册

**接口地址**: `POST /api/auth/register`

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "用户名已存在",
  "data": null
}
```

### 2. 用户登录

**接口地址**: `POST /api/auth/login`

**请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "nickname": "测试用户",
    "createTime": "2024-01-01T12:00:00",
    "updateTime": "2024-01-01T12:00:00"
  }
}
```

**错误响应**:
```json
{
  "code": 500,
  "message": "用户名或密码错误",
  "data": null
}
```

## 项目结构

```
springboot/
├── src/main/java/com/example/demo/
│   ├── controller/
│   │   └── AuthController.java      # 认证控制器
│   ├── service/
│   │   └── UserService.java         # 用户服务层
│   ├── mapper/
│   │   └── UserMapper.java          # MyBatis Mapper 接口
│   ├── entity/
│   │   └── User.java                # 用户实体类
│   ├── dto/
│   │   ├── RegisterRequest.java     # 注册请求 DTO
│   │   ├── LoginRequest.java        # 登录请求 DTO
│   │   └── ApiResponse.java         # 统一响应结果
│   └── config/
│       └── MyBatisConfig.java       # MyBatis 配置
├── src/main/resources/
│   ├── mapper/
│   │   └── UserMapper.xml           # MyBatis Mapper XML
│   ├── sql/
│   │   └── schema.sql               # 数据库初始化 SQL
│   └── application.yml              # 应用配置
```

## 技术说明

1. **MyBatis**: 使用 XML 方式编写 SQL，不使用 MyBatis Plus
2. **密码加密**: 使用 MD5 加密（生产环境建议使用 BCrypt）
3. **数据库**: MySQL 8.0+
4. **字段映射**: 自动将数据库下划线命名转换为 Java 驼峰命名

## 测试步骤

1. 启动 MySQL 数据库
2. 执行 `schema.sql` 创建数据库和表
3. 修改 `application.yml` 中的数据库连接信息
4. 启动 Spring Boot 应用
5. 使用 Postman 或其他工具测试接口

## 注意事项

- 密码使用 MD5 加密，实际生产环境应使用更安全的加密方式（如 BCrypt）
- 当前未实现 JWT Token，登录成功后直接返回用户信息
- 建议后续添加参数校验（如使用 @Valid 注解）
- 建议添加异常统一处理机制












