# 1Panel 服务器部署指南

本指南详细介绍了如何使用 **1Panel** 面板部署本报销管理系统。项目已预配置 `docker-compose.yml`，在 1Panel 中部署非常简单。

## 📋 前置要求

1. 服务器已安装 **1Panel** (开源 Linux 面板)。
2. 1Panel 中已安装 **Docker** 和 **Docker Compose**。
3. 服务器已开放必要的端口：
   - `80` (前端访问)
   - `8080` (后端 API 访问/调试，可选)
   - `3306` (数据库调试，可选)
   - `6379` (Redis 调试，可选)

## 🚀 部署步骤

### 第一步：上传项目文件

将项目完整上传到服务器。建议路径：`/opt/1panel/apps/reimbursement-system`

**必需包含的文件/文件夹：**
- `docker-compose.yml` - 主编排文件
- `.env` - 环境变量配置文件（重要！）
- `reimbursement-system/` (包含后端代码、`Dockerfile` 和 `src/main/resources/init.sql`)
- `reimbursement-frontend/` (包含前端代码、`Dockerfile` 和 `nginx.conf`)

### 第二步：配置环境变量

在项目根目录创建或修改 `.env` 文件：

```bash
# MySQL Configuration
MYSQL_ROOT_PASSWORD=root123456
MYSQL_DATABASE=reimbursement_db
MYSQL_PORT=3306

# Redis Configuration
REDIS_PASSWORD=redis123456
REDIS_PORT=6379

# Backend Configuration
SPRING_PROFILES_ACTIVE=docker
BACKEND_PORT=8080

# Frontend Configuration
FRONTEND_PORT=80
```

**⚠️ 安全提示：生产环境请修改默认密码！**

### 第三步：在 1Panel 中编排部署

1. 登录 1Panel 面板。
2. 进入 **[容器]** -> **[编排]**。
3. 点击 **[创建编排]**。
4. **基本信息**：
   - 名称：`reimbursement-system`
   - 路径：选择您刚才上传文件的目录 (例如 `/opt/1panel/apps/reimbursement-system`)。
5. **配置内容**：
   - 选择 **[从本地文件读取]**，选择该目录下的 `docker-compose.yml`。
6. 点击 **[确定]**，1Panel 将自动开始拉取镜像、构建并启动容器。

### 第四步：验证部署

1. 在 **[容器]** 列表中确认所有容器均处于 `运行中`。
2. **数据库初始化**：首次启动时，MySQL 容器会自动执行 `init.sql`。您可以通过 1Panel 的 **[数据库]** -> **[MySQL]** -> **[管理]** 检查 `reimbursement_db` 及其表是否已创建。
3. **访问前端**：在浏览器访问服务器 IP 的 `80` 端口。

## 🔧 配置说明

### 服务架构

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| mysql | mysql:8.0 | 3306 | MySQL 数据库，带健康检查 |
| redis | redis:7-alpine | 6379 | Redis 缓存，带密码认证 |
| backend | 自建 (Spring Boot) | 8080 | 后端 API 服务 |
| frontend | 自建 (Vue 3 + Nginx) | 80 | 前端静态服务 |

### 健康检查

所有服务都配置了健康检查：
- **MySQL**: `mysqladmin ping` 检测
- **Redis**: `redis-cli ping` 检测  
- **Backend**: `/api/actuator/health` 端点检测

### 启动顺序

服务启动顺序（自动等待依赖服务健康）：
1. MySQL、Redis 同时启动
2. 等待 MySQL 和 Redis 健康后启动 Backend
3. 等待 Backend 健康后启动 Frontend

## 🛠️ 运维与常见问题

### 1. 修改配置

**修改密码：**
编辑 `.env` 文件，修改对应的密码配置，然后在 1Panel 编排页面点击 **[更新]**。

**修改端口：**
编辑 `.env` 文件中的端口配置，更新编排后重启容器。

### 2. 查看日志

在 1Panel **[容器]** 界面，点击对应容器的 **[日志]** 按钮，可以查看：
- MySQL 启动和查询日志
- Redis 连接日志
- Spring Boot 应用日志
- Nginx 访问和错误日志

### 3. 数据备份

**MySQL 数据备份：**
```bash
docker exec reimbursement-mysql mysqldump -u root -p${MYSQL_ROOT_PASSWORD} reimbursement_db > backup.sql
```

**Redis 数据备份：**
Redis 默认开启 AOF 持久化，数据保存在 `redis-data` 卷中。

### 4. 重建镜像

如果更新了代码，需要重新构建镜像：
```bash
docker-compose build --no-cache
docker-compose up -d
```

### 5. 常见问题

**Q: 后端启动失败，数据库连接错误？**
A: 检查 MySQL 容器是否健康，`.env` 文件中的密码是否正确。

**Q: Redis 连接被拒绝？**
A: 确认 `.env` 中的 `REDIS_PASSWORD` 配置正确，Redis 现在需要密码认证。

**Q: 前端无法访问后端 API？**
A: 检查 backend 容器是否健康，nginx.conf 中的代理配置是否正确。

## 🌐 设置域名 (可选)

如果您希望使用域名访问：

1. 在 1Panel **[网站]** 中创建一个反向代理网站。
2. 代理地址填入：`http://127.0.0.1:80` (前端)。
3. 配置 SSL 证书实现 HTTPS 访问。

## 🔐 安全建议

1. **修改默认密码**：生产环境务必修改 `.env` 中的所有密码
2. **关闭调试端口**：如不需要外部访问，移除 MySQL 和 Redis 的端口映射
3. **配置防火墙**：仅开放必要端口
4. **定期备份**：定期备份 MySQL 数据卷

---

**祝您部署顺利！如有疑问，请参考项目主 README 文档。**
