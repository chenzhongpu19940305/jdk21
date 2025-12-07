#!/bin/bash

# Vue项目Docker快速部署脚本

set -e

echo "=========================================="
echo "Vue项目 Docker 部署脚本"
echo "=========================================="

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null; then
    echo "⚠️  Docker Compose 未安装，将使用Docker命令部署"
    USE_COMPOSE=false
else
    USE_COMPOSE=true
fi

# 检查.env.production文件
if [ ! -f ".env.production" ]; then
    echo "⚠️  未找到 .env.production 文件"
    echo "正在创建示例配置文件..."
    cat > .env.production << EOF
# 生产环境配置
# 请修改为实际的服务器地址
VITE_API_BASE_URL=http://localhost:8080/api
EOF
    echo "✅ 已创建 .env.production 文件，请修改其中的服务器地址"
    read -p "按回车键继续，或 Ctrl+C 取消..."
fi

# 选择部署方式
if [ "$USE_COMPOSE" = true ]; then
    echo ""
    echo "使用 Docker Compose 部署..."
    echo ""
    
    # 停止旧容器
    if [ "$(docker-compose ps -q)" ]; then
        echo "停止旧容器..."
        docker-compose down
    fi
    
    # 构建并启动
    echo "构建并启动容器..."
    docker-compose up -d --build
    
    echo ""
    echo "=========================================="
    echo "✅ 部署完成！"
    echo "=========================================="
    echo ""
    echo "查看日志: docker-compose logs -f"
    echo "停止服务: docker-compose down"
    echo "重启服务: docker-compose restart"
    echo ""
    docker-compose ps
else
    echo ""
    echo "使用 Docker 命令部署..."
    echo ""
    
    # 停止并删除旧容器
    if [ "$(docker ps -aq -f name=vue-app)" ]; then
        echo "停止并删除旧容器..."
        docker stop vue-app 2>/dev/null || true
        docker rm vue-app 2>/dev/null || true
    fi
    
    # 构建镜像
    echo "构建Docker镜像..."
    docker build -t vue-app:latest .
    
    # 运行容器
    echo "启动容器..."
    docker run -d \
        --name vue-app \
        -p 80:80 \
        --restart unless-stopped \
        vue-app:latest
    
    echo ""
    echo "=========================================="
    echo "✅ 部署完成！"
    echo "=========================================="
    echo ""
    echo "查看日志: docker logs -f vue-app"
    echo "停止服务: docker stop vue-app"
    echo "重启服务: docker restart vue-app"
    echo ""
    docker ps | grep vue-app
fi

echo ""
echo "应用已启动，访问: http://$(hostname -I | awk '{print $1}')"
echo "或访问: http://localhost"
echo ""

