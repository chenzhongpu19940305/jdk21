# 浏览器启动器

一个简单的 Windows 客户端应用，可以通过输入 URL 创建快捷按钮，点击按钮在谷歌浏览器中打开对应的 URL 页面。

## 功能特点

- ✅ 输入 URL 创建快捷按钮
- ✅ 点击按钮在 Chrome 浏览器中打开 URL
- ✅ 支持删除已添加的按钮
- ✅ 自动规范化 URL（自动添加 https://）
- ✅ 支持回车键快速添加
- ✅ 滚动列表支持多个按钮

## 运行要求

- Python 3.6+
- Windows 操作系统
- Google Chrome 浏览器（可选，如果没有会使用默认浏览器）

## 使用方法

### 方式一：直接运行 Python 脚本

```bash
python browser_launcher.py
```

### 方式二：打包为 EXE 可执行文件

详细步骤请查看 [BUILD.md](BUILD.md) 文件。

**快速打包（推荐）：**

1. 双击运行 `build_exe.bat` 文件
2. 等待打包完成
3. 在 `dist` 文件夹中找到 `浏览器启动器.exe`

**手动打包：**

```bash
# 1. 安装 PyInstaller
pip install pyinstaller

# 2. 打包
pyinstaller --onefile --windowed --name="浏览器启动器" --clean browser_launcher.py

# 3. 在 dist 文件夹中找到生成的 exe 文件
```

2. 在输入框中输入 URL（例如：`www.baidu.com` 或 `https://www.google.com`）

3. 按回车键或点击"添加"按钮创建快捷按钮

4. 点击创建的按钮，会在 Chrome 浏览器中打开对应的 URL

5. 点击"删除"按钮可以移除不需要的快捷按钮

## 注意事项

- 如果系统没有安装 Chrome，程序会自动使用系统默认浏览器
- URL 会自动添加 `https://` 前缀（如果没有协议）
- 支持输入完整 URL 或域名

