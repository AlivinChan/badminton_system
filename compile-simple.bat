@echo off
chcp 65001 >nul
echo 正在编译项目...
if not exist "out" mkdir out

REM 直接编译所有Java文件
javac -d out -encoding UTF-8 -sourcepath "src\main\java" ^
    "src\main\java\com\badminton\Main.java" ^
    "src\main\java\com\badminton\model\*.java" ^
    "src\main\java\com\badminton\service\*.java" ^
    "src\main\java\com\badminton\persistence\*.java" ^
    "src\main\java\com\badminton\util\*.java" ^
    "src\main\java\com\badminton\ui\console\*.java" ^
    "src\main\java\com\badminton\ui\swing\*.java"

if %errorlevel% == 0 (
    echo 编译成功！
) else (
    echo 编译失败！
    pause
    exit /b 1
)
pause

