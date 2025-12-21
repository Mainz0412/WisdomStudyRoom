#!/bin/bash

# 智慧自习室 - 完整启动脚本
# 同时启动后端和前端服务

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_JAR="$PROJECT_DIR/target/demo-0.0.1-SNAPSHOT.jar"
FRONTEND_DIR="$PROJECT_DIR/vue_project"
LOG_DIR="$PROJECT_DIR/logs"

# Ensure log directory exists
mkdir -p "$LOG_DIR"

echo "🚀 智慧自习室 - 启动脚本"
echo "================================"

# 检查后端 JAR 文件
if [ ! -f "$BACKEND_JAR" ]; then
    echo "❌ 后端 JAR 文件不存在，正在编译..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "❌ 后端编译失败"
        exit 1
    fi
fi

# 检查前端依赖
if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
    echo "📦 安装前端依赖..."
    cd "$FRONTEND_DIR"
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 前端依赖安装失败"
        exit 1
    fi
fi

# 启动后端
echo ""
echo "🔧 启动后端服务 (http://localhost:8080)..."
cd "$PROJECT_DIR"
java -jar "$BACKEND_JAR" > "$LOG_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
echo "   后端进程 PID: $BACKEND_PID"
echo "   日志文件: $LOG_DIR/backend.log"

# 等待后端启动
echo "   等待后端启动..."
sleep 5

# 检查后端是否启动成功
if ! ps -p $BACKEND_PID > /dev/null; then
    echo "❌ 后端启动失败，查看日志："
    tail -20 "$LOG_DIR/backend.log"
    exit 1
fi

# 启动前端
echo ""
echo "🎨 启动前端服务 (http://localhost:8081)..."
cd "$FRONTEND_DIR"
npm run serve > "$LOG_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "   前端进程 PID: $FRONTEND_PID"
echo "   日志文件: $LOG_DIR/frontend.log"

# 等待前端启动
echo "   等待前端启动..."
sleep 5

# 显示启动完成信息
echo ""
echo "================================"
echo "✅ 启动完成！"
echo ""
echo "📌 服务地址："
echo "   后端 API: http://localhost:8080"
echo "   前端应用: http://localhost:8081"
echo ""
echo "👤 测试账户："
echo "   管理员: admin / admin"
echo "   普通用户: testuser / testuser"
echo ""
echo "🔍 查看日志："
echo "   后端: tail -f $LOG_DIR/backend.log"
echo "   前端: tail -f $LOG_DIR/frontend.log"
echo ""
echo "🛑 停止服务："
echo "   kill $BACKEND_PID  (后端)"
echo "   kill $FRONTEND_PID (前端)"
echo ""
echo "================================"

# 保持脚本运行，显示日志
echo ""
echo "显示实时日志 (Ctrl+C 退出)..."
echo ""
wait
