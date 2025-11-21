#!/bin/bash
echo "正在编译项目..."
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -d out -encoding UTF-8 -sourcepath src/main/java @sources.txt
if [ $? -eq 0 ]; then
    echo "编译成功！"
    rm -f sources.txt
else
    echo "编译失败！"
    rm -f sources.txt
    exit 1
fi

