# 报销管理系统

## 项目简介

这是一个基于Spring Boot + MyBatis + Redis + Shiro的报销管理系统,实现了员工报销单的提交、审批、查询等功能。

## 技术栈

- **后端框架**: Spring Boot 2.7.14
- **持久层框架**: MyBatis 2.2.2
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全框架**: Apache Shiro 1.11.0
- **连接池**: Druid 1.2.16
- **分页插件**: PageHelper 1.4.6

## 功能特性

### 1. 用户管理
- 用户登录/登出
- 用户信息查询
- 用户CRUD操作
- 基于角色的权限控制(RBAC)
- **Redis分布式Session** (F5刷新不丢失登录)

### 2. 报销管理
- 创建报销单(支持多明细)
- **更新报销单** (含明细同步更新)
- 提交报销单
- **分页查询** (支持分页、条件查询、**数据权限控制**)
- 修改报销单(仅草稿状态)
- 删除报销单(仅草稿状态)

### 3. 审批功能
- 单个审批
- 批量审批
- 审批记录查询
- 审批流程状态管理

### 4. 数据权限控制 ⭐
| 角色 | 数据权限 | 实现方式 |
|------|----------|----------|
| 普通员工 | 只看自己的报销 | `employee_id = 当前用户ID` |
| 部门主管 | 看本部门员工 | `employee_id IN (本部门员工ID列表)` |
| 财务/管理员 | 看全部 | 无过滤 |

### 5. 性能优化
- Redis缓存优化
- **Redis Session共享**
- 分页查询
- 批量操作接口
- 连接池优化

## 项目结构

```
reimbursement-system/
├── src/main/
│   ├── java/com/example/reimbursement/
│   │   ├── common/              # 公共类(Result、异常等)
│   │   ├── config/              # 配置类(Shiro、Redis等)
│   │   ├── controller/          # 控制器层
│   │   ├── entity/              # 实体类
│   │   ├── mapper/              # Mapper接口
│   │   ├── service/             # 服务层
│   │   └── ReimbursementSystemApplication.java  # 启动类
│   └── resources/
│       ├── mapper/              # MyBatis XML文件
│       ├── application.yml      # 配置文件
│       └── init.sql            # 数据库初始化脚本
└── pom.xml                     # Maven配置
```

## 快速开始

### 1. 环境要求
- JDK 1.8+
- MySQL 8.0+
- Redis 3.0+
- Maven 3.6+

### 2. 数据库配置

执行 `src/main/resources/init.sql` 初始化数据库:

```bash
mysql -u root -p < src/main/resources/init.sql
```

### 3. 修改配置

修改 `src/main/resources/application.yml` 中的数据库和Redis配置:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/reimbursement_db
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### 4. 启动项目

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/reimbursement-system-1.0.0.jar
```

访问地址: http://localhost:8080/api

## API接口文档

### 员工管理

#### 1. 用户登录
```
POST /api/employee/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

#### 2. 用户登出
```
POST /api/employee/logout
```

#### 3. 获取当前用户信息
```
GET /api/employee/current
```

#### 4. 查询所有员工
```
GET /api/employee/list
```

### 报销管理

#### 1. 创建报销单
```
POST /api/reimbursement
Content-Type: application/json

{
  "employeeId": 1,
  "title": "差旅报销",
  "type": "TRAVEL",
  "description": "北京出差",
  "details": [
    {
      "itemName": "高铁票",
      "amount": 500.00,
      "occurrenceDate": "2024-01-10",
      "description": "北京往返"
    }
  ]
}
```

#### 2. 更新报销单
```
PUT /api/reimbursement
Content-Type: application/json

{
  "id": 1,
  "employeeId": 1,
  "title": "差旅报销(修改)",
  "type": "TRAVEL",
  "description": "北京出差",
  "details": [
    {
      "itemName": "高铁票",
      "amount": 600.00,
      "occurrenceDate": "2024-01-10",
      "description": "北京往返(修改后)"
    }
  ]
}
```

#### 3. 分页查询报销单
```
GET /api/reimbursement/page?pageNum=1&pageSize=10&status=PENDING
```

**权限说明:** 根据当前用户角色自动过滤数据范围
- 普通员工: 只能看到自己的报销单
- 部门主管: 能看到本部门所有员工的报销单  
- 财务/管理员: 能看到全部报销单

#### 4. 提交报销单
```
PUT /api/reimbursement/submit/1
```

#### 5. 审批报销单
```
PUT /api/reimbursement/approve
Content-Type: application/json

{
  "id": 1,
  "approverId": 2,
  "result": "APPROVED",
  "comment": "同意"
}
```

#### 6. 批量审批
```
PUT /api/reimbursement/batch-approve
Content-Type: application/json

{
  "ids": [1, 2, 3],
  "approverId": 2,
  "result": "APPROVED",
  "comment": "批量通过"
}
```

## 数据字典

### 报销类型(type)
- TRAVEL: 差旅费
- OFFICE: 办公费
- COMMUNICATION: 通讯费
- ENTERTAINMENT: 招待费
- OTHER: 其他

### 报销状态(status)
- DRAFT: 草稿
- PENDING: 待审批
- APPROVED: 已通过
- REJECTED: 已驳回
- CANCELLED: 已取消

### 审批结果(result)
- APPROVED: 通过
- REJECTED: 驳回

### 角色编码(role code)
- ADMIN: 管理员
- FINANCE: 财务
- MANAGER: 部门主管
- EMPLOYEE: 普通员工

## 测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | 123456 | ADMIN | 系统管理员 |
| finance | 123456 | FINANCE | 财务人员 |
| manager | 123456 | MANAGER | 部门主管 |
| employee | 123456 | EMPLOYEE | 普通员工 |

## 使用Postman测试

### 1. 导入环境变量
- 设置变量 `base_url`: http://localhost:8080/api

### 2. 登录获取Session
```
POST {{base_url}}/employee/login
{
  "username": "admin",
  "password": "123456"
}
```

### 3. 后续请求会自动携带Cookie进行认证

## 项目亮点

1. **完整的权限控制**: 使用Shiro实现基于角色的权限控制(RBAC)
2. **数据权限控制**: 不同角色查看不同数据范围，保护数据安全
3. **Redis分布式Session**: 使用Redis存储Session，刷新页面不丢失登录状态
4. **缓存优化**: 使用Redis缓存热点数据,提升查询性能
5. **分页查询**: 集成PageHelper实现高效分页
6. **批量操作**: 支持批量审批等批量操作
7. **事务管理**: 使用Spring声明式事务保证数据一致性
8. **异常处理**: 统一异常处理和响应格式
9. **代码规范**: 清晰的分层架构,良好的代码注释

## 后续优化建议

1. 增加文件上传功能(报销凭证)
2. 实现多级审批流程
3. 添加消息通知(邮件/短信)
4. 增加数据统计和报表功能
5. 集成工作流引擎(Activiti/Flowable)
6. 添加操作日志审计
7. 实现前后端分离(Vue/React前端)

## 许可证

MIT License

## 联系方式

如有问题,欢迎提Issue或Pull Request。
