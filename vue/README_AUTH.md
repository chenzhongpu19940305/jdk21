# Vue 登录注册功能说明

## 功能特性

1. **用户注册**
   - 支持用户名、密码、邮箱、昵称注册
   - 注册成功后自动登录

2. **用户登录**
   - 支持用户名和密码登录
   - 登录状态持久化（localStorage）

3. **用户状态管理**
   - 使用 Vue 3 Composition API 实现状态管理
   - 自动从 localStorage 恢复登录状态

4. **UI 组件**
   - 登录/注册对话框
   - 根据登录状态显示不同导航栏内容

## 项目结构

```
vue/src/
├── api/
│   ├── index.js          # API 请求封装
│   └── auth.js           # 认证相关 API
├── components/
│   └── LoginDialog.vue   # 登录/注册对话框组件
├── store/
│   └── user.js           # 用户状态管理
└── views/
    └── Juejin.vue        # 掘金页面（已集成登录注册）
```

## 使用说明

### 1. 启动后端服务

确保 Spring Boot 后端服务已启动在 `http://localhost:8080`

### 2. 配置 API 地址

API 地址配置在 `src/api/index.js` 中，默认指向 `http://localhost:8080/api`

也可以通过环境变量配置：
- 创建 `.env` 文件
- 设置 `VITE_API_BASE_URL=http://localhost:8080/api`

### 3. 启动前端服务

```bash
cd vue
npm install
npm run dev
```

### 4. 访问页面

访问 `http://localhost:3000/juejin` 即可看到掘金页面

## 功能演示

### 注册流程

1. 点击导航栏右侧的"注册"按钮
2. 填写注册信息（用户名、密码必填，邮箱、昵称可选）
3. 点击"注册"按钮
4. 注册成功后自动登录

### 登录流程

1. 点击导航栏右侧的"登录"按钮
2. 输入用户名和密码
3. 点击"登录"按钮
4. 登录成功后显示用户信息

### 退出登录

1. 登录后，导航栏右侧显示用户名
2. 点击"退出"按钮即可退出登录

## API 接口

### 注册接口
- **URL**: `POST /api/auth/register`
- **请求体**:
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@example.com",
  "nickname": "测试用户"
}
```

### 登录接口
- **URL**: `POST /api/auth/login`
- **请求体**:
```json
{
  "username": "testuser",
  "password": "123456"
}
```

## 注意事项

1. 确保后端服务已启动并可以访问
2. 如果遇到跨域问题，需要在后端配置 CORS
3. 用户信息存储在 localStorage 中，清除浏览器缓存会退出登录
4. 密码使用 MD5 加密传输（生产环境建议使用 HTTPS）

## 后续优化建议

1. 添加 JWT Token 认证
2. 添加表单验证（如密码强度、邮箱格式等）
3. 添加记住密码功能
4. 添加忘记密码功能
5. 添加用户头像上传
6. 优化错误提示信息








