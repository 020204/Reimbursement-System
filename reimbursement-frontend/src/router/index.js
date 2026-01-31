import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/login/index.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/views/layout/index.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          meta: { title: '首页' }
        },
        {
          path: '/reimbursement/list',
          name: 'ReimbursementList',
          component: () => import('@/views/reimbursement/list.vue'),
          meta: { title: '报销单列表' }
        },
        {
          path: '/reimbursement/create',
          name: 'ReimbursementCreate',
          component: () => import('@/views/reimbursement/create.vue'),
          meta: { title: '创建报销单' }
        },
        {
          path: '/reimbursement/detail/:id',
          name: 'ReimbursementDetail',
          component: () => import('@/views/reimbursement/detail.vue'),
          meta: { title: '报销单详情' }
        },
        {
          path: '/approval',
          name: 'Approval',
          component: () => import('@/views/approval/index.vue'),
          meta: { 
            title: '审批中心',
            roles: ['ADMIN', 'FINANCE', 'MANAGER']
          }
        },
        {
          path: '/employee/list',
          name: 'EmployeeList',
          component: () => import('@/views/employee/list.vue'),
          meta: { 
            title: '员工管理',
            roles: ['ADMIN']
          }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 需要登录的页面
  if (to.meta.requiresAuth !== false) {
    if (!userStore.isLoggedIn) {
      next('/login')
      return
    }
    
    // 检查角色权限
    if (to.meta.roles && to.meta.roles.length > 0) {
      const hasRole = to.meta.roles.some(role => userStore.roles.includes(role))
      if (!hasRole) {
        ElMessage.error('您没有权限访问此页面')
        next(from.path || '/dashboard')
        return
      }
    }
  }
  
  // 已登录用户访问登录页,重定向到首页
  if (to.path === '/login' && userStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router
