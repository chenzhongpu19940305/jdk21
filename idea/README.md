# 浏览器启动器 - IntelliJ IDEA 插件

这是一个 IntelliJ IDEA 插件，提供浏览器启动器功能，可以快速打开常用网页。

## 功能特点

- ✅ 输入 URL 创建快捷按钮
- ✅ 点击按钮在浏览器中打开网页
- ✅ 支持管理多个 URL 按钮
- ✅ 自动规范化 URL（自动添加 https://）
- ✅ 支持删除按钮
- ✅ 工具窗口界面，方便使用

## 项目结构

```
idea/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/
│       │       ├── BrowserLauncherAction.java          # 打开工具窗口的 Action
│       │       ├── BrowserLauncherToolWindowFactory.java # 工具窗口工厂
│       │       ├── BrowserLauncherPanel.java             # 主面板（UI）
│       │       ├── UrlButton.java                        # URL 按钮组件
│       │       └── MyPlugin.java                         # 插件主类
│       └── resources/
│           └── META-INF/
│               └── plugin.xml                            # 插件配置文件
├── build.gradle.kts                   # Gradle 构建配置
├── settings.gradle.kts                # Gradle 设置
├── gradle.properties                  # Gradle 属性
└── README.md                          # 项目说明
```

## 开发环境要求

- **JDK**: 17 或更高版本
- **IntelliJ IDEA**: 2023.3 或更高版本（推荐 Ultimate Edition）
- **Gradle**: 7.0+（项目会自动下载）

## 快速开始

### 1. 在 IntelliJ IDEA 中打开项目

1. 打开 IntelliJ IDEA
2. File -> Open -> 选择 `idea` 文件夹
3. 等待 Gradle 同步完成

### 2. 运行插件

1. 点击 "Run" -> "Run 'Plugin'"
2. 或使用快捷键 `Shift + F10`
3. 会启动一个新的 IntelliJ IDEA 实例，插件已加载

### 3. 测试插件

在新启动的 IDE 中：
1. 打开菜单 "Tools" -> "浏览器启动器"
2. 或使用快捷键 `Ctrl + Alt + B`
3. 工具窗口会在右侧打开
4. 输入 URL（如：`www.baidu.com`）后按回车或点击"添加"
5. 点击创建的按钮，会在浏览器中打开对应的 URL

## 构建插件

### 使用 Gradle 命令

```bash
# 构建插件
./gradlew buildPlugin

# 生成的插件文件在 build/distributions/ 目录下
```

### 使用 IntelliJ IDEA

1. Build -> Build Project
2. Build -> Prepare Plugin Module 'idea' For Deployment
3. 生成的插件文件在 `build/distributions/` 目录

## 安装插件

### 方式一：从文件安装

1. File -> Settings -> Plugins
2. 点击齿轮图标 -> "Install Plugin from Disk..."
3. 选择生成的 `.zip` 文件

### 方式二：本地安装

1. 将生成的插件文件复制到：
   - Windows: `%APPDATA%\JetBrains\IntelliJIdea<version>\plugins`
   - macOS: `~/Library/Application Support/JetBrains/IntelliJIdea<version>/plugins`
   - Linux: `~/.local/share/JetBrains/IntelliJIdea<version>/plugins`

## 开发指南

### 添加新的 Action

1. 创建新的 Action 类，继承 `AnAction`
2. 在 `plugin.xml` 中注册 Action
3. 实现 `actionPerformed` 方法

### 添加工具窗口

1. 创建 ToolWindowFactory 类
2. 在 `plugin.xml` 的 `<extensions>` 中注册
3. 实现窗口内容

### 使用 PSI（Program Structure Interface）

PSI 用于分析和操作代码结构：
- `PsiFile` - 文件对象
- `PsiElement` - 代码元素
- `PsiManager` - PSI 管理器

## 常用 API

- `AnAction` - 动作类（菜单项、工具栏按钮）
- `Project` - 项目对象
- `Editor` - 编辑器对象
- `PsiFile` - PSI 文件对象
- `ToolWindow` - 工具窗口
- `NotificationGroup` - 通知组
- `Messages` - 消息对话框

## 发布插件

### 发布到 JetBrains Marketplace

1. 注册 JetBrains 账号
2. 访问 https://plugins.jetbrains.com/
3. 上传插件文件
4. 填写插件信息
5. 提交审核

### 本地分发

直接分发生成的 `.zip` 文件给其他用户安装。

## 参考资源

- [IntelliJ Platform Plugin SDK 文档](https://plugins.jetbrains.com/docs/intellij/)
- [示例代码](https://github.com/JetBrains/intellij-sdk-code-samples)
- [官方模板](https://github.com/JetBrains/intellij-platform-plugin-template)

