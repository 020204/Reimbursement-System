# 报销管理系统 - 快速启动指南

## 📦 项目内容

本压缩包包含完整的报销管理系统后端代码,包括:

- ✅ 完整的Spring Boot项目结构
- ✅ 数据库初始化脚本
- ✅ MyBatis XML映射文件
- ✅ Shiro权限控制
- ✅ Redis缓存配置
- ✅ Postman测试集合
- ✅ 详细的README文档

## 🚀 快速开始(5分钟上手)

### 第一步: 解压项目
```bash
tar -xzf reimbursement-system.tar.gz
cd reimbursement-system
```

### 第二步: 准备环境

**必需环境:**
- JDK 1.8或更高版本
- MySQL 8.0或更高版本
- Redis 3.0或更高版本
- Maven 3.6或更高版本

**检查环境:**
```bash
java -version
mysql --version
redis-cli --version
mvn -version
```

### 第三步: 初始化数据库

1. 启动MySQL服务
2. 执行初始化脚本:

```bash
# 方式1: 命令行执行
mysql -u root -p < src/main/resources/init.sql

# 方式2: 使用MySQL客户端
# 登录MySQL后执行: source /path/to/init.sql
```

**数据库初始化后会创建:**
- 数据库: reimbursement_db
- 5张表: employee, role, user_role, reimbursement_form, reimbursement_detail, approval_record等
- 测试数据: 4个测试用户 + 3条示例报销单

### 第四步: 配置数据库和Redis

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/reimbursement_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root          # 修改为你的MySQL用户名
    password: your_password # 修改为你的MySQL密码
  
  redis:
    host: localhost         # 修改为你的Redis地址
    port: 6379
    password:               # 如果有密码,填写Redis密码
```

### 第五步: 启动项目

```bash
# 方式1: 使用Maven直接运行
mvn spring-boot:run

# 方式2: 打包后运行
mvn clean package
java -jar target/reimbursement-system-1.0.0.jar
```

看到以下提示说明启动成功:
```
========================================
报销管理系统启动成功!
访问地址: http://localhost:8080/api
========================================
```

### 第六步: 测试接口

**使用Postman测试:**

1. 导入测试集合: `Postman_Collection.json`
2. 测试登录接口:

```
POST http://localhost:8080/api/employee/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**使用curl测试:**

```bash
# 登录
curl -X POST http://localhost:8080/api/employee/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 查询报销单列表(需要先登录获取Cookie)
curl http://localhost:8080/api/reimbursement/list
```

## 📋 测试账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 所有权限 |
| finance | 123456 | 财务 | 审批权限 |
| manager | 123456 | 主管 | 审批权限 |
| employee | 123456 | 员工 | 提交报销单 |

## 🔧 常见问题

### 1. 启动失败: 数据库连接错误

**原因:** MySQL配置不正确或服务未启动

**解决:**
- 检查MySQL服务是否启动: `systemctl status mysql` 或 `service mysql status`
- 检查application.yml中的数据库配置是否正确
- 确认数据库reimbursement_db已创建

### 2. 启动失败: Redis连接错误

**原因:** Redis服务未启动或配置不正确

**解决:**
- 检查Redis服务是否启动: `redis-cli ping` (返回PONG说明正常)
- 启动Redis: `redis-server`
- 检查application.yml中的Redis配置

### 3. 端口被占用

**原因:** 8080端口已被其他程序占用

**解决:**
- 修改application.yml中的端口号:
```yaml
server:
  port: 8081  # 改为其他端口
```

### 4. Maven依赖下载失败

**原因:** 网络问题或Maven配置问题

**解决:**
- 配置Maven国内镜像(阿里云):
```xml
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

## 📚 主要API接口

### 员工管理
- POST `/api/employee/login` - 登录
- GET `/api/employee/current` - 获取当前用户
- GET `/api/employee/list` - 查询所有员工

### 报销管理
- POST `/api/reimbursement` - 创建报销单
- GET `/api/reimbursement/page` - 分页查询
- PUT `/api/reimbursement/submit/{id}` - 提交报销单
- PUT `/api/reimbursement/approve` - 审批报销单
- PUT `/api/reimbursement/batch-approve` - 批量审批

详细API文档请查看 `README.md`

## 📖 项目文档

- **README.md** - 项目介绍和完整文档
- **PROJECT_STRUCTURE.md** - 项目结构详细说明
- **Postman_Collection.json** - Postman测试集合
- **init.sql** - 数据库初始化脚本

## 🎯 下一步

1. 阅读 `README.md` 了解完整功能
2. 查看 `PROJECT_STRUCTURE.md` 理解项目结构
3. 使用Postman测试所有接口
4. 根据需求进行二次开发

## 💡 开发建议

### 推荐的开发流程:
1. 熟悉现有代码结构
2. 了解数据库表设计
3. 测试现有功能
4. 根据需求添加新功能

### 代码规范:
- 遵循阿里巴巴Java开发手册
- 保持良好的代码注释
- 统一的异常处理
- 合理使用事务

### 性能优化:
- 使用Redis缓存热点数据
- 合理使用分页查询
- 优化SQL语句
- 添加数据库索引

## 🤝 技术支持

如有问题:
1. 查看README.md的详细文档
2. 检查日志文件定位问题
3. 参考项目中的示例代码

## 📞 联系方式

有问题欢迎反馈和交流!

---

**祝你使用愉快! 🎉**
