@echo off
REM 智慧自习室 - Windows 一键启动脚本
REM 同时启动后端和前端

setlocal enabledelayedexpansion

cd /d "%~dp0"
set "PROJECT_ROOT=%cd%"
set "BACKEND_DIR=%PROJECT_ROOT%"
set "FRONTEND_DIR=%PROJECT_ROOT%\vue_project"

echo.
echo ==========================================
echo     智慧自习室 - 启动脚本
echo ==========================================
echo.

REM 检查 Java
echo [检查] Java 环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] Java 未安装
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do set JAVA_VERSION=%%i
echo [成功] Java 已安装: %JAVA_VERSION%
echo.

REM 检查 npm
echo [检查] npm 环境...
npm -v >nul 2>&1
if errorlevel 1 (
    echo [错误] npm 未安装
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('npm -v') do set NPM_VERSION=%%i
echo [成功] npm 已安装: %NPM_VERSION%
echo.

REM 检查 MySQL
echo [检查] MySQL 连接...
mysql -u wisdom -pwisdom_study_room -e "SELECT 1;" >nul 2>&1
if errorlevel 1 (
    echo [错误] 无法连接 MySQL，请确保 MySQL 服务正在运行
    pause
    exit /b 1
)
echo [成功] MySQL 连接成功
echo.

REM 启动后端
echo [启动] 后端服务...
cd /d "%BACKEND_DIR%"

if not exist "target\demo-0.0.1-SNAPSHOT.jar" (
    echo [警告] JAR 文件不存在，正在构建...
    call mvn clean package -DskipTests -q
)

start "Wisdom-Backend" java -jar target\demo-0.0.1-SNAPSHOT.jar
echo [成功] 后端服务已启动
echo.

REM 等待后端启动
echo [等待] 后端服务初始化...
set RETRY_COUNT=0
:backend_wait
timeout /t 2 /nobreak >nul
set /a RETRY_COUNT+=1
if %RETRY_COUNT% gtr 30 (
    echo [错误] 后端服务启动超时
    pause
    exit /b 1
)
powershell -Command "try { [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; $null = Invoke-WebRequest -Uri http://localhost:8080/user/list?userAccount=admin -TimeoutSec 2 -ErrorAction Stop } catch { exit 1 }" >nul 2>&1
if errorlevel 1 goto backend_wait
echo [成功] 后端服务已就绪 (http://localhost:8080)
echo.

REM 启动前端
echo [启动] 前端服务...
cd /d "%FRONTEND_DIR%"

if not exist "node_modules" (
    echo [警告] npm 依赖不存在，正在安装...
    call npm install -q
)

start "Wisdom-Frontend" npm run serve
echo [成功] 前端服务已启动
echo.

REM 等待前端启动
echo [等待] 前端服务初始化...
set RETRY_COUNT=0
:frontend_wait
timeout /t 2 /nobreak >nul
set /a RETRY_COUNT+=1
if %RETRY_COUNT% gtr 30 (
    echo [错误] 前端服务启动超时
    pause
    exit /b 1
)
powershell -Command "try { [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; $null = Invoke-WebRequest -Uri http://localhost:8081 -TimeoutSec 2 -ErrorAction Stop } catch { exit 1 }" >nul 2>&1
if errorlevel 1 goto frontend_wait
echo [成功] 前端服务已就绪 (http://localhost:8081)
echo.

echo ==========================================
echo 所有服务已启动！
echo ==========================================
echo.
echo 访问地址:
echo   前端: http://localhost:8081
echo   后端: http://localhost:8080
echo.
echo 测试账号:
echo   管理员: admin / admin123
echo   用户: testuser / user123
echo.
echo 提示: 关闭这个窗口时，请按 Ctrl+C 停止服务，或直接关闭窗口
echo.
pause
