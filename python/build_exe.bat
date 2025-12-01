@echo off
chcp 65001 >nul
echo ========================================
echo 浏览器启动器 - 打包为 EXE
echo ========================================
echo.

echo [1/3] 检查 PyInstaller...
pip show pyinstaller >nul 2>&1
if errorlevel 1 (
    echo PyInstaller 未安装，正在安装...
    pip install pyinstaller
    if errorlevel 1 (
        echo 安装失败，请检查网络连接或手动安装: pip install pyinstaller
        pause
        exit /b 1
    )
    echo PyInstaller 安装成功！
) else (
    echo PyInstaller 已安装
)
echo.

echo [2/3] 清理旧的打包文件...
if exist dist rmdir /s /q dist
if exist build rmdir /s /q build
if exist *.spec del /q *.spec
echo 清理完成
echo.

echo [3/3] 开始打包...
pyinstaller --onefile --windowed --name="浏览器启动器" --clean browser_launcher.py

if errorlevel 1 (
    echo.
    echo 打包失败！
    pause
    exit /b 1
)

echo.
echo ========================================
echo 打包完成！
echo ========================================
echo.
echo 可执行文件位置: dist\浏览器启动器.exe
echo.
echo 按任意键打开 dist 文件夹...
pause >nul
explorer dist









