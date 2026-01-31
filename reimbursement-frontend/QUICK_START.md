# 前端快速启动指南 ⚡

## 📦 前提条件

- Node.js 16+ 
- npm 或 pnpm 或 yarn

## 🚀 5分钟快速启动

### 1️⃣ 安装依赖

```bash
npm install
```

如果速度慢,可以使用国内镜像:

```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### 2️⃣ 确保后端已启动

确保后端服务运行在 `http://localhost:8080`

如果后端地址不同,修改 `vite.config.js`:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://your-backend-url:port',  // 修改这里
      changeOrigin: true
    }
  }
}
```

### 3️⃣ 启动开发服务器

```bash
npm run dev
```

看到以下输出说明启动成功:

```
  VITE v5.0.11  ready in 500 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

### 4️⃣ 访问系统

浏览器打开: http://localhost:3000

使用测试账号登录:
- 用户名: admin
- 密码: 123456

## 🎯 功能演示路径

### 路径1: 员工提交报销单
1. 使用 employee/123456 登录
2. 点击"报销管理" -> "创建报销单"
3. 填写标题、类型、说明
4. 点击"添加明细"按钮
5. 填写费用项目、金额、日期
6. 查看自动计算的总金额
7. 点击"提交审批"

### 路径2: 主管批量审批
1. 使用 manager/123456 登录
2. 点击"审批中心"
3. 勾选多个待审批报销单
4. 点击"批量通过"或"批量驳回"
5. 输入审批意见(可选)
6. 确认审批

### 路径3: 查看统计数据
1. 登录任意账号
2. 查看首页仪表盘
3. 查看统计卡片(待审批、已通过等)
4. 查看最近的报销单列表

## 📋 页面清单

| 页面 | 路径 | 权限 |
|------|------|------|
| 登录 | /login | 所有人 |
| 首页 | /dashboard | 登录用户 |
| 报销单列表 | /reimbursement/list | 登录用户 |
| 创建报销单 | /reimbursement/create | 登录用户 |
| 报销单详情 | /reimbursement/detail/:id | 登录用户 |
| 审批中心 | /approval | 主管/财务/管理员 |
| 员工管理 | /employee/list | 管理员 |

## 🔧 常见问题

### Q1: npm install 失败?
**A:** 更换国内镜像
```bash
npm config set registry https://registry.npmmirror.com
npm install
```

### Q2: 启动后无法访问后端?
**A:** 检查:
1. 后端是否已启动(localhost:8080)
2. vite.config.js 中的代理配置是否正确
3. 浏览器控制台是否有错误

### Q3: 登录后白屏?
**A:** 
1. 打开浏览器控制台查看错误
2. 检查后端API是否正常返回数据
3. 清除浏览器缓存和localStorage

### Q4: 跨域问题?
**A:** 开发环境已配置代理,如果仍有问题:
1. 确认后端允许跨域
2. 检查 vite.config.js 代理配置
3. 重启开发服务器

### Q5: 页面刷新后登录状态丢失?
**A:** 不会丢失,已配置Pinia持久化

## 🎨 界面预览

### 登录页
- 渐变色背景
- 居中的登录表单
- 测试账号提示

### 首页仪表盘
- 4个统计卡片(不同颜色)
- 最近报销单表格
- 快速操作按钮

### 创建报销单
- 基本信息表单
- **动态明细表格**
- **实时金额计算**
- 保存/提交按钮

### 审批中心
- 待审批列表
- **多选复选框**
- **批量操作按钮**
- 审批对话框

## 🛠️ 开发工具推荐

### VSCode插件
- Volar (Vue Language Features)
- ESLint
- Prettier
- Auto Rename Tag

### Chrome插件
- Vue.js devtools
- React Developer Tools

## 📚 学习资源

- [Vue 3 官方文档](https://cn.vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/zh-CN/)
- [Pinia 官方文档](https://pinia.vuejs.org/zh/)
- [Vite 官方文档](https://cn.vitejs.dev/)

## 🎯 下一步

1. ✅ 熟悉各个页面功能
2. ✅ 了解组件结构
3. ✅ 学习Composition API
4. ✅ 查看Element Plus文档
5. ✅ 根据需求进行二次开发

## 💡 开发提示

### 修改主题色
编辑 `src/assets/style.css`:
```css
:root {
  --el-color-primary: #409eff; /* 修改这里 */
}
```

### 添加新页面
1. 在 `src/views/` 创建组件
2. 在 `src/router/index.js` 添加路由
3. 在布局侧边栏添加菜单

### 调用新接口
1. 在 `src/api/` 添加接口函数
2. 在组件中引入并调用
3. 使用 try-catch 处理错误

---

**祝你使用愉快! 🎉**

有问题随时查看 README.md 或提 Issue
