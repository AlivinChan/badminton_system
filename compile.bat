@echo off
chcp 65001 >nul
echo 正在编译项目...
if not exist "out" mkdir out

REM 创建临时文件列表
echo 正在收集Java源文件...
dir /s /b src\main\java\*.java > sources.tmp

REM 编译所有Java文件
javac -d out -encoding UTF-8 -sourcepath "src\main\java" @sources.tmp

if %errorlevel% == 0 (
    echo 编译成功！
    del sources.tmp >nul 2>&1
) else (
    echo 编译失败！
    del sources.tmp >nul 2>&1
    pause
    exit /b 1
)
pause

