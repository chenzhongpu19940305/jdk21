# Spring Boot 3 项目

这是一个使用 Spring Boot 3.2 和 JDK 21 构建的标准项目。

## 技术栈

- **Java**: 21
- **Spring Boot**: 3.2.0
- **构建工具**: Maven
- **数据库**: H2 (嵌入式数据库)
- **ORM**: Spring Data JPA

## 项目结构

```
springboot/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── DemoApplication.java      # 主应用类
│   │   │   └── controller/
│   │   │       └── HelloController.java  # 示例控制器
│   │   └── resources/
│   │       └── application.yml           # 应用配置
│   └── test/
│       └── java/com/example/demo/
│           ├── DemoApplicationTests.java
│           └── controller/
│               └── HelloControllerTest.java
├── pom.xml                               # Maven 配置
└── README.md
```

## 快速开始

### 前置要求

- JDK 21
- Maven 3.6+

### 运行项目

1. **编译项目**
   ```bash
   mvn clean compile
   ```

2. **运行应用**
   ```bash
   mvn spring-boot:run
   ```
   或者直接运行 `DemoApplication.java` 的 main 方法

3. **访问应用**
   - API 接口: http://localhost:8080/api/hello
   - 应用信息: http://localhost:8080/api/info
   - H2 控制台: http://localhost:8080/h2-console

### 构建项目

```bash
mvn clean package
```

构建完成后，可执行 JAR 文件位于 `target/demo-1.0.0.jar`

运行 JAR 文件：
```bash
java -jar target/demo-1.0.0.jar
```

### 运行测试

```bash
mvn test
```

## API 接口

### GET /api/hello
返回欢迎信息和当前时间

**响应示例:**
```json
{
  "message": "Hello, Spring Boot 3 with JDK 21!",
  "timestamp": "2024-01-01T12:00:00",
  "javaVersion": "21"
}
```

### GET /api/info
返回应用信息

**响应示例:**
```json
{
  "application": "Spring Boot Demo",
  "version": "1.0.0",
  "javaVersion": "21",
  "springBootVersion": "3.2.0"
}
```

## 配置说明

主要配置在 `application.yml` 中：

- **服务器端口**: 8080
- **数据库**: H2 内存数据库
- **JPA**: 自动创建表结构
- **H2 控制台**: 已启用，访问路径 `/h2-console`

## 开发建议

1. 使用 IDE (IntelliJ IDEA 或 Eclipse) 导入 Maven 项目
2. 确保 IDE 使用 JDK 21
3. 安装 Lombok 插件（如果使用 IDE）
4. 使用 Spring Boot DevTools 实现热部署

## 依赖说明

- **spring-boot-starter-web**: Web 开发支持
- **spring-boot-starter-data-jpa**: JPA 数据访问
- **h2**: 嵌入式数据库
- **lombok**: 简化 Java 代码
- **spring-boot-devtools**: 开发工具，支持热部署








