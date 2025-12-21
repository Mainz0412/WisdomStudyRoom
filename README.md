# 智慧自习室系统

## 项目简介
本项目为智慧自习室管理系统，包含后端（Java Spring Boot）与前端（Vue.js）两部分，旨在实现自习室座位预约、用户管理、信息展示等功能，提升自习室管理效率与用户体验。

## 主要功能
- 用户注册与登录
- 座位预约与管理
- 管理员后台管理（如用户、座位、预约记录等）
- 预约信息展示与统计
- 前端页面交互（Vue.js 实现）
- 后端接口服务（Spring Boot 实现）

## 实现过程简述
1. **需求分析与方案设计**：明确系统功能需求，设计数据库结构与系统架构。
2. **后端开发**：使用 Spring Boot 框架，完成用户、座位、预约等核心业务逻辑及接口开发。
3. **前端开发**：采用 Vue.js 实现用户界面，包括登录、注册、预约、管理等页面。
4. **数据库初始化**：通过 SQL 脚本初始化基础数据表。
5. **系统集成与测试**：前后端联调，功能测试与优化。

## 目录结构说明
```
SoftwareEngineering/
├── Wisdom-Study-Room-master/         # 项目主目录
│   ├── src/                          # 后端源码
│   ├── vue_project/                  # 前端源码
│   ├── init-database.sql             # 数据库初始化脚本
│   ├── Dockerfile                    # 容器部署配置
│   ├── pom.xml                       # Maven 配置
│   ├── README.md                     # 项目说明文件
│   └── ...
└── ...
```

## 启动与使用方法
### 后端启动
1. 安装 JDK 8/11 及 Maven
2. 在 `Wisdom-Study-Room-master` 目录下运行：
   ```powershell
   mvn clean package
   java -jar target/demo-0.0.1-SNAPSHOT.jar.original
   ```

### 前端启动
1. 安装 Node.js 和 npm
2. 进入 `vue_project` 目录，运行：
   ```powershell
   npm install
   npm run serve
   ```

### 数据库初始化
使用 `init-database.sql` 脚本初始化数据库。

## 其他说明
- 如需容器化部署，可参考 `Dockerfile`。
- 更多详细配置与使用说明见各目录下的 `README.md` 或相关文档。

---
如有问题或建议，欢迎反馈！
