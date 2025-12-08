# 🚀 快速参考指南

## 💻 系统要求

- Java 21+
- Node.js 20+
- npm 11+
- Maven 3.9+
- MySQL 8.0+

---

## 📌 关键路径

```
Wisdom-Study-Room-master/
├── src/main/java/          # Java 源代码
├── vue_project/            # Vue 前端
├── pom.xml                 # Maven 配置
├── target/                 # 构建输出
├── start-all.sh            # ⭐ 一键启动脚本
└── DEPENDENCY_UPDATE_SUMMARY.md  # 详细升级说明
```

---

## 🎯 日常操作

### 启动应用（推荐）
```bash
cd Wisdom-Study-Room-master
./start-all.sh
```

**自动完成：**
- ✅ 检查后端 JAR 文件
- ✅ 检查前端依赖
- ✅ 启动后端服务 (8080)
- ✅ 启动前端服务 (8081)
- ✅ 显示进程 PID 和日志位置

### 手动启动

**后端：**
```bash
cd Wisdom-Study-Room-master
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

**前端：**
```bash
cd Wisdom-Study-Room-master/vue_project
npm run serve
```

### 构建应用

**后端：**
```bash
cd Wisdom-Study-Room-master
mvn clean package -DskipTests
```

**前端：**
```bash
cd Wisdom-Study-Room-master/vue_project
npm run build
```

---

## 🔍 常见操作

### 检查日志
```bash
# 后端日志
tail -f /tmp/backend.log

# 前端日志
tail -f /tmp/frontend.log
```

### 杀死进程
```bash
# 杀死所有 Java 进程
pkill -f "java -jar"

# 杀死前端进程
pkill -f "npm run serve"
```

### 重建依赖
```bash
# 前端
cd vue_project
rm -rf node_modules package-lock.json
npm install

# 后端
mvn clean install
```

---

## 🌐 访问地址

| 服务 | 地址 | 说明 |
|-----|------|------|
| 前端应用 | http://localhost:8081 | Vue 应用 |
| 后端 API | http://localhost:8080 | Spring Boot API |
| API 文档 | http://localhost:8080/swagger-ui.html | 接口文档（如已配置） |

---

## 👤 测试账户

| 角色 | 账户 | 密码 |
|-----|------|------|
| 管理员 | admin | admin |
| 普通用户 | testuser | testuser |

---

## 📊 版本信息

```
前端框架: Vue 3.4.31
UI 组件库: Element Plus 2.12.0
HTTP 客户端: axios 1.7.7

后端框架: Spring Boot 3.1.5
ORM 框架: MyBatis Plus 3.5.7
数据库驱动: MySQL Connector 8.0.33
工具库: Hutool 5.8.41

Java 版本: 21.0.1
Node.js: 25.2.1
```

---

## 🔧 故障排除

### 后端无法启动
```bash
# 检查日志
tail -50 /tmp/backend.log

# 检查端口占用
lsof -i :8080

# 清理并重新编译
mvn clean package -DskipTests
```

### 前端编译失败
```bash
# 清理依赖
cd vue_project
rm -rf node_modules package-lock.json

# 重新安装
npm install

# 检查 Node 版本
node --version
npm --version
```

### 前端无法连接后端
```bash
# 确认后端运行
curl http://localhost:8080/user/list

# 检查代理配置
cat vue_project/vue.config.js | grep -A 5 devServer
```

---

## 📚 相关文件

| 文件 | 描述 |
|-----|------|
| **DEPENDENCY_UPDATE_SUMMARY.md** | 完整的升级历史和技术决策 |
| **README_UPGRADE.md** | 升级总结和开始指南 |
| **UPGRADE_CHECKLIST.md** | 升级验证清单 |
| **pom.xml** | Maven 依赖配置 |
| **vue_project/package.json** | npm 依赖配置 |
| **src/main/java/com/huawei/DemoApplication.java** | 应用启动类 |
| **vue_project/src/main.js** | Vue 启动文件 |

---

## 💡 最佳实践

1. **定期更新**
   - 每周运行 `npm audit`
   - 每月检查 Maven 依赖

2. **备份配置**
   - 提交 pom.xml 和 package-lock.json 到版本控制
   - 保存 application.properties 配置

3. **监控日志**
   - 启动时检查 startup 日志
   - 生产环境定期检查错误日志

4. **测试流程**
   - 修改代码后重新编译
   - 本地测试后再提交
   - 使用测试账户验证功能

---

## 🆘 获取帮助

**检查清单：**
1. ☑️ Java/Node 版本正确
2. ☑️ 后端 JAR 文件存在
3. ☑️ 前端 node_modules 已安装
4. ☑️ MySQL 数据库运行中
5. ☑️ 无端口占用冲突

**联系方式：**
- 查看项目日志
- 检查 README 文件
- 查阅 Git 提交历史

---

## ✨ 快速命令速查

```bash
# 一键启动全部
./start-all.sh

# 编译后端
mvn clean package -DskipTests

# 编译前端
npm run build

# 启动后端
java -jar target/demo-0.0.1-SNAPSHOT.jar

# 启动前端
cd vue_project && npm run serve

# 查看进程
ps aux | grep java
ps aux | grep node

# 查看日志
tail -f /tmp/backend.log
tail -f /tmp/frontend.log

# 杀死进程
pkill -f "java -jar"
pkill -f "npm run serve"

# 测试 API
curl http://localhost:8080/user/list

# 测试前端
curl http://localhost:8081/
```

---

**最后更新：** 2025-12-08
**维护者：** 开发团队
**版本：** 1.0
