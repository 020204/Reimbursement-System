# 报销管理系统 - 安装与使用手册

本文档为**安装配置手册**与**用户操作手册**，供部署人员与最终用户使用。

---

## 第一部分：安装与部署

### 1.1 环境要求

| 类别     | 要求                          |
|----------|-------------------------------|
| 后端     | JDK 1.8+（推荐 JDK 17/21）    |
| 数据库   | MariaDB 10.x+ 或 MySQL 8.0+   |
| 缓存     | Redis 3.0+                    |
| 构建     | Maven 3.6+                    |
| 前端     | Node.js 16+                   |
| 包管理   | npm / pnpm / yarn             |

### 1.2 安装 MariaDB

**Linux (apt)：**
```bash
sudo apt update
sudo apt install mariadb-server mariadb-client
sudo systemctl start mariadb
sudo mysql_secure_installation   # 按提示设置 root 密码，例如: zz2332zz
```

**验证：**
```bash
mysql -u root -p
# 输入密码后能进入 MySQL 即表示安装成功
```

### 1.3 安装 Redis

**Linux (apt)：**
```bash
sudo apt install redis-server
sudo systemctl start redis-server
# 如需设置密码，编辑 /etc/redis/redis.conf，找到 requirepass 并设置，例如:
# requirepass zz2332zz
# 然后重启: sudo systemctl restart redis-server
```

**验证：**
```bash
redis-cli
AUTH zz2332zz
PING
# 返回 PONG 表示 Redis 正常且密码正确
```

### 1.4 初始化数据库

1. 进入项目目录，执行初始化脚本（将 `zz2332zz` 替换为你的 MariaDB root 密码）：

```bash
cd /home/ckc/IdeaProjects/Reimbursement-System/reimbursement-system
mysql -u root -pzz2332zz < src/main/resources/init.sql
```

2. 脚本会自动完成：
   - 创建数据库 `reimbursement_db`
   - 创建 7 张表（员工、角色、用户角色、报销单、报销明细、审批记录、操作日志）
   - 插入角色与测试账号数据

3. 验证：
```bash
mysql -u root -pzz2332zz -e "USE reimbursement_db; SHOW TABLES;"
```

### 1.5 配置后端

编辑 `reimbursement-system/src/main/resources/application.yml`，确认以下配置与你的环境一致：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/reimbursement_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: zz2332zz    # 你的 MariaDB 密码

  redis:
    host: localhost
    port: 6379
    password: zz2332zz    # 你的 Redis 密码
```

若 MariaDB/Redis 不在本机或端口不同，请修改 `url`、`host`、`port`。

### 1.6 启动后端

```bash
cd /home/ckc/IdeaProjects/Reimbursement-System/reimbursement-system
mvn spring-boot:run
```

看到类似 `Started ReimbursementSystemApplication` 即表示启动成功。  
接口根地址：**http://localhost:8080/api**

### 1.7 安装并启动前端

```bash
cd /home/ckc/IdeaProjects/Reimbursement-System/reimbursement-frontend
npm install
npm run dev
```

浏览器访问：**http://localhost:3000**（或终端提示的地址）。

### 1.8 生产部署（可选）

- **后端**：`mvn clean package` 后运行 `java -jar target/reimbursement-system-1.0.0.jar`
- **前端**：`npm run build`，将 `dist` 目录部署到 Nginx 或其他 Web 服务器，并配置 API 代理到后端 `http://localhost:8080`

---

## 第二部分：用户操作手册

### 2.1 登录与角色说明

打开前端地址，在登录页输入用户名和密码。

| 用户名   | 密码   | 角色   | 可访问功能说明                     |
|----------|--------|--------|------------------------------------|
| admin    | 123456 | 管理员 | 所有功能（报销、审批、统计、员工管理） |
| finance  | 123456 | 财务   | 报销、审批中心、数据统计           |
| manager  | 123456 | 主管   | 报销、审批中心、数据统计           |
| employee | 123456 | 员工   | 报销管理、个人报销单、首页统计     |

登录成功后进入首页（仪表盘）。

### 2.2 首页（仪表盘）

- **统计卡片**：待审批、已通过、已驳回、总报销单数量。
- **最近报销单**：列表展示最近记录，可点击查看详情。
- **侧边栏菜单**：根据角色显示不同菜单（员工无「审批中心」「员工管理」）。

### 2.3 报销管理

#### 2.3.1 报销单列表

- 路径：**报销管理 → 报销单列表**。
- 可按**状态**（草稿/待审批/已通过/已驳回）、**类型**（差旅费/餐饮费/办公费等）筛选。
- 支持分页。
- 操作说明：
  - **查看**：查看报销单详情及明细。
  - **提交审批**：仅「草稿」状态可点击，提交后变为「待审批」。
  - **编辑**：仅草稿可编辑。
  - **删除**：仅草稿可删除（按系统实现可能仅本人可删）。

#### 2.3.2 创建报销单

- 路径：**报销管理 → 创建报销单**。
- 必填项：
  - **标题**：如「北京出差报销」。
  - **报销类型**：差旅费、餐饮费、办公费、其他等。
- **报销明细**：
  - 点击「添加明细」增加一行。
  - 每行：费用项目、金额、发生日期（按界面要求填写）。
  - 可删除某一行明细。
  - 系统会**自动汇总总金额**。
- 底部操作：
  - **保存草稿**：不提交，可稍后在「报销单列表」中继续编辑或提交。
  - **提交审批**：直接提交，状态变为「待审批」，等待主管/财务审批。

### 2.4 审批中心（主管/财务/管理员）

- 路径：**审批中心**。
- 仅主管、财务、管理员角色可见并可操作。

#### 2.4.1 待审批列表

- 表格列出所有「待审批」报销单。
- 可查看报销单详情（标题、类型、金额、明细、申请人等）。

#### 2.4.2 单个审批

1. 在待审批列表中点击某条的「查看」。
2. 确认信息后点击「通过」或「驳回」。
3. 在弹框中输入**审批意见**（可选但建议填写）。
4. 确认后，该报销单状态变为「已通过」或「已驳回」。

#### 2.4.3 批量审批

1. 在待审批列表中**勾选**多条报销单。
2. 点击「批量通过」或「批量驳回」。
3. 输入批量审批意见，确认。
4. 所有勾选记录将统一通过或驳回。

### 2.5 员工管理（仅管理员）

- 路径：**员工管理 → 员工列表**。
- 可查看所有员工信息（用户名、姓名、部门、职位等）。
- 支持按条件查询（如按部门、姓名等，以实际界面为准）。

### 2.6 登出

- 点击页面右上角用户信息或头像区域，选择「退出登录」或「登出」即可安全退出。

---

## 第三部分：常见问题

**Q：登录后白屏或菜单为空？**  
A：检查浏览器控制台是否有报错；确认后端已启动且前端请求的 API 地址正确（开发环境一般为代理到 `http://localhost:8080`）。

**Q：后端启动报数据库连接失败？**  
A：确认 MariaDB 已启动、`application.yml` 中数据库地址/用户名/密码正确，且已执行 `init.sql` 创建库和表。

**Q：后端报 Redis 连接错误？**  
A：确认 Redis 已启动，且 `application.yml` 中 Redis 的 host、port、password 与当前环境一致。

**Q：忘记测试账号密码？**  
A：默认均为 `123456`；若已修改数据库，请以管理员身份在数据库中查询或重置 `employee` 表中对应用户的 `password` 字段（系统使用 MD5 存储）。

**Q：如何修改端口？**  
A：后端在 `application.yml` 中修改 `server.port`；前端在 `vite.config.js` 的 `server.port` 中修改，并确认代理仍指向后端地址。

---

## 文档版本

- 版本：1.0  
- 适用项目：报销管理系统（Spring Boot + Vue 3 + Element Plus）  
- 最后更新：按实际修订日期填写  

如有疑问，请结合项目中的 `FULLSTACK_README.md` 及代码注释进行排查或二次开发。
