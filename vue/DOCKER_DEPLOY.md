# Docker 部署指南

## 快速开始

### 1. 前置要求

- Linux服务器
- 已安装Docker（版本 20.10+）
- 已安装Docker Compose（版本 2.0+，可选但推荐）

### 2. 安装Docker（如果未安装）

```bash
# 安装Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# 启动Docker服务
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

### 3. 安装Docker Compose（如果未安装）

```bash
# 下载最新版本
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 添加执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

## 部署步骤

### 方式一：使用Docker Compose（推荐）

#### 1. 准备项目文件

```bash
# 上传项目到服务器（在本地执行）
scp -r vue/* user@your-server:/opt/vue-app/

# 或者使用git
git clone your-repo
cd vue
```

#### 2. 配置环境变量

在项目根目录创建 `.env.production` 文件：

```bash
cd /opt/vue-app
nano .env.production
```

内容示例：
```bash
# 生产环境配置
# 修改为实际的服务器地址
VITE_API_BASE_URL=http://192.168.1.100:8080/api
```

**重要说明：**
- 如果后端服务也在Docker中，可以使用服务名称（如 `http://backend:8080/api`）
- 如果后端在宿主机，可以使用 `http://host.docker.internal:8080/api`（Docker Desktop）或宿主机IP
- 如果使用域名，直接使用域名即可

#### 3. 启动服务

```bash
# 构建并启动
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 查看运行状态
docker-compose ps
```

#### 4. 访问应用

打开浏览器访问：`http://your-server-ip`

### 方式二：使用Docker命令

#### 1. 构建镜像

```bash
cd /opt/vue-app
docker build -t vue-app:latest .
```

#### 2. 运行容器

```bash
docker run -d \
  --name vue-app \
  -p 80:80 \
  --restart unless-stopped \
  vue-app:latest
```

#### 3. 查看日志

```bash
docker logs -f vue-app
```

### 方式三：使用构建参数传递环境变量

如果需要动态设置环境变量，可以使用 `Dockerfile.build-arg`：

```bash
# 构建时传递环境变量
docker build \
  --build-arg VITE_API_BASE_URL=http://192.168.1.100:8080/api \
  -f Dockerfile.build-arg \
  -t vue-app:latest .

# 运行容器
docker run -d -p 80:80 vue-app:latest
```

## 常用操作

### 查看容器状态

```bash
# 查看运行中的容器
docker ps

# 查看所有容器（包括停止的）
docker ps -a

# 查看容器详细信息
docker inspect vue-app
```

### 查看日志

```bash
# 实时查看日志
docker logs -f vue-app

# 查看最近100行日志
docker logs --tail 100 vue-app

# 查看指定时间段的日志
docker logs --since 30m vue-app
```

### 重启容器

```bash
# 重启容器
docker restart vue-app

# 停止容器
docker stop vue-app

# 启动容器
docker start vue-app
```

### 更新应用

```bash
# 方式1：使用Docker Compose
cd /opt/vue-app
docker-compose down
docker-compose up -d --build

# 方式2：使用Docker命令
docker stop vue-app
docker rm vue-app
docker build -t vue-app:latest .
docker run -d --name vue-app -p 80:80 --restart unless-stopped vue-app:latest
```

### 进入容器（调试用）

```bash
# 进入容器
docker exec -it vue-app sh

# 查看nginx配置
cat /etc/nginx/conf.d/default.conf

# 查看静态文件
ls -la /usr/share/nginx/html
```

### 清理资源

```bash
# 删除容器
docker rm vue-app

# 删除镜像
docker rmi vue-app:latest

# 清理未使用的资源
docker system prune -a
```

## 配置说明

### 修改端口

如果80端口被占用，可以修改 `docker-compose.yml`：

```yaml
ports:
  - "3000:80"  # 将80改为3000
```

或者使用Docker命令：

```bash
docker run -d -p 3000:80 vue-app:latest
```

### 自定义Nginx配置

如果需要自定义Nginx配置：

1. 修改 `nginx.conf` 文件
2. 在 `docker-compose.yml` 中取消注释volumes部分：

```yaml
volumes:
  - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
```

3. 重新构建：

```bash
docker-compose up -d --build
```

### 配置HTTPS

如果需要HTTPS，可以使用Nginx反向代理或Let's Encrypt：

```bash
# 使用Nginx反向代理（在宿主机上）
# 或者使用certbot自动配置
```

## 故障排查

### 问题1：容器无法启动

```bash
# 查看详细错误信息
docker logs vue-app

# 检查端口是否被占用
netstat -tulpn | grep 80

# 检查Docker服务状态
sudo systemctl status docker
```

### 问题2：无法访问应用

```bash
# 检查容器是否运行
docker ps

# 检查端口映射
docker port vue-app

# 检查防火墙
sudo ufw status
sudo ufw allow 80/tcp
```

### 问题3：API请求失败

- 检查 `.env.production` 中的 `VITE_API_BASE_URL` 是否正确
- 确认后端服务已启动并可访问
- 检查网络连接（如果后端在Docker中，确保在同一网络）

### 问题4：路由刷新404

确保 `nginx.conf` 中配置了：

```nginx
location / {
    try_files $uri $uri/ /index.html;
}
```

## 生产环境建议

1. **使用Docker Compose**：便于管理和维护
2. **配置自动重启**：`restart: unless-stopped`
3. **设置资源限制**：在 `docker-compose.yml` 中添加：

```yaml
deploy:
  resources:
    limits:
      cpus: '0.5'
      memory: 512M
```

4. **配置日志轮转**：避免日志文件过大
5. **定期更新镜像**：保持安全性
6. **使用健康检查**：配置健康检查端点

## 与后端服务一起部署

如果后端也在Docker中，可以在 `docker-compose.yml` 中添加后端服务：

```yaml
version: '3.8'

services:
  backend:
    image: your-backend-image:latest
    container_name: backend
    ports:
      - "8080:8080"
    networks:
      - vue-network

  vue-app:
    build:
      context: .
      dockerfile: Dockerfile
    image: vue-app:latest
    container_name: vue-app
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - vue-network
    environment:
      - VITE_API_BASE_URL=http://backend:8080/api

networks:
  vue-network:
    driver: bridge
```

然后在 `.env.production` 中使用服务名称：

```bash
VITE_API_BASE_URL=http://backend:8080/api
```

## 总结

Docker部署的优势：
- ✅ 环境一致性
- ✅ 一键部署
- ✅ 易于扩展
- ✅ 快速回滚
- ✅ 资源隔离

按照以上步骤，你就可以轻松地将Vue应用部署到Linux服务器上了！


