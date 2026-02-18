# 报销管理系统 - 前后端完整项目

一个基于 **Spring Boot + Vue 3 + Element Plus** 的现代化企业级报销管理系统

## 🎯 项目概述

这是一个功能完整、代码规范、开箱即用的报销管理系统,包含:

✅ **完整的后端项目** (Spring Boot + MyBatis + Redis + Shiro)  
✅ **完整的前端项目** (Vue 3 + Element Plus + Pinia + Vite)  
✅ **数据库初始化脚本** (7张表 + 测试数据)  
✅ **详细的文档** (README + 快速启动指南)  
✅ **Postman测试集合**

## 📦 项目文件

```
reimbursement-system.tar.gz          # 后端项目(26KB)
reimbursement-frontend.tar.gz        # 前端项目(19KB)
```

## 🏗️ 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.14
- **持久层**: MyBatis 2.2.2
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **安全**: Apache Shiro 1.11.0
- **连接池**: Druid
- **分页**: PageHelper

### 前端技术栈
- **框架**: Vue 3 (Composition API)
- **UI组件**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP**: Axios
- **构建**: Vite 5

## ✨ 核心功能

### 1. 用户认证与权限
- ✅ 登录/登出
- ✅ Shiro权限控制
- ✅ 基于角色的菜单显示
- ✅ 路由守卫

### 2. 报销单管理
- ✅ 创建报销单(支持动态添加多个明细)
- ✅ **编辑报销单** (草稿状态可修改)
- ✅ 实时计算总金额
- ✅ 提交审批
- ✅ 分页查询
- ✅ **数据权限控制** (根据角色显示不同数据范围)
- ✅ 状态/类型筛选
- ✅ 详情查看

### 3. 审批流程
- ✅ 单个审批
- ✅ **批量审批** (核心亮点)
- ✅ 审批意见输入
- ✅ 审批记录查看
- ✅ 权限控制(仅主管/财务/管理员可见)

### 4. 数据统计
- ✅ 仪表盘数据卡片
- ✅ 待审批/已通过/已驳回统计
- ✅ 最近报销单展示

### 5. 员工管理
- ✅ 员工列表
- ✅ 条件查询
- ✅ 权限控制(仅管理员可见)

## 🚀 快速开始(10分钟部署)

### 准备环境

**后端环境:**
- JDK 1.8+
- MySQL 8.0+
- Redis 3.0+
- Maven 3.6+

**前端环境:**
- Node.js 16+
- npm/pnpm/yarn

### 第一步: 部署后端

```bash
# 1. 解压后端项目
tar -xzf reimbursement-system.tar.gz
cd reimbursement-system

# 2. 初始化数据库
mysql -u root -p < src/main/resources/init.sql

# 3. 修改配置(如需要)
vim src/main/resources/application.yml
# 修改数据库用户名密码、Redis配置等

# 4. 启动后端
mvn spring-boot:run
# 或者打包后运行: mvn clean package && java -jar target/reimbursement-system-1.0.0.jar
```

启动成功后访问: http://localhost:8080/api

### 第二步: 部署前端

```bash
# 1. 解压前端项目
tar -xzf reimbursement-frontend.tar.gz
cd reimbursement-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev
```

启动成功后访问: http://localhost:3000

### 第三步: 登录测试

使用以下测试账号登录:

| 用户名 | 密码 | 角色 | 功能 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 所有功能 |
| finance | 123456 | 财务 | 审批功能 |
| manager | 123456 | 主管 | 审批功能 |
| employee | 123456 | 员工 | 基本功能 |

## 🔐 数据权限设计

系统实现了基于角色的数据权限控制:

| 角色 | 数据权限 | 实现方式 | 设计理由 |
|------|----------|----------|----------|
| **普通员工** | 只看自己的报销 | `employee_id = 当前用户ID` | 保护隐私，避免内部攀比、泄密 |
| **部门主管/经理** | 看本部门员工 | `employee_id IN (本部门员工ID列表)` | 审批需要 |
| **HR/财务** | 看全部 | 无过滤 | 统计、审计需要 |
| **管理员** | 看全部 | 无过滤 | 系统管理 |

### 权限实现代码示例
```java
// Service层权限控制
if (用户是 ADMIN 或 FINANCE) {
    // 查看全部
    list = formMapper.selectByCondition(null, status, type);
} else if (用户是 MANAGER) {
    // 查看本部门
    List<Integer> deptIds = employeeMapper.selectIdsByDepartment(当前用户部门);
    list = formMapper.selectByEmployeeIds(deptIds, status, type);
} else {
    // 普通员工，只看自己
    list = formMapper.selectByCondition(当前用户ID, status, type);
}
```

## 🎬 功能演示流程

### 流程1: 员工提交报销单 → 主管审批

1. **员工提交** (employee/123456)
   - 登录系统
   - 点击"报销管理" → "创建报销单"
   - 填写标题: "北京出差报销"
   - 选择类型: "差旅费"
   - 点击"添加明细"
   - 填写第一条: 高铁票 800元
   - 填写第二条: 酒店 600元
   - **查看自动计算的总金额: ¥1400**
   - 点击"提交审批"

2. **主管审批** (manager/123456)
   - 登出员工账号
   - 使用主管账号登录
   - 点击"审批中心"
   - 看到待审批的报销单
   - 点击"查看"查看详情
   - 点击"通过"进行审批
   - 输入审批意见(可选)
   - 确认审批

3. **查看结果**
   - 切换回员工账号
   - 查看报销单列表
   - 状态变为"已通过"

### 流程3: 编辑报销单演示

1. **创建草稿** (employee/123456)
   - 创建报销单但不提交
   - 保存为草稿状态

2. **编辑草稿**
   - 在列表页找到草稿状态的报销单
   - 点击"编辑"按钮
   - 修改报销金额或添加明细
   - 点击"保存修改"
   - 数据更新成功

### 流程4: 数据权限验证

1. **普通员工视角** (employee/123456)
   - 登录后查看报销列表
   - 只能看到自己创建的报销单

2. **主管视角** (manager/123456)
   - 登录后查看报销列表
   - 可以看到本部门所有员工的报销单

3. **管理员视角** (admin/123456)
   - 登录后查看报销列表
   - 可以看到全部员工的报销单

### 流程2: 批量审批演示

1. **准备数据** (employee/123456)
   - 创建3-5个报销单
   - 全部提交审批

2. **批量审批** (finance/123456)
   - 使用财务账号登录
   - 进入"审批中心"
   - **勾选多个待审批报销单**
   - **点击"批量通过"按钮**
   - 输入批量审批意见
   - 确认批量审批
   - 所有选中的报销单状态变更

## 🎨 界面截图说明

### 登录页
- 渐变色背景(紫色系)
- 居中的白色登录卡片
- 用户名密码输入框
- 测试账号提示

### 首页仪表盘
- **4个彩色统计卡片**
  - 待审批(黄色)
  - 已通过(绿色)
  - 已驳回(红色)
  - 总报销单(蓝色)
- 最近报销单表格
- 快速操作入口

### 报销单列表
- 状态/类型筛选器
- 分页表格
- 不同状态的彩色标签
- 操作按钮(查看/提交/编辑/删除)

### 创建报销单 ⭐核心页面
- 基本信息表单
- **动态明细表格**
  - 添加明细按钮
  - 可输入的表格单元格
  - 删除按钮
- **实时计算显示总金额**
- 保存草稿/提交审批按钮

### 审批中心 ⭐核心页面
- 待审批列表
- **表格多选复选框**
- **批量通过/批量驳回按钮**
- 审批对话框(输入意见)
- 权限控制显示

## 📊 数据库设计

### 7张核心表

1. **employee** - 员工表
   - 存储用户信息、部门、职位

2. **role** - 角色表
   - 管理员、财务、主管、员工

3. **user_role** - 用户角色关联表

4. **reimbursement_form** - 报销单表
   - 报销单号、标题、类型、金额、状态

5. **reimbursement_detail** - 报销明细表
   - 费用项目、金额、发生日期

6. **approval_record** - 审批记录表
   - 审批人、结果、意见、时间

7. **operation_log** - 操作日志表

## 🔥 项目亮点

### 后端亮点

1. **清晰的三层架构**
   - Controller → Service → Mapper
   - 职责分离,易于维护

2. **完善的权限控制**
   - Shiro安全框架
   - 基于角色的访问控制(RBAC)
   - **数据权限控制** (不同角色查看不同数据范围)
   - Redis分布式Session (刷新页面不丢失登录)
   - 密码MD5加密

3. **性能优化**
   - Redis缓存热点数据
   - PageHelper分页插件
   - 批量操作减少数据库交互
   - Druid连接池

4. **统一异常处理**
   - 全局异常拦截
   - 统一响应格式
   - 友好的错误提示

5. **事务管理**
   - 声明式事务
   - 保证数据一致性

6. **数据安全**
   - 普通员工只能看自己的报销
   - 部门主管看本部门数据
   - 财务/管理员查看全部

### 前端亮点

1. **动态表单设计** ⭐
   - 可动态添加/删除报销明细
   - 实时计算总金额
   - 表单验证
   - **创建/编辑复用同一页面**

2. **批量操作** ⭐
   - 批量审批功能
   - 多选复选框
   - 批量操作按钮禁用控制

3. **权限控制**
   - 路由级别权限(路由守卫)
   - 组件级别权限(v-if指令)
   - 菜单动态显示

4. **用户体验**
   - 统一的错误提示
   - Loading加载状态
   - 操作二次确认
   - **Pinia状态持久化** (F5刷新不丢失登录)
   - 响应式布局

5. **代码质量**
   - Vue 3 Composition API
   - 组件化设计
   - TypeScript支持(可选)
   - 代码复用

## 📁 项目结构

### 后端结构
```
reimbursement-system/
├── src/main/
│   ├── java/
│   │   └── com/example/reimbursement/
│   │       ├── common/          # 公共类
│   │       ├── config/          # 配置类
│   │       ├── controller/      # 控制器
│   │       ├── entity/          # 实体类
│   │       ├── mapper/          # Mapper接口
│   │       └── service/         # 服务层
│   └── resources/
│       ├── mapper/              # MyBatis XML
│       ├── application.yml      # 配置
│       └── init.sql            # 数据库脚本
└── pom.xml
```

### 前端结构
```
reimbursement-frontend/
├── src/
│   ├── api/                    # API接口
│   ├── router/                 # 路由配置
│   ├── stores/                 # 状态管理
│   ├── utils/                  # 工具函数
│   └── views/                  # 页面组件
│       ├── login/             # 登录
│       ├── layout/            # 布局
│       ├── dashboard/         # 首页
│       ├── reimbursement/     # 报销管理
│       ├── approval/          # 审批中心
│       └── employee/          # 员工管理
├── package.json
└── vite.config.js
```

## 🔧 配置说明

### 后端配置

**数据库配置** (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/reimbursement_db
    username: root
    password: your_password
```

**Redis配置**
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
```

### 前端配置

**API代理配置** (`vite.config.js`)
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 📝 API接口文档

### 员工管理
- POST `/api/employee/login` - 登录
- POST `/api/employee/logout` - 登出
- GET `/api/employee/current` - 获取当前用户
- GET `/api/employee/list` - 查询所有员工

### 报销管理
- POST `/api/reimbursement` - 创建报销单
- PUT `/api/reimbursement` - 更新报销单(含明细)
- GET `/api/reimbursement/page` - 分页查询(**带数据权限控制**)
- GET `/api/reimbursement/{id}` - 查询详情
- PUT `/api/reimbursement/submit/{id}` - 提交
- PUT `/api/reimbursement/approve` - 审批
- PUT `/api/reimbursement/batch-approve` - 批量审批
- DELETE `/api/reimbursement/{id}` - 删除

**权限说明:**
- 分页查询接口根据当前用户角色自动过滤数据范围
- 普通员工: 只能看到自己的报销单
- 部门主管: 能看到本部门所有员工的报销单
- 财务/管理员: 能看到全部报销单

详细API文档查看后端项目的 `README.md`

## 🎯 适用场景

✅ **毕业设计** - 功能完整,代码规范  
✅ **课程设计** - 文档齐全,易于理解  
✅ **企业使用** - 可直接部署或二次开发  
✅ **学习参考** - 最佳实践,技术主流  
✅ **面试作品** - 展示技术能力

## 🚧 后续扩展方向

### 功能扩展
1. **文件上传** - 支持上传报销凭证(图片/PDF)
2. **消息通知** - 邮件/短信通知审批结果
3. **工作流引擎** - Activiti实现复杂审批流程
4. **数据统计** - 报销数据分析和图表展示
5. **Excel导入导出** - 批量导入/导出报销数据
6. **移动端** - 开发移动App或H5页面

### 技术优化
1. **前端** - TypeScript、单元测试、PWA
2. **后端** - 微服务架构、消息队列、ES搜索
3. **部署** - Docker容器化、CI/CD自动部署
4. **监控** - 日志中心、性能监控、链路追踪

## 📚 学习资源

### 后端
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [MyBatis官方文档](https://mybatis.org/mybatis-3/zh/)
- [Shiro官方文档](https://shiro.apache.org/)

### 前端
- [Vue 3官方文档](https://cn.vuejs.org/)
- [Element Plus官方文档](https://element-plus.org/zh-CN/)
- [Pinia官方文档](https://pinia.vuejs.org/zh/)

## 💡 开发建议

1. **先熟悉功能**
   - 使用不同角色测试所有功能
   - 了解业务流程

2. **学习代码结构**
   - 从简单页面开始
   - 理解前后端交互流程

3. **参考最佳实践**
   - 代码注释详细
   - 命名规范统一

4. **二次开发**
   - 根据需求添加新功能
   - 保持代码风格一致

## 🐛 常见问题

### 后端问题

**Q: 启动失败,数据库连接错误?**  
A: 检查MySQL是否启动,配置是否正确

**Q: Redis连接失败?**  
A: 确认Redis服务已启动

**Q: 端口被占用?**  
A: 修改application.yml中的端口号

### 前端问题

**Q: npm install失败?**  
A: 使用国内镜像: `npm config set registry https://registry.npmmirror.com`

**Q: 无法访问后端API?**  
A: 检查后端是否启动,代理配置是否正确

**Q: 登录后白屏?**  
A: 查看浏览器控制台错误,检查后端API返回

**Q: F5刷新后需要重新登录?**  
A: 已使用 `pinia-plugin-persistedstate` 实现状态持久化，登录信息会保存在 localStorage 中，刷新页面不会丢失

## 📄 许可证

MIT License

## 🤝 支持

有问题欢迎:
- 查看详细的README文档
- 检查QUICK_START快速启动指南
- 参考代码注释

---

**祝你使用愉快! 🎉**

这是一个生产级别的完整项目,适合学习、使用和二次开发!
