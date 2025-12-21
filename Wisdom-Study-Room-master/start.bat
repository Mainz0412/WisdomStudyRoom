@echo off
REM 智慧自习室 - Windows 一键启动脚本
REM 说明: 使用 CMD 兼容的检测和更稳健的 `start` 调用，加入 `where` 检查

setlocal enabledelayedexpansion

cd /d "%~dp0"
set "PROJECT_ROOT=%cd%"
set "BACKEND_DIR=%PROJECT_ROOT%"
set "FRONTEND_DIR=%PROJECT_ROOT%\vue_project"

echo.
echo ==========================================
echo     智慧自习室 - 启动脚本
echo =========================================
echo.

REM 检查 Java
echo [检查] Java 环境...
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [错误] Java 未找到，请将 Java 添加到 PATH
    pause
    exit /b 1
)
for /f "delims=" %%i in ('java -version 2^>^&1 ^| findstr /R "version"') do set "JAVA_VERSION=%%i"
echo [成功] Java 已安装: %JAVA_VERSION%
echo.

REM 检查 npm
echo [检查] npm 环境...
where npm >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [错误] npm 未找到，请安装 Node.js 并确保 npm 在 PATH 中
    pause
    exit /b 1
)
for /f "delims=" %%i in ('npm -v 2^>^&1') do set "NPM_VERSION=%%i"
echo [成功] npm 已安装: %NPM_VERSION%
echo.

REM 检查 mvn 和 mysql 可用性（可选提示）
where mvn >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [提示] Maven 未检测到，构建 JAR 时可能失败
)
where mysql >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [提示] MySQL 客户端未检测到，无法验证数据库连接
)
echo.

REM 检查 MySQL 连接（如果 mysql 可用则尝试连接）
where mysql >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo [检查] MySQL 连接...
    mysql -u wisdom -pwisdom_study_room -e "SELECT 1;" >nul 2>&1
    if %ERRORLEVEL% neq 0 (
        echo [警告] 无法使用提供的凭据连接 MySQL，请确认服务和凭据
    ) else (
        echo [成功] MySQL 连接成功
    )
    echo.
)

REM 启动后端
echo [启动] 后端服务...
cd /d "%BACKEND_DIR%"

if not exist "target\demo-0.0.1-SNAPSHOT.jar" (
    echo [警告] JAR 文件不存在，尝试构建（如果未安装 Maven 会跳过）...
    where mvn >nul 2>&1
    if %ERRORLEVEL% equ 0 (
        call mvn clean package -DskipTests -q
    ) else (
        echo [错误] Maven 未找到，无法构建 JAR，请先运行 `mvn package` 或安装 Maven
        echo.
    )
)

if exist "target\demo-0.0.1-SNAPSHOT.jar" (
    start "Wisdom-Backend" cmd /c "java -jar ""%~dp0target\demo-0.0.1-SNAPSHOT.jar"""
    echo [成功] 后端服务已启动
) else (
    echo [错误] 未找到 JAR，后端未启动
    echo.
)
echo.

REM 等待后端启动
echo [等待] 后端服务初始化...
set RETRY_COUNT=0
:backend_wait
timeout /t 2 /nobreak >nul
set /a RETRY_COUNT+=1
if %RETRY_COUNT% gtr 60 (
    echo [错误] 后端服务启动超时
    pause
    exit /b 1
)
REM 更健壮的检测：尝试 localhost 和 127.0.0.1，接受任意 2xx/3xx 响应
powershell -Command "try { [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; $urls=@('http://localhost:8080/user/list?userAccount=admin','http://127.0.0.1:8080/user/list?userAccount=admin'); $ok=$false; foreach($u in $urls){ $r=Invoke-WebRequest -Uri $u -TimeoutSec 3 -ErrorAction SilentlyContinue; if($r -and $r.StatusCode -ge 200 -and $r.StatusCode -lt 400){ $ok=$true; break } }; if(-not $ok){ exit 1 } } catch { exit 1 }" >nul 2>&1
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

start "Wisdom-Frontend" cmd /c "npm run serve"
echo [成功] 前端服务已启动
echo.

REM 等待前端启动
echo [等待] 前端服务初始化...
set RETRY_COUNT=0
:frontend_wait
timeout /t 2 /nobreak >nul
set /a RETRY_COUNT+=1
if %RETRY_COUNT% gtr 120 (
    echo [错误] 前端服务启动超时
    pause
    exit /b 1
)
REM 更健壮的检测：尝试访问多个常见路径并接受 2xx/3xx 响应
powershell -Command "try { [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12; $urls=@('http://localhost:8081','http://127.0.0.1:8081','http://localhost:8081/index.html','http://127.0.0.1:8081/index.html'); $ok=$false; foreach($u in $urls){ $r=Invoke-WebRequest -Uri $u -TimeoutSec 3 -ErrorAction SilentlyContinue; if($r -and $r.StatusCode -ge 200 -and $r.StatusCode -lt 400){ $ok=$true; break } }; if(-not $ok){ exit 1 } } catch { exit 1 }" >nul 2>&1
if errorlevel 1 goto frontend_wait
echo [成功] 前端服务已就绪 (http://localhost:8081)
echo.

echo ==========================================
echo 所有服务已启动（或已尝试启动）。
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
