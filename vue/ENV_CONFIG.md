# Vite 环境变量配置说明

## 为什么 `npm run build` 使用 `.env.production`？

这是 **Vite 的默认行为**，Vite 会根据不同的命令模式（mode）自动加载对应的环境变量文件。

## Vite 环境变量加载规则

### 1. 默认模式（Mode）

Vite 根据不同的命令自动设置模式：

| 命令 | 默认模式 | 加载的环境文件 |
|------|---------|---------------|
| `npm run dev` | `development` | `.env.development` |
| `npm run build` | `production` | `.env.production` |
| `npm run preview` | `production` | `.env.production` |

### 2. 环境变量文件优先级

Vite 会按以下顺序加载环境变量文件（后面的会覆盖前面的）：

1. `.env` - 所有环境都会加载
2. `.env.local` - 所有环境都会加载（会被 git 忽略）
3. `.env.[mode]` - 只在指定模式下加载（如 `.env.production`）
4. `.env.[mode].local` - 只在指定模式下加载（会被 git 忽略）

**示例：**
- 执行 `npm run build` 时，会加载：
  1. `.env`
  2. `.env.local`
  3. `.env.production`
  4. `.env.production.local`

### 3. 如何自定义模式

如果你想使用其他模式，可以通过 `--mode` 参数指定：

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "build:dev": "vite build --mode development",
    "build:test": "vite build --mode test",
    "build:prod": "vite build --mode production"
  }
}
```

这样：
- `npm run build:dev` 会使用 `.env.development`
- `npm run build:test` 会使用 `.env.test`
- `npm run build:prod` 会使用 `.env.production`

### 4. 在代码中访问环境变量

只有以 `VITE_` 开头的环境变量才会暴露给客户端代码：

```javascript
// ✅ 正确 - 可以访问
console.log(import.meta.env.VITE_API_BASE_URL)

// ❌ 错误 - 无法访问（会被过滤掉）
console.log(import.meta.env.API_BASE_URL)
```

### 5. 实际示例

假设你有以下环境变量文件：

**.env.development**
```
VITE_API_BASE_URL=http://localhost:8080/api
```

**.env.production**
```
VITE_API_BASE_URL=http://192.168.1.100:8080/api
```

当你执行：
- `npm run dev` → 使用 `http://localhost:8080/api`
- `npm run build` → 构建时使用 `http://192.168.1.100:8080/api`，并打包到代码中

### 6. 验证当前使用的环境变量

你可以在代码中打印查看：

```javascript
console.log('当前模式:', import.meta.env.MODE)
console.log('API地址:', import.meta.env.VITE_API_BASE_URL)
console.log('是否为生产环境:', import.meta.env.PROD)
```

### 7. 为什么这样设计？

- **开发环境**：通常连接本地后端服务（localhost）
- **生产环境**：需要连接实际的服务器地址

通过不同的环境文件，可以在不修改代码的情况下，为不同环境配置不同的参数。

## 总结

`npm run build` 使用 `.env.production` 是因为：
1. Vite 的 `build` 命令默认模式是 `production`
2. Vite 会自动加载 `.env.production` 文件
3. 这是 Vite 的标准行为，确保生产构建使用正确的配置

如果你想在构建时使用其他配置，可以：
- 创建对应的 `.env.[mode]` 文件
- 使用 `vite build --mode [mode]` 指定模式

