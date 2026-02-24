# 报销管理系统测试 - 研究发现

## 项目架构分析

### 后端架构 (Spring Boot)

#### 技术栈
- **框架**: Spring Boot 2.7.14
- **JDK**: Java 1.8
- **数据库**: MySQL 8.0 + Druid 连接池
- **ORM**: MyBatis 2.2.2 + PageHelper 分页
- **安全**: Apache Shiro 1.11.0 + Redis Session
- **缓存**: Spring Data Redis
- **工具**: Lombok, FastJSON, Commons Lang3

#### 目录结构
```
reimbursement-system/src/main/java/com/example/reimbursement/
├── common/           # 通用类
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   ├── MD5Util.java
│   ├── PageResult.java
│   └── Result.java
├── config/           # 配置类
│   ├── shiro/        # Shiro 配置
│   ├── CorsConfig.java
│   ├── RedisConfig.java
│   └── ShiroConfig.java
├── controller/       # 控制器层
│   ├── DepartmentController.java
│   ├── EmployeeController.java
│   └── ReimbursementFormController.java
├── entity/           # 实体类
│   ├── ApprovalRecord.java
│   ├── Department.java
│   ├── Employee.java
│   ├── ReimbursementDetail.java
│   ├── ReimbursementForm.java
│   └── Role.java
├── mapper/           # MyBatis Mapper
│   ├── ApprovalRecordMapper.java
│   ├── DepartmentMapper.java
│   ├── EmployeeMapper.java
│   ├── ReimbursementDetailMapper.java
│   └── ReimbursementFormMapper.java
└── service/          # 服务层
    ├── EmployeeService.java
    └── ReimbursementFormService.java
```

#### 实体类关系
- **Employee**: 员工信息，关联 Department
- **Department**: 部门信息
- **ReimbursementForm**: 报销单，关联 Employee
- **ReimbursementDetail**: 报销明细，关联 ReimbursementForm
- **ApprovalRecord**: 审批记录，关联 ReimbursementForm 和 Employee
- **Role**: 角色信息

### 前端架构 (Vue 3)

#### 技术栈
- **框架**: Vue 3.4.15
- **构建工具**: Vite 5.0.11
- **UI 组件库**: Element Plus 2.5.4
- **状态管理**: Pinia 2.1.7 + persistedstate
- **路由**: Vue Router 4.2.5
- **HTTP 客户端**: Axios 1.6.5
- **日期处理**: Day.js 1.11.10

#### 目录结构
```
reimbursement-frontend/src/
├── api/              # API 接口
│   ├── department.js
│   ├── employee.js
│   └── reimbursement.js
├── router/           # 路由配置
│   └── index.js
├── stores/           # Pinia Store
│   └── user.js
├── utils/            # 工具函数
│   └── request.js    # Axios 封装
├── views/            # 页面组件
│   ├── approval/     # 审批管理
│   ├── dashboard/    # 仪表盘
│   ├── department/   # 部门管理
│   ├── employee/     # 员工管理
│   ├── layout/       # 布局组件
│   ├── login/        # 登录页面
│   ├── profile/      # 个人中心
│   └── reimbursement/# 报销管理
├── App.vue
└── main.js
```

### 部署架构

#### Docker Compose 服务
1. **MySQL**: 数据库服务
   - 端口: 3306
   - 数据卷: mysql-data
   - 初始化脚本: init.sql

2. **Redis**: 缓存服务
   - 端口: 6379
   - 数据卷: redis-data
   - 持久化: AOF

3. **Backend**: 后端服务
   - 端口: 8080
   - 依赖: MySQL, Redis
   - 健康检查: /api/actuator/health

4. **Frontend**: 前端服务
   - 端口: 80
   - 依赖: Backend

---

## 测试重点发现

### 需要重点测试的模块

1. **认证授权模块 (Shiro)**
   - 登录/登出功能
   - Session 管理
   - 权限控制
   - 密码加密 (MD5)

2. **核心业务模块**
   - 报销单 CRUD
   - 审批流程
   - 报销明细计算
   - 状态流转

3. **数据访问层**
   - MyBatis Mapper 正确性
   - SQL 查询性能
   - 分页功能

4. **前端交互**
   - 表单验证
   - 路由守卫
   - API 错误处理
   - 状态持久化

### 潜在风险点

1. **Shiro Redis Session**: 需要验证 Session 序列化和过期处理
2. **报销金额计算**: 需要验证精度处理
3. **并发审批**: 需要验证状态一致性
4. **跨域配置**: 需要验证 CORS 设置

---

## 测试数据准备

### 数据库初始数据 (init.sql)
需要分析 init.sql 了解初始数据结构，用于编写测试用例。

### 测试用户
- 管理员账号
- 普通员工账号
- 部门经理账号

---

## 待进一步调查

1. [ ] 详细分析 init.sql 中的初始数据
2. [ ] 查看 Controller 层的 API 接口定义
3. [ ] 了解报销单状态流转规则
4. [ ] 确认审批流程的业务逻辑

---

## 更新记录

| 日期 | 内容 |
|------|------|
| 2026-02-20 | 初始项目架构分析 |
