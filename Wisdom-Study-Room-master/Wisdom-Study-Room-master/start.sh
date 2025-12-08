#!/bin/bash

# 智慧自习室 - 一键启动脚本
# 同时启动后端和前端，并自动打开浏览器

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$PROJECT_ROOT"
FRONTEND_DIR="$PROJECT_ROOT/vue_project"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_status() {
    echo -e "${BLUE}[$(date '+%H:%M:%S')]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[$(date '+%H:%M:%S')] ✓ $1${NC}"
}

print_error() {
    echo -e "${RED}[$(date '+%H:%M:%S')] ✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}[$(date '+%H:%M:%S')] ⚠ $1${NC}"
}

# 清理函数
cleanup() {
    print_warning "正在停止应用..."
    
    # 杀死后端进程
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID 2>/dev/null || true
        print_status "已停止后端服务 (PID: $BACKEND_PID)"
    fi
    
    # 杀死前端进程
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID 2>/dev/null || true
        print_status "已停止前端服务 (PID: $FRONTEND_PID)"
    fi
    
    print_success "应用已安全关闭"
    exit 0
}

# 处理中断信号
trap cleanup SIGINT SIGTERM

print_status "=========================================="
print_status "    智慧自习室 - 启动脚本"
print_status "=========================================="

# 检查 Java
print_status "检查 Java 环境..."
if ! command -v java &> /dev/null; then
    print_error "Java 未安装"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
print_success "Java 已安装: $JAVA_VERSION"

# 检查 npm
print_status "检查 npm 环境..."
if ! command -v npm &> /dev/null; then
    print_error "npm 未安装"
    exit 1
fi
NPM_VERSION=$(npm -v)
print_success "npm 已安装: $NPM_VERSION"

# 检查 MySQL
print_status "检查 MySQL 连接..."
if ! mysql -u wisdom -pwisdom_study_room -e "SELECT 1;" &> /dev/null; then
    print_error "无法连接 MySQL，请确保 MySQL 服务正在运行"
    exit 1
fi
print_success "MySQL 连接成功"

# 启动后端
print_status "启动后端服务..."
cd "$BACKEND_DIR"

if [ ! -f "target/demo-0.0.1-SNAPSHOT.jar" ]; then
    print_warning "JAR 文件不存在，正在构建..."
    mvn clean package -DskipTests -q
fi

java -jar target/demo-0.0.1-SNAPSHOT.jar > backend.log 2>&1 &
BACKEND_PID=$!
print_success "后端服务已启动 (PID: $BACKEND_PID)"

# 等待后端启动
print_status "等待后端服务初始化..."
RETRY_COUNT=0
MAX_RETRIES=30
until curl -s http://localhost:8080/user/list?userAccount=admin > /dev/null 2>&1; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        print_error "后端服务启动超时，请检查日志:"
        tail -20 backend.log
        kill $BACKEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 1
done
print_success "后端服务已就绪 (http://localhost:8080)"

# 启动前端
print_status "启动前端服务..."
cd "$FRONTEND_DIR"

# 检查依赖
if [ ! -d "node_modules" ]; then
    print_warning "npm 依赖不存在，正在安装..."
    npm install -q
fi

npm run serve > frontend.log 2>&1 &
FRONTEND_PID=$!
print_success "前端服务已启动 (PID: $FRONTEND_PID)"

# 等待前端启动
print_status "等待前端服务初始化..."
RETRY_COUNT=0
MAX_RETRIES=30
until curl -s http://localhost:8081 > /dev/null 2>&1; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        print_error "前端服务启动超时，请检查日志:"
        tail -20 frontend.log
        kill $BACKEND_PID $FRONTEND_PID 2>/dev/null || true
        exit 1
    fi
    sleep 1
done
print_success "前端服务已就绪 (http://localhost:8081)"

print_status "=========================================="
print_success "所有服务已启动！"
print_status "=========================================="
echo ""
echo -e "${GREEN}访问地址:${NC}"
echo -e "  前端: ${BLUE}http://localhost:8081${NC}"
echo -e "  后端: ${BLUE}http://localhost:8080${NC}"
echo ""
echo -e "${GREEN}测试账号:${NC}"
echo "  管理员: admin / admin123"
echo "  用户: testuser / user123"
echo ""
echo -e "${YELLOW}按 Ctrl+C 停止服务${NC}"
echo ""

# 保持运行
wait $BACKEND_PID $FRONTEND_PID
