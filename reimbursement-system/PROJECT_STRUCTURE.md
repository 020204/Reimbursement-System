# 项目结构说明

## 目录结构

```
reimbursement-system/
│
├── src/main/
│   ├── java/com/example/reimbursement/
│   │   │
│   │   ├── common/                          # 公共模块
│   │   │   ├── BusinessException.java       # 业务异常类
│   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理器
│   │   │   ├── MD5Util.java                 # MD5加密工具
│   │   │   ├── PageResult.java              # 分页结果封装
│   │   │   └── Result.java                  # 统一响应结果封装
│   │   │
│   │   ├── config/                          # 配置模块
│   │   │   ├── shiro/                       # Shiro相关配置
│   │   │   │   └── CustomRealm.java         # 自定义Realm(认证授权)
│   │   │   ├── CorsConfig.java              # 跨域配置
│   │   │   ├── RedisConfig.java             # Redis配置
│   │   │   └── ShiroConfig.java             # Shiro主配置
│   │   │
│   │   ├── controller/                      # 控制器层
│   │   │   ├── EmployeeController.java      # 员工控制器
│   │   │   └── ReimbursementFormController.java  # 报销单控制器
│   │   │
│   │   ├── entity/                          # 实体类
│   │   │   ├── ApprovalRecord.java          # 审批记录实体
│   │   │   ├── Employee.java                # 员工实体
│   │   │   ├── ReimbursementDetail.java     # 报销明细实体
│   │   │   ├── ReimbursementForm.java       # 报销单实体
│   │   │   └── Role.java                    # 角色实体
│   │   │
│   │   ├── mapper/                          # Mapper接口层
│   │   │   ├── ApprovalRecordMapper.java    # 审批记录Mapper
│   │   │   ├── EmployeeMapper.java          # 员工Mapper
│   │   │   ├── ReimbursementDetailMapper.java    # 报销明细Mapper
│   │   │   └── ReimbursementFormMapper.java # 报销单Mapper
│   │   │
│   │   ├── service/                         # 服务层
│   │   │   ├── EmployeeService.java         # 员工服务
│   │   │   └── ReimbursementFormService.java     # 报销单服务
│   │   │
│   │   └── ReimbursementSystemApplication.java   # Spring Boot启动类
│   │
│   └── resources/
│       ├── mapper/                          # MyBatis XML映射文件
│       │   ├── ApprovalRecordMapper.xml     # 审批记录Mapper XML
│       │   ├── EmployeeMapper.xml           # 员工Mapper XML
│       │   ├── ReimbursementDetailMapper.xml     # 报销明细Mapper XML
│       │   └── ReimbursementFormMapper.xml  # 报销单Mapper XML
│       │
│       ├── application.yml                  # 应用配置文件
│       └── init.sql                        # 数据库初始化脚本
│
├── .gitignore                              # Git忽略文件
├── pom.xml                                 # Maven项目配置
├── Postman_Collection.json                 # Postman测试集合
└── README.md                               # 项目说明文档
```

## 模块说明

### 1. common - 公共模块
包含项目中通用的工具类、异常类、响应封装类等。

- **Result**: 统一API响应格式
- **PageResult**: 分页查询结果封装
- **BusinessException**: 自定义业务异常
- **GlobalExceptionHandler**: 全局异常处理器,统一异常响应
- **MD5Util**: 密码加密工具

### 2. config - 配置模块
包含Spring Boot、Shiro、Redis等框架的配置类。

- **ShiroConfig**: Shiro安全框架配置
- **CustomRealm**: 自定义认证和授权逻辑
- **RedisConfig**: Redis缓存配置
- **CorsConfig**: 跨域请求配置

### 3. controller - 控制器层
处理HTTP请求,调用服务层方法,返回响应结果。

- **EmployeeController**: 处理员工相关请求(登录、查询、CRUD)
- **ReimbursementFormController**: 处理报销单相关请求(提交、审批、查询)

### 4. entity - 实体类
与数据库表对应的Java实体类。

- **Employee**: 员工信息
- **Role**: 角色信息
- **ReimbursementForm**: 报销单主表
- **ReimbursementDetail**: 报销明细
- **ApprovalRecord**: 审批记录

### 5. mapper - 数据访问层
MyBatis的Mapper接口,定义数据库操作方法。

### 6. service - 服务层
实现业务逻辑,包含事务处理。

- **EmployeeService**: 员工管理业务逻辑
- **ReimbursementFormService**: 报销单管理业务逻辑

## 技术架构

```
┌─────────────────────────────────────────────┐
│           前端(可选Vue/React)                │
└─────────────────────────────────────────────┘
                     ↓ HTTP
┌─────────────────────────────────────────────┐
│              Controller层                    │
│         (接收请求、参数校验、返回响应)         │
└─────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────┐
│               Service层                      │
│      (业务逻辑、事务管理、缓存处理)            │
└─────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────┐
│               Mapper层                       │
│          (数据访问、SQL映射)                  │
└─────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────┐
│              MySQL数据库                      │
└─────────────────────────────────────────────┘

            ┌──────────────┐
            │  Redis缓存    │  (旁路缓存)
            └──────────────┘
```

## 数据流向

### 查询流程
1. 用户请求 → Controller接收
2. Controller调用Service
3. Service检查Redis缓存
4. 缓存命中 → 直接返回
5. 缓存未命中 → Mapper查询数据库
6. 查询结果写入Redis
7. 返回数据给用户

### 更新流程
1. 用户请求 → Controller接收
2. Controller调用Service
3. Service开启事务
4. Mapper更新数据库
5. 清除Redis相关缓存
6. 提交事务
7. 返回结果给用户

## 权限控制

### Shiro认证流程
1. 用户登录 → 提交用户名密码
2. Shiro调用CustomRealm.doGetAuthenticationInfo()
3. 验证用户名密码
4. 创建Session,返回Token

### Shiro授权流程
1. 访问需要权限的接口
2. Shiro调用CustomRealm.doGetAuthorizationInfo()
3. 查询用户角色
4. 判断是否有权限
5. 有权限 → 继续执行
6. 无权限 → 返回403

## 性能优化点

1. **Redis缓存**: 热点数据缓存,减少数据库查询
2. **分页查询**: PageHelper实现高效分页
3. **批量操作**: 批量审批、批量插入减少数据库交互
4. **连接池**: Druid连接池提升数据库连接性能
5. **索引优化**: 数据库表添加合适索引
6. **事务控制**: 合理使用事务,避免长事务

## 安全性

1. **密码加密**: MD5加密存储
2. **权限控制**: Shiro基于角色的权限控制
3. **SQL注入防护**: MyBatis预编译SQL
4. **XSS防护**: 前端输入验证
5. **CSRF防护**: 可选配置

## 扩展性

1. **分层清晰**: 便于维护和扩展
2. **接口抽象**: 易于替换实现
3. **配置外部化**: 便于不同环境部署
4. **缓存可选**: 可以切换其他缓存方案
5. **数据库迁移**: 可以更换其他数据库
