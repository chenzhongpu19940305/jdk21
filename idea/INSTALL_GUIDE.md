# 插件安装指南

## 方式一：在 IntelliJ IDEA 中直接构建（推荐）

### 步骤

1. **打开项目**
   - 在 IntelliJ IDEA 中打开 `idea` 文件夹
   - 等待 Gradle 同步完成（首次打开会自动下载依赖）

2. **构建插件**
   - 方法 A：使用 Gradle 工具窗口
     - 打开右侧 "Gradle" 工具窗口
     - 展开项目 -> Tasks -> intellij
     - 双击 `buildPlugin` 任务
   
   - 方法 B：使用终端
     ```bash
     ./gradlew buildPlugin
     ```
     或 Windows:
     ```cmd
     gradlew.bat buildPlugin
     ```

3. **查找生成的插件文件**
   - 构建完成后，插件文件位于：`build/distributions/`
   - 文件名格式：`MyIntelliJPlugin-1.0.0.zip`

4. **安装插件**
   - 打开 IntelliJ IDEA
   - File -> Settings（或 Preferences）-> Plugins
   - 点击右上角齿轮图标 -> "Install Plugin from Disk..."
   - 选择 `build/distributions/MyIntelliJPlugin-1.0.0.zip`
   - 点击 OK，然后重启 IDEA

## 方式二：使用批处理脚本（Windows）

1. **双击运行 `build_plugin.bat`**
   - 脚本会自动执行构建过程
   - 构建完成后会自动打开 `build/distributions/` 目录

2. **安装插件**
   - 按照方式一的第 4 步操作

## 方式三：手动使用 Gradle 命令

### Windows

```cmd
cd idea
gradlew.bat clean buildPlugin
```

### macOS/Linux

```bash
cd idea
chmod +x gradlew
./gradlew clean buildPlugin
```

## 验证安装

安装成功后：

1. 打开菜单 `Tools` -> `浏览器启动器`
2. 或使用快捷键 `Ctrl + Alt + B`（Windows/Linux）或 `Cmd + Alt + B`（macOS）
3. 工具窗口应该在右侧打开

## 常见问题

### 1. Gradle 同步失败

**解决方案**：
- 检查网络连接
- 确保 JDK 17+ 已安装并配置
- 在 IDEA 中：File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle
  - 确保 "Gradle JVM" 设置为 JDK 17+

### 2. 构建失败：找不到 Gradle Wrapper

**解决方案**：
- 在 IDEA 中：File -> Settings -> Build, Execution, Deployment -> Build Tools -> Gradle
  - 选择 "Use Gradle from: 'wrapper' task in build script"
  - 或手动运行：`gradle wrapper`

### 3. 插件安装后无法使用

**解决方案**：
- 检查 IDEA 版本是否兼容（需要 2023.3+）
- 查看 IDEA 日志：Help -> Show Log in Explorer
- 确保插件已启用：Settings -> Plugins -> 找到"浏览器启动器"并确保已勾选

### 4. 找不到 build/distributions 目录

**解决方案**：
- 确保构建成功完成
- 检查控制台是否有错误信息
- 尝试先运行 `gradlew clean` 再运行 `gradlew buildPlugin`

## 插件文件结构

构建后的 ZIP 文件包含：

```
MyIntelliJPlugin-1.0.0.zip
└── MyIntelliJPlugin/
    ├── lib/
    │   └── MyIntelliJPlugin.jar  # 编译后的插件代码
    └── META-INF/
        └── plugin.xml             # 插件配置文件
```

## 卸载插件

1. File -> Settings -> Plugins
2. 找到"浏览器启动器"插件
3. 点击 "Uninstall"
4. 重启 IDEA





