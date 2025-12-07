@echo off
chcp 65001 >nul
echo ========================================
echo 构建 IntelliJ IDEA 插件
echo ========================================
echo.

REM 检查是否在正确的目录
if not exist "build.gradle.kts" (
    echo 错误：未找到 build.gradle.kts 文件
    echo 请确保在插件项目根目录下运行此脚本
    pause
    exit /b 1
)

echo [1/3] 清理之前的构建...
call gradlew.bat clean
if %ERRORLEVEL% neq 0 (
    echo 清理失败！
    pause
    exit /b 1
)

echo.
echo [2/3] 构建插件...
call gradlew.bat buildPlugin
if %ERRORLEVEL% neq 0 (
    echo 构建失败！
    pause
    exit /b 1
)

echo.
echo [3/3] 检查构建结果...
if exist "build\distributions\*.zip" (
    echo.
    echo ========================================
    echo 构建成功！
    echo ========================================
    echo.
    echo 插件文件位置：
    dir /b build\distributions\*.zip
    echo.
    echo 您可以在 IntelliJ IDEA 中通过以下方式安装：
    echo 1. File -^> Settings -^> Plugins
    echo 2. 点击齿轮图标 -^> Install Plugin from Disk...
    echo 3. 选择 build\distributions\ 目录下的 .zip 文件
    echo.
) else (
    echo 错误：未找到插件文件！
    pause
    exit /b 1
)

echo 按任意键打开构建目录...
pause >nul
if exist "build\distributions" (
    explorer build\distributions
)










