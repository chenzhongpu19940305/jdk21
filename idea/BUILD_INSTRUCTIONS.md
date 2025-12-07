# 构建插件为可安装文件

## 方法一：在 IntelliJ IDEA 中构建（最简单，推荐）

### 步骤：

1. **在 IntelliJ IDEA 中打开项目**
   - File -> Open -> 选择 `idea` 文件夹
   - 等待 Gradle 同步完成（首次可能需要几分钟）

2. **构建插件**
   - 打开底部 "Terminal" 标签
   - 运行以下命令之一：
   
   **Windows:**
   ```cmd
   gradlew.bat buildPlugin
   ```
   
   **macOS/Linux:**
   ```bash
   chmod +x gradlew
   ./gradlew buildPlugin
   ```

3. **找到生成的插件文件**
   - 构建成功后，文件位于：`build/distributions/MyIntelliJPlugin-1.0.0.zip`

4. **安装插件**
   - File -> Settings -> Plugins
   - 点击右上角齿轮图标 -> "Install Plugin from Disk..."
   - 选择 `build/distributions/MyIntelliJPlugin-1.0.0.zip`
   - 点击 OK，重启 IDEA

## 方法二：使用批处理脚本（Windows）

1. **双击运行 `build_plugin.bat`**
   - 脚本会自动执行构建
   - 构建完成后会自动打开 `build/distributions/` 目录

2. **安装插件**
   - 按照方法一的第 4 步操作

## 方法三：手动下载 Gradle Wrapper（如果方法一失败）

如果 Gradle Wrapper 不存在，可以手动下载：

1. **下载 gradle-wrapper.jar**
   - 访问：https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
   - 保存到：`idea/gradle/wrapper/gradle-wrapper.jar`

2. **然后运行构建命令**
   ```cmd
   gradlew.bat buildPlugin
   ```

## 验证构建成功

构建成功后，您应该看到：

```
BUILD SUCCESSFUL in XXs
```

并且 `build/distributions/` 目录下有 `.zip` 文件。

## 插件文件位置

构建后的插件文件：
- **路径**：`idea/build/distributions/MyIntelliJPlugin-1.0.0.zip`
- **大小**：约 50-200 KB（取决于依赖）
- **格式**：标准的 IntelliJ IDEA 插件 ZIP 文件

## 安装后的验证

安装成功后：

1. 打开菜单 `Tools` -> `浏览器启动器`
2. 或使用快捷键 `Ctrl + Alt + B`（Windows/Linux）或 `Cmd + Alt + B`（macOS）
3. 工具窗口应该在右侧打开
4. 可以输入 URL 并创建按钮

## 常见问题

### Q: 构建时提示 "gradlew.bat 不是内部或外部命令"

**A:** 确保您在 `idea` 目录下运行命令，或者使用完整路径。

### Q: 构建失败，提示找不到 Java

**A:** 
- 确保已安装 JDK 17 或更高版本
- 设置 JAVA_HOME 环境变量
- 或在 IDEA 中：File -> Settings -> Build Tools -> Gradle -> Gradle JVM，选择正确的 JDK

### Q: 构建很慢

**A:** 
- 首次构建需要下载 IntelliJ Platform SDK（约 500MB），这是正常的
- 确保网络连接正常
- 后续构建会快很多

### Q: 插件安装后无法使用

**A:**
- 检查 IDEA 版本（需要 2023.3 或更高版本）
- 确保插件已启用：Settings -> Plugins -> 找到"浏览器启动器"并确保已勾选
- 重启 IDEA










