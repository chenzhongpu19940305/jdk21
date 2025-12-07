# Vue项目部署指南

## 部署到Linux服务器

### 1. 配置生产环境变量

在项目根目录创建 `.env.production` 文件，配置生产环境的服务器地址：

```bash
# 生产环境配置
# 修改为实际的服务器地址
VITE_API_BASE_URL=http://your-server-ip:8080/api
```

**示例：**
- 如果后端服务在同一台服务器：`VITE_API_BASE_URL=http://localhost:8080/api`
- 如果后端服务在其他服务器：`VITE_API_BASE_URL=http://192.168.1.100:8080/api`
- 如果使用域名：`VITE_API_BASE_URL=http://api.yourdomain.com/api`

### 2. 安装依赖

```bash
cd vue
npm install
```

### 3. 构建生产版本

```bash
npm run build
```

构建完成后，会在 `dist` 目录生成生产环境的静态文件。

### 4. 部署到服务器

#### 方式一：使用Nginx部署（推荐）

1. **将dist目录上传到服务器**
   ```bash
   scp -r dist/* user@your-server:/var/www/html/
   ```

2. **配置Nginx**
   
   编辑 `/etc/nginx/sites-available/default` 或创建新的配置文件：
   
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;  # 修改为你的域名或IP
       
       root /var/www/html;
       index index.html;
       
       location / {
           try_files $uri $uri/ /index.html;
       }
       
       # API代理（可选，如果前后端分离部署）
       location /api {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
       }
   }
   ```

3. **重启Nginx**
   ```bash
   sudo nginx -t  # 测试配置
   sudo systemctl restart nginx
   ```

#### 方式二：使用Node.js服务器（如serve）

1. **安装serve**
   ```bash
   npm install -g serve
   ```

2. **启动服务**
   ```bash
   serve -s dist -l 3000
   ```

#### 方式三：使用Docker部署（推荐，最简单）

Docker部署是最简单的方式，项目已经包含了完整的Docker配置文件。

**前置要求：**
- Linux服务器已安装Docker和Docker Compose
- 如果未安装，可以运行：
  ```bash
  # 安装Docker
  curl -fsSL https://get.docker.com -o get-docker.sh
  sh get-docker.sh
  
  # 安装Docker Compose
  sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  sudo chmod +x /usr/local/bin/docker-compose
  ```

**部署步骤：**

1. **配置生产环境变量**
   
   在项目根目录创建 `.env.production` 文件：
   ```bash
   VITE_API_BASE_URL=http://your-backend-server:8080/api
   ```
   
   **注意：** 如果后端服务在Docker容器中，可以使用容器名称或服务名称作为主机名。

2. **上传项目文件到服务器**
   ```bash
   # 使用scp上传（在本地执行）
   scp -r vue/* user@your-server:/opt/vue-app/
   
   # 或者使用git克隆
   git clone your-repo
   cd vue
   ```

3. **使用Docker Compose部署（推荐）**
   ```bash
   cd /opt/vue-app
   
   # 构建并启动容器
   docker-compose up -d --build
   
   # 查看日志
   docker-compose logs -f
   
   # 停止服务
   docker-compose down
   ```

4. **或者使用Docker命令部署**
   ```bash
   # 构建镜像
   docker build -t vue-app:latest .
   
   # 运行容器
   docker run -d \
     --name vue-app \
     -p 80:80 \
     --restart unless-stopped \
     vue-app:latest
   
   # 查看日志
   docker logs -f vue-app
   
   # 停止容器
   docker stop vue-app
   docker rm vue-app
   ```

5. **使用构建参数传递环境变量（可选）**
   
   如果需要动态设置环境变量，可以使用 `Dockerfile.build-arg`：
   ```bash
   docker build \
     --build-arg VITE_API_BASE_URL=http://192.168.1.100:8080/api \
     -f Dockerfile.build-arg \
     -t vue-app:latest .
   
   docker run -d -p 80:80 vue-app:latest
   ```

6. **验证部署**
   
   访问 `http://your-server-ip`，应该能看到Vue应用。

**Docker部署的优势：**
- ✅ 环境一致，避免"在我机器上能跑"的问题
- ✅ 一键部署，无需手动配置Nginx
- ✅ 易于扩展和维护
- ✅ 支持快速回滚
- ✅ 包含Gzip压缩和缓存优化

**常用Docker命令：**
```bash
# 查看运行中的容器
docker ps

# 查看容器日志
docker logs vue-app

# 进入容器（调试用）
docker exec -it vue-app sh

# 重启容器
docker restart vue-app

# 更新应用（重新构建）
docker-compose up -d --build

# 查看镜像大小
docker images vue-app
```

### 5. 验证部署

访问 `http://your-server-ip` 或 `http://your-domain.com`，检查应用是否正常运行。

### 6. 环境变量说明

Vite在构建时会根据不同的模式加载不同的环境变量文件：

- **开发环境** (npm run dev): 加载 `.env.development`
- **生产环境** (npm run build): 加载 `.env.production`

环境变量必须以 `VITE_` 开头才能在代码中访问。

### 7. 运行时配置

除了构建时配置，应用还支持运行时配置（通过localStorage）：

1. 打开应用后，点击导航栏的"服务器配置"按钮
2. 输入新的服务器地址
3. 保存后刷新页面即可生效

**优先级：** localStorage配置 > 环境变量 > 默认值

### 8. 常见问题

#### 问题1：构建后API请求失败
- 检查 `.env.production` 中的 `VITE_API_BASE_URL` 是否正确
- 确认后端服务已启动并可访问
- 检查浏览器控制台的网络请求

#### 问题2：路由刷新404
- 确保Nginx配置了 `try_files $uri $uri/ /index.html;`
- 这是Vue Router的history模式必需的配置

#### 问题3：跨域问题
- 如果前后端分离部署，需要配置CORS
- 或者使用Nginx反向代理统一域名

### 9. 生产环境优化建议

1. **启用Gzip压缩**（Nginx配置）
   ```nginx
   gzip on;
   gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
   ```

2. **配置HTTPS**（使用Let's Encrypt）
   ```bash
   sudo certbot --nginx -d your-domain.com
   ```

3. **设置缓存策略**
   ```nginx
   location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
       expires 1y;
       add_header Cache-Control "public, immutable";
   }
   ```

### 10. 快速部署脚本示例

创建 `deploy.sh`：

```bash
#!/bin/bash

# 配置服务器信息
SERVER_USER="your-user"
SERVER_HOST="your-server-ip"
SERVER_PATH="/var/www/html"

# 构建项目
echo "正在构建项目..."
npm run build

# 上传文件
echo "正在上传文件..."
scp -r dist/* ${SERVER_USER}@${SERVER_HOST}:${SERVER_PATH}

echo "部署完成！"
```

使用方法：
```bash
chmod +x deploy.sh
./deploy.sh
```

