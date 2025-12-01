# MinIO 文件存储配置说明

## 1. 安装 MinIO

### 使用 Docker 安装（推荐）

```bash
docker run -d \
  -p 9000:9000 \
  -p 9001:9001 \
  --name minio \
  -e "MINIO_ROOT_USER=minioadmin" \
  -e "MINIO_ROOT_PASSWORD=minioadmin" \
  -v /path/to/data:/data \
  minio/minio server /data --console-address ":9001"
```

### 使用二进制文件安装

1. 下载 MinIO：https://min.io/download
2. 运行：
```bash
minio server /path/to/data --console-address ":9001"
```

## 2. 配置说明

在 `application.yml` 中配置 MinIO 连接信息：

```yaml
minio:
  endpoint: http://localhost:9000      # MinIO 服务地址
  accessKey: minioadmin                 # 访问密钥
  secretKey: minioadmin                 # 密钥
  bucket: demo-bucket                    # 存储桶名称
```

## 3. 访问 MinIO 控制台

- 控制台地址：http://localhost:9001
- 默认用户名：minioadmin
- 默认密码：minioadmin

## 4. 功能说明

### 文件上传接口

- **上传图片**：`POST /api/file/upload/image`
  - 限制：文件大小不超过 10MB
  - 支持格式：所有图片格式（image/*）

- **上传视频**：`POST /api/file/upload/video`
  - 限制：文件大小不超过 100MB
  - 支持格式：所有视频格式（video/*）

### 富文本编辑器集成

在富文本编辑器中：
- 点击图片按钮，选择图片后自动上传到 MinIO
- 点击视频按钮，选择视频后自动上传到 MinIO
- 上传成功后，图片/视频 URL 会自动插入到编辑器中

## 5. 文件存储结构

```
demo-bucket/
  ├── images/          # 图片文件
  │   ├── uuid1.jpg
  │   ├── uuid2.png
  │   └── ...
  └── videos/          # 视频文件
      ├── uuid1.mp4
      ├── uuid2.mov
      └── ...
```

## 6. 注意事项

1. **Bucket 自动创建**：应用启动时会自动检查并创建配置的 bucket
2. **文件访问 URL**：上传成功后返回的 URL 格式为：`http://localhost:9000/demo-bucket/images/uuid.jpg`
3. **生产环境配置**：
   - 修改默认的 accessKey 和 secretKey
   - 配置 HTTPS
   - 设置合适的文件大小限制
   - 配置 CORS 策略（如果需要前端直接访问）

## 7. CORS 配置（可选）

如果需要前端直接访问 MinIO，需要在 MinIO 控制台配置 CORS：

1. 登录 MinIO 控制台
2. 进入 Settings -> CORS
3. 添加规则：
   - Allowed Origins: `*` 或具体的前端域名
   - Allowed Methods: `GET, PUT, POST, DELETE`
   - Allowed Headers: `*`











