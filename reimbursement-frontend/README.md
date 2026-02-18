# 报销管理系统 - 前端项目

基于 Vue 3 + Element Plus 的现代化报销管理系统前端

## 🎯 技术栈

- **框架**: Vue 3 (Composition API)
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **构建工具**: Vite
- **图标**: @element-plus/icons-vue

## ✨ 功能特性

### 1. 用户认证
- ✅ 登录/登出
- ✅ 基于角色的权限控制
- ✅ 路由守卫
- ✅ **Pinia状态持久化** (F5刷新不丢失登录)

### 2. 报销管理
- ✅ 报销单列表(分页、搜索、筛选、**数据权限控制**)
- ✅ 创建报销单(动态添加明细)
- ✅ **编辑报销单** (创建/编辑复用页面)
- ✅ 报销单详情查看
- ✅ 提交/删除报销单
- ✅ 实时计算总金额

### 3. 审批中心
- ✅ 待审批列表
- ✅ 单个审批(通过/驳回)
- ✅ 批量审批
- ✅ 审批意见输入

### 4. 员工管理(管理员)
- ✅ 员工列表
- ✅ 条件查询

### 5. 仪表盘
- ✅ 数据统计卡片
- ✅ 最近报销单展示
- ✅ 快速入口

## 📁 项目结构

```
src/
├── api/                    # API接口
│   ├── employee.js        # 员工相关接口
│   └── reimbursement.js   # 报销单相关接口
├── assets/                # 静态资源
├── components/            # 公共组件
├── router/                # 路由配置
│   └── index.js
├── stores/                # Pinia状态管理
│   └── user.js           # 用户状态
├── utils/                 # 工具函数
│   └── request.js        # Axios封装
├── views/                 # 页面组件
│   ├── login/            # 登录页
│   ├── layout/           # 布局框架
│   ├── dashboard/        # 仪表盘
│   ├── reimbursement/    # 报销管理
│   │   ├── list.vue
│   │   ├── create.vue
│   │   └── detail.vue
│   ├── approval/         # 审批中心
│   └── employee/         # 员工管理
├── App.vue               # 根组件
└── main.js               # 入口文件
```

## 🚀 快速开始

### 1. 安装依赖

```bash
npm install
# 或
pnpm install
# 或
yarn install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问: http://localhost:3000

### 3. 构建生产版本

```bash
npm run build
```

### 4. 预览生产构建

```bash
npm run preview
```

## 🔧 配置说明

### 后端API地址配置

在 `vite.config.js` 中配置代理:

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 后端地址
      changeOrigin: true
    }
  }
}
```

## 👥 测试账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 所有功能 |
| finance | 123456 | 财务 | 审批权限 |
| manager | 123456 | 主管 | 审批权限 |
| employee | 123456 | 员工 | 基本功能 |

## 📱 页面说明

### 登录页 (/login)
- 用户名密码登录
- 表单验证
- 测试账号提示

### 首页 (/dashboard)
- 数据统计卡片(待审批、已通过、已驳回、总数)
- 最近报销单列表
- 快速操作入口

### 报销单列表 (/reimbursement/list)
- 分页展示
- 状态/类型筛选
- 提交/编辑/删除操作
- 快速查看详情

### 创建/编辑报销单 (/reimbursement/create 和 /reimbursement/edit/:id)
- 基本信息录入
- **动态添加报销明细**
- **实时计算总金额**
- 保存草稿/提交审批
- **编辑模式加载原有数据**

### 报销单详情 (/reimbursement/detail/:id)
- 完整信息展示
- 报销明细表格
- 审批记录时间线

### 审批中心 (/approval)
- 待审批列表
- 单个审批/批量审批
- 审批意见输入
- 权限控制(仅主管/财务/管理员可见)

### 员工管理 (/employee/list)
- 员工列表展示
- 姓名/部门搜索
- 权限控制(仅管理员可见)

## 🎨 核心功能实现

### 1. 动态报销明细

```vue
<template>
  <!-- 添加明细按钮 -->
  <el-button @click="addDetail">添加明细</el-button>
  
  <!-- 明细表格 -->
  <el-table :data="form.details">
    <el-table-column label="费用项目">
      <template #default="{ row }">
        <el-input v-model="row.itemName" />
      </template>
    </el-table-column>
    
    <el-table-column label="金额">
      <template #default="{ row }">
        <el-input-number v-model="row.amount" />
      </template>
    </el-table-column>
    
    <el-table-column label="操作">
      <template #default="{ $index }">
        <el-button @click="removeDetail($index)">删除</el-button>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
const addDetail = () => {
  form.details.push({
    itemName: '',
    amount: 0,
    occurrenceDate: '',
    description: ''
  })
}

const removeDetail = (index) => {
  form.details.splice(index, 1)
}

// 实时计算总金额
const totalAmount = computed(() => {
  return form.details.reduce((sum, item) => {
    return sum + (Number(item.amount) || 0)
  }, 0).toFixed(2)
})
</script>
```

### 2. 批量审批

```vue
<template>
  <!-- 批量操作按钮 -->
  <el-button 
    :disabled="selectedRows.length === 0"
    @click="handleBatchApprove('APPROVED')"
  >
    批量通过
  </el-button>
  
  <!-- 表格支持多选 -->
  <el-table 
    :data="tableData"
    @selection-change="handleSelectionChange"
  >
    <el-table-column type="selection" />
    <!-- 其他列 -->
  </el-table>
</template>

<script setup>
const selectedRows = ref([])

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleBatchApprove = async (result) => {
  const ids = selectedRows.value.map(row => row.id)
  await batchApprove({ ids, result, comment: '' })
}
</script>
```

### 3. 权限控制

```vue
<!-- 路由级别权限 -->
<script>
// router/index.js
{
  path: '/approval',
  meta: { 
    roles: ['ADMIN', 'FINANCE', 'MANAGER']
  }
}

// 路由守卫检查权限
router.beforeEach((to, from, next) => {
  if (to.meta.roles) {
    const hasRole = to.meta.roles.some(role => 
      userStore.roles.includes(role)
    )
    if (!hasRole) {
      ElMessage.error('您没有权限访问此页面')
      return next(false)
    }
  }
  next()
})
</script>

<!-- 组件级别权限 -->
<template>
  <el-menu-item 
    v-if="userStore.canApprove" 
    index="/approval"
  >
    审批中心
  </el-menu-item>
</template>
```

### 4. Axios请求封装

```javascript
// utils/request.js
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true // 携带cookie
})

// 响应拦截器统一处理
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message)
      if (res.code === 401) {
        router.push('/login')
      }
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    ElMessage.error('请求失败')
    return Promise.reject(error)
  }
)
```

## 🔒 安全特性

- ✅ 路由守卫(登录验证)
- ✅ 权限控制(角色验证)
- ✅ **数据权限控制** (不同角色查看不同数据范围)
- ✅ Token自动携带
- ✅ 401/403统一处理
- ✅ XSS防护(Element Plus内置)

## 📊 性能优化

- ✅ 路由懒加载
- ✅ 组件按需引入(unplugin-vue-components)
- ✅ Vite快速构建
- ✅ 生产环境代码压缩

## 🎯 最佳实践

### 1. 组件命名
- 页面组件: PascalCase (如 ReimbursementList)
- 公共组件: PascalCase (如 UserAvatar)

### 2. API调用
- 统一在 api/ 目录管理
- 使用 try-catch 处理错误
- 显示loading状态

### 3. 状态管理
- 用户信息存储在 Pinia
- 支持持久化(localStorage)

### 4. 样式规范
- 使用 scoped 避免样式污染
- 使用 Element Plus 主题色
- 响应式布局

## 🐛 常见问题

### 1. 跨域问题
已在 vite.config.js 配置代理,开发环境无需担心

### 2. 登录后刷新丢失状态
已使用 `pinia-plugin-persistedstate` 实现状态持久化，登录信息保存在 localStorage，刷新页面不会丢失

### 3. 生产环境API地址
修改 vite.config.js 中的 target 地址

## 📝 开发建议

1. **遵循Vue 3最佳实践**
   - 使用 Composition API
   - 善用 computed 和 watch
   - 合理拆分组件

2. **Element Plus使用**
   - 查阅官方文档
   - 使用组件类型提示
   - 自定义主题色

3. **代码规范**
   - 使用ESLint
   - 统一命名规范
   - 添加必要注释

## 🔮 后续优化方向

1. **功能扩展**
   - 文件上传(报销凭证)
   - Excel导入导出
   - 数据可视化图表
   - 消息通知

2. **体验优化**
   - 页面骨架屏
   - 请求防抖节流
   - 离线缓存
   - PWA支持

3. **测试**
   - 单元测试
   - E2E测试

## 📄 许可证

MIT License

## 🤝 贡献

欢迎提交 Issue 和 Pull Request
