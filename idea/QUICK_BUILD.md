# 快速构建指南

## 最简单的方法

### 在 IntelliJ IDEA 中：

1. **打开项目**
   - File -> Open -> 选择 `idea` 文件夹

2. **等待同步**
   - IDEA 会自动检测 Gradle 项目并开始同步
   - 首次同步可能需要几分钟（下载依赖）

3. **构建插件**
   - 打开底部 "Terminal" 标签
   - 运行命令：
     ```bash
     ./gradlew buildPlugin
     ```
     或 Windows:
     ```cmd
     gradlew.bat buildPlugin
     ```

4. **找到插件文件**
   - 构建完成后，在项目根目录下：
     - `build/distributions/MyIntelliJPlugin-1.0.0.zip`

5. **安装插件**
   - File -> Settings -> Plugins
   - 点击齿轮图标 -> "Install Plugin from Disk..."
   - 选择 `build/distributions/MyIntelliJPlugin-1.0.0.zip`
   - 点击 OK，重启 IDEA

## 如果遇到问题

### 问题：没有 Gradle Wrapper

**解决**：在 IDEA 终端运行：
```bash
gradle wrapper --gradle-version 8.5
```

### 问题：Java 版本不对

**解决**：
- 确保安装了 JDK 17 或更高版本
- 在 IDEA 中：File -> Project Structure -> Project
  - 设置 "SDK" 为 JDK 17+

### 问题：构建很慢

**解决**：
- 首次构建需要下载 IntelliJ Platform SDK（约 500MB）
- 后续构建会快很多
- 确保网络连接正常

## 验证构建成功

构建成功后，您应该看到：

```
BUILD SUCCESSFUL in XXs
```

并且 `build/distributions/` 目录下有 `.zip` 文件。









