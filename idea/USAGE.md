# 浏览器启动器插件使用说明

## 功能概述

浏览器启动器插件允许您在 IntelliJ IDEA 中快速创建和管理常用网页的快捷按钮，点击按钮即可在浏览器中打开对应的 URL。

## 使用方法

### 1. 打开工具窗口

有以下几种方式打开浏览器启动器工具窗口：

- **方式一**：菜单栏 -> `Tools` -> `浏览器启动器`
- **方式二**：使用快捷键 `Ctrl + Alt + B`（Windows/Linux）或 `Cmd + Alt + B`（macOS）
- **方式三**：在右侧工具窗口栏找到 "BrowserLauncher" 标签并点击

### 2. 添加 URL 按钮

1. 在工具窗口顶部的输入框中输入 URL
   - 可以输入完整 URL：`https://www.baidu.com`
   - 也可以只输入域名：`www.baidu.com`（会自动添加 `https://`）
2. 按 `Enter` 键或点击"添加"按钮
3. 新的 URL 按钮会出现在列表中

### 3. 打开网页

点击任意 URL 按钮，会在系统默认浏览器中打开对应的网页。

### 4. 删除按钮

点击按钮右侧的"删除"按钮，可以移除不需要的 URL 按钮。

## 功能特点

- ✅ **自动 URL 规范化**：自动为没有协议的 URL 添加 `https://`
- ✅ **多按钮管理**：支持创建多个 URL 按钮
- ✅ **滚动支持**：按钮列表支持滚动，可管理大量 URL
- ✅ **跨平台支持**：Windows、macOS、Linux 均可使用
- ✅ **快捷键支持**：快速打开工具窗口

## 技术实现

### 核心组件

1. **BrowserLauncherAction** - 打开工具窗口的 Action
2. **BrowserLauncherToolWindowFactory** - 工具窗口工厂，创建工具窗口
3. **BrowserLauncherPanel** - 主面板，包含 UI 和业务逻辑
4. **UrlButton** - URL 按钮组件，显示 URL 并提供操作

### 浏览器打开机制

插件使用 Java `Desktop` API 打开浏览器：
- 优先使用 `Desktop.getDesktop().browse()`
- 如果不支持，则使用系统命令：
  - Windows: `rundll32 url.dll,FileProtocolHandler`
  - macOS: `open`
  - Linux: `xdg-open`

## 注意事项

1. **URL 格式**：建议使用完整的 URL（包含协议），插件会自动处理
2. **浏览器选择**：插件使用系统默认浏览器打开网页
3. **按钮持久化**：当前版本按钮不会持久化保存，重启 IDE 后需要重新添加（未来版本可能会添加持久化功能）

## 开发说明

### 项目结构

```
src/main/java/com/example/
├── BrowserLauncherAction.java          # Action 类
├── BrowserLauncherToolWindowFactory.java # 工具窗口工厂
├── BrowserLauncherPanel.java             # 主面板
├── UrlButton.java                        # URL 按钮组件
└── MyPlugin.java                         # 插件主类
```

### 扩展功能建议

未来可以考虑添加以下功能：
- [ ] URL 按钮持久化存储（保存到配置文件）
- [ ] 按钮分组功能
- [ ] 自定义按钮名称
- [ ] 导入/导出 URL 列表
- [ ] 支持指定浏览器（Chrome、Firefox 等）










