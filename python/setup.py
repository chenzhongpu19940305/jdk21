"""
使用 cx_Freeze 打包的配置文件（可选）
如果使用 PyInstaller，可以忽略此文件
"""

from cx_Freeze import setup, Executable
import sys

# 构建选项
build_exe_options = {
    "packages": ["tkinter"],
    "excludes": [],
    "include_files": []
}

# 可执行文件配置
executables = [
    Executable(
        "browser_launcher.py",
        base="Win32GUI" if sys.platform == "win32" else None,
        target_name="浏览器启动器.exe",
        icon=None
    )
]

setup(
    name="浏览器启动器",
    version="1.0.0",
    description="浏览器启动器 - 通过按钮快速打开网页",
    options={"build_exe": build_exe_options},
    executables=executables
)





