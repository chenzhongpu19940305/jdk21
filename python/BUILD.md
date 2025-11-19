# 打包为可执行文件（EXE）步骤

## 方法一：使用 PyInstaller（推荐）

### 1. 安装 PyInstaller

```bash
pip install pyinstaller
```

### 2. 打包命令

在 `python` 文件夹下执行：

```bash
pyinstaller --onefile --windowed --name="浏览器启动器" --icon=NONE browser_launcher.py
```

或者使用更详细的配置：

```bash
pyinstaller --onefile --windowed --name="浏览器启动器" --add-data="README.md;." browser_launcher.py
```

### 3. 参数说明

- `--onefile`: 打包成单个可执行文件
- `--windowed`: 不显示控制台窗口（GUI 程序）
- `--name`: 指定生成的 exe 文件名
- `--icon`: 指定图标文件（可选，需要 .ico 格式）
- `--add-data`: 添加额外文件（可选）

### 4. 生成的文件位置

打包完成后，可执行文件会在 `dist` 文件夹中：
```
python/
  ├── dist/
  │   └── 浏览器启动器.exe  ← 这就是生成的可执行文件
  ├── build/
  └── browser_launcher.spec
```

### 5. 简化打包命令（推荐）

使用以下命令可以生成更简洁的 exe：

```bash
pyinstaller --onefile --windowed --name="浏览器启动器" --clean browser_launcher.py
```

## 方法二：使用 auto-py-to-exe（图形界面工具）

### 1. 安装

```bash
pip install auto-py-to-exe
```

### 2. 运行

```bash
auto-py-to-exe
```

### 3. 配置

在图形界面中：
- Script Location: 选择 `browser_launcher.py`
- Onefile: 选择 "One File"
- Console Window: 选择 "Window Based (hide the console)"
- Icon: 可选，选择图标文件
- 点击 "CONVERT .PY TO .EXE" 按钮

## 方法三：使用 cx_Freeze

### 1. 安装

```bash
pip install cx_Freeze
```

### 2. 创建 setup.py

```python
from cx_Freeze import setup, Executable

setup(
    name="浏览器启动器",
    version="1.0",
    description="浏览器启动器应用程序",
    executables=[Executable("browser_launcher.py", base="Win32GUI")]
)
```

### 3. 打包

```bash
python setup.py build
```

## 快速打包脚本

我已经创建了 `build_exe.bat` 批处理文件，可以直接双击运行进行打包。

## 注意事项

1. **首次打包可能较慢**，需要下载依赖
2. **杀毒软件可能误报**，这是正常现象，可以添加信任
3. **文件大小**：打包后的 exe 文件大约 10-20MB（包含 Python 解释器）
4. **兼容性**：打包的 exe 只能在相同架构的 Windows 系统上运行（32位/64位）

## 测试

打包完成后，建议在干净的 Windows 系统上测试 exe 文件是否能正常运行。





