# IntelliJ IDEA 插件开发指南

## 主流开发方式

### 方式一：使用 IntelliJ Platform Plugin Template（推荐）

这是官方推荐的现代化开发方式，使用 Gradle 构建系统。

**特点：**
- ✅ 官方维护，持续更新
- ✅ 使用 Gradle 构建，配置简单
- ✅ 支持 Kotlin 和 Java
- ✅ 自动生成项目结构
- ✅ 内置开发工具和测试框架

**创建步骤：**
1. 访问 GitHub: https://github.com/JetBrains/intellij-platform-plugin-template
2. 使用 GitHub Template 创建新项目
3. 或使用 IntelliJ IDEA 的 "New Project" -> "IntelliJ Platform Plugin"

### 方式二：使用 IntelliJ IDEA 内置插件开发工具

**特点：**
- ✅ 集成在 IDE 中，无需额外配置
- ✅ 可视化界面设计
- ✅ 快速原型开发

**创建步骤：**
1. File -> New -> Project
2. 选择 "IntelliJ Platform Plugin"
3. 配置项目信息
4. 使用 GUI Designer 设计界面

### 方式三：手动创建 Gradle 项目

**特点：**
- ✅ 完全控制项目结构
- ✅ 适合有经验的开发者
- ✅ 可以自定义构建流程

## 技术栈

### 核心组件

1. **IntelliJ Platform SDK**
   - 提供插件开发 API
   - 包含各种服务和组件

2. **Gradle**
   - 构建工具（推荐）
   - 或使用 Maven（传统方式）

3. **开发语言**
   - Java（传统，广泛支持）
   - Kotlin（现代，推荐）

### 插件类型

1. **Action Plugin** - 添加菜单项和工具栏按钮
2. **Tool Window Plugin** - 创建工具窗口
3. **Language Plugin** - 支持新语言
4. **Theme Plugin** - 自定义主题
5. **Build Tool Plugin** - 构建工具集成

## 项目结构

```
plugin-project/
├── src/
│   ├── main/
│   │   ├── java/          # Java 源代码
│   │   ├── kotlin/        # Kotlin 源代码（可选）
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── plugin.xml  # 插件配置文件（重要！）
│   │       └── messages/       # 国际化资源
│   └── test/              # 测试代码
├── build.gradle.kts      # Gradle 构建配置
├── gradle.properties     # Gradle 属性
└── settings.gradle.kts   # Gradle 设置
```

## 关键文件说明

### 1. plugin.xml
插件的主配置文件，定义：
- 插件元信息（ID、名称、版本）
- 扩展点（Extensions）
- Actions（菜单项、工具栏按钮）
- 服务（Services）
- 监听器（Listeners）

### 2. build.gradle.kts
Gradle 构建配置：
- IntelliJ Platform 版本
- 插件依赖
- 构建任务

### 3. 插件类
实现插件逻辑的 Java/Kotlin 类

## 开发流程

1. **创建项目** - 使用模板或手动创建
2. **配置 plugin.xml** - 定义插件元信息和扩展
3. **编写代码** - 实现插件功能
4. **测试** - 使用内置测试框架
5. **打包** - 生成 .zip 或 .jar 文件
6. **发布** - 上传到 JetBrains Marketplace

## 常用 API

- `AnAction` - 动作类（菜单项、按钮）
- `Project` - 项目对象
- `Editor` - 编辑器对象
- `PsiFile` - PSI 文件对象
- `ToolWindow` - 工具窗口
- `NotificationGroup` - 通知组

## 开发工具

1. **IntelliJ IDEA Ultimate** - 推荐使用（有完整的插件开发支持）
2. **IntelliJ IDEA Community** - 也可以，但功能较少
3. **Plugin DevKit** - 内置插件开发工具

## 学习资源

- 官方文档：https://plugins.jetbrains.com/docs/intellij/
- 示例项目：https://github.com/JetBrains/intellij-sdk-code-samples
- 社区论坛：https://intellij-support.jetbrains.com/hc/en-us/community/topics










