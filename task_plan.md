# 报销管理系统测试计划

## 项目概述
- **项目名称**: 报销管理系统 (Reimbursement System)
- **技术栈**: 
  - 后端: Spring Boot 2.7.14 + MyBatis + Shiro + MySQL 8.0 + Redis 7
  - 前端: Vue 3 + Element Plus + Vite + Pinia
- **测试目标**: 验证系统各模块功能完整性、API正确性、前后端集成

---

## 测试阶段

### Phase 1: 环境准备与依赖检查
**状态**: pending
**预计耗时**: 15分钟

#### 任务清单
- [ ] 1.1 检查后端依赖完整性 (pom.xml)
- [ ] 1.2 检查前端依赖完整性 (package.json)
- [ ] 1.3 验证 Docker Compose 配置
- [ ] 1.4 确认数据库初始化脚本 (init.sql)

#### 验证标准
- Maven 依赖无冲突
- npm 包安装成功
- Docker 服务配置正确

---

### Phase 2: 后端单元测试
**状态**: pending
**预计耗时**: 45分钟

#### 任务清单
- [ ] 2.1 创建测试目录结构 `src/test/java/com/example/reimbursement/`
- [ ] 2.2 编写 Entity 单元测试
  - [ ] EmployeeTest
  - [ ] DepartmentTest
  - [ ] ReimbursementFormTest
  - [ ] ReimbursementDetailTest
  - [ ] ApprovalRecordTest
- [ ] 2.3 编写 Mapper 层测试 (使用 @MybatisTest)
  - [ ] EmployeeMapperTest
  - [ ] DepartmentMapperTest
  - [ ] ReimbursementFormMapperTest
  - [ ] ReimbursementDetailMapperTest
  - [ ] ApprovalRecordMapperTest
- [ ] 2.4 编写 Service 层测试 (使用 @SpringBootTest + Mockito)
  - [ ] EmployeeServiceTest
  - [ ] ReimbursementFormServiceTest
- [ ] 2.5 编写 Controller 层测试 (使用 MockMvc)
  - [ ] EmployeeControllerTest
  - [ ] DepartmentControllerTest
  - [ ] ReimbursementFormControllerTest
- [ ] 2.6 编写工具类测试
  - [ ] MD5UtilTest
  - [ ] ResultTest

#### 验证标准
- 所有单元测试通过
- 代码覆盖率 > 60%

---

### Phase 3: 后端集成测试
**状态**: pending
**预计耗时**: 30分钟

#### 任务清单
- [ ] 3.1 配置测试环境 (application-test.yml)
- [ ] 3.2 编写数据库集成测试
  - [ ] 测试数据库连接
  - [ ] 测试 MyBatis 映射
- [ ] 3.3 编写 Redis 集成测试
  - [ ] 测试 Redis 连接
  - [ ] 测试 Session 存储
- [ ] 3.4 编写 Shiro 认证测试
  - [ ] 登录认证测试
  - [ ] 权限控制测试
- [ ] 3.5 编写 API 端到端测试
  - [ ] 员工管理 API 测试
  - [ ] 部门管理 API 测试
  - [ ] 报销单 API 测试

#### 验证标准
- 集成测试通过
- 数据库操作正常
- 缓存功能正常
- 认证授权正常

---

### Phase 4: 前端测试
**状态**: pending
**预计耗时**: 30分钟

#### 任务清单
- [ ] 4.1 安装前端测试依赖 (Vitest + @vue/test-utils)
- [ ] 4.2 配置 Vitest 测试环境
- [ ] 4.3 编写组件单元测试
  - [ ] 登录组件测试
  - [ ] 布局组件测试
  - [ ] 表单组件测试
- [ ] 4.4 编写 Store 测试 (Pinia)
  - [ ] user store 测试
- [ ] 4.5 编写 API 测试
  - [ ] request.js 测试
  - [ ] API 模块测试

#### 验证标准
- 组件测试通过
- Store 状态管理测试通过
- API 调用测试通过

---

### Phase 5: 端到端 (E2E) 测试
**状态**: pending
**预计耗时**: 30分钟

#### 任务清单
- [ ] 5.1 配置 E2E 测试环境 (Playwright/Cypress)
- [ ] 5.2 编写用户登录流程测试
- [ ] 5.3 编写报销单创建流程测试
- [ ] 5.4 编写审批流程测试
- [ ] 5.5 编写部门管理流程测试
- [ ] 5.6 编写员工管理流程测试

#### 验证标准
- 核心业务流程测试通过
- 页面交互正常
- 数据流转正确

---

### Phase 6: 性能测试
**状态**: pending
**预计耗时**: 20分钟

#### 任务清单
- [ ] 6.1 配置 JMeter/k6 测试环境
- [ ] 6.2 编写 API 性能测试脚本
  - [ ] 登录接口压力测试
  - [ ] 报销单查询性能测试
  - [ ] 报销单提交性能测试
- [ ] 6.3 执行性能测试并记录结果
- [ ] 6.4 分析性能瓶颈

#### 验证标准
- API 响应时间 < 500ms (95%)
- 并发用户支持 > 50
- 无内存泄漏

---

### Phase 7: 测试报告与总结
**状态**: pending
**预计耗时**: 15分钟

#### 任务清单
- [ ] 7.1 汇总所有测试结果
- [ ] 7.2 生成测试覆盖率报告
- [ ] 7.3 编写测试总结报告
- [ ] 7.4 记录发现的问题和改进建议

---

## 测试环境配置

### 后端测试配置
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  redis:
    host: localhost
    port: 6379
```

### 前端测试配置
```javascript
// vitest.config.js
export default {
  test: {
    environment: 'jsdom',
    globals: true
  }
}
```

---

## 错误记录

| 阶段 | 错误描述 | 尝试次数 | 解决方案 |
|------|----------|----------|----------|
| | | | |

---

## 测试执行命令

### 后端测试
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=EmployeeServiceTest

# 生成测试报告
mvn surefire-report:report
```

### 前端测试
```bash
# 运行单元测试
npm run test:unit

# 运行 E2E 测试
npm run test:e2e

# 生成覆盖率报告
npm run test:coverage
```

---

## 注意事项

1. **测试数据**: 使用 @Sql 注解或 Flyway 管理测试数据
2. **测试隔离**: 每个测试方法应独立，不依赖执行顺序
3. **Mock 使用**: 外部服务（如邮件、短信）使用 Mock
4. **CI/CD 集成**: 测试脚本需适配 GitHub Actions/Jenkins

---

## 当前状态

**当前阶段**: Phase 1 - 环境准备与依赖检查
**总体进度**: 0%
**最后更新**: 2026-02-20
