# Postman 接口集合使用说明

## 导入 Postman Collection

1. **打开 Postman 应用**

2. **导入集合文件**
   - 点击左上角的 `Import` 按钮
   - 选择 `File` 标签
   - 选择 `SpringBoot_Demo.postman_collection.json` 文件
   - 点击 `Import` 完成导入

3. **或者使用拖拽方式**
   - 直接将 `SpringBoot_Demo.postman_collection.json` 文件拖拽到 Postman 窗口中

## 配置环境变量

集合中已配置了 `baseUrl` 变量，默认值为 `http://localhost:8080`

如果需要修改：
1. 在 Postman 中选中集合
2. 点击 `Variables` 标签
3. 修改 `baseUrl` 的值（例如：`http://localhost:8080` 或 `http://your-server:8080`）

## 接口列表

### 1. Hello - 欢迎接口
- **方法**: GET
- **路径**: `/api/hello`
- **描述**: 返回欢迎信息和当前时间
- **响应示例**:
```json
{
    "message": "Hello, Spring Boot 3 with JDK 21!",
    "timestamp": "2024-01-01T12:00:00",
    "javaVersion": "21"
}
```

### 2. Info - 应用信息
- **方法**: GET
- **路径**: `/api/info`
- **描述**: 返回应用信息，包括版本号和 Java 版本
- **响应示例**:
```json
{
    "application": "Spring Boot Demo",
    "version": "1.0.0",
    "javaVersion": "21",
    "springBootVersion": "3.2.0"
}
```

## 使用前准备

1. **启动 Spring Boot 应用**
   ```bash
   cd springboot
   mvn spring-boot:run
   ```

2. **确认服务运行**
   - 应用启动后，默认运行在 `http://localhost:8080`
   - 查看控制台日志确认启动成功

3. **测试接口**
   - 在 Postman 中选择对应的接口
   - 点击 `Send` 按钮发送请求
   - 查看响应结果

## 注意事项

- 确保 Spring Boot 应用已启动
- 确保端口 8080 未被占用
- 如果修改了 `application.yml` 中的端口配置，记得同步更新 Postman 中的 `baseUrl` 变量

