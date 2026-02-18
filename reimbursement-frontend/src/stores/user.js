import { defineStore } from 'pinia'
import { login, logout, getCurrentUser } from '@/api/employee'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: null,
    roles: []
  }),
  
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!state.userInfo,
    
    // 用户名
    username: (state) => state.userInfo?.username || '',
    
    // 姓名
    name: (state) => state.userInfo?.name || '',
    
    // 是否是管理员
    isAdmin: (state) => state.roles.includes('ADMIN'),
    
    // 是否是财务
    isFinance: (state) => state.roles.includes('FINANCE'),
    
    // 是否是主管
    isManager: (state) => state.roles.includes('MANAGER'),

    // 是否是HR
    isHR: (state) => state.roles.includes('HR'),

    // 是否有审批权限
    canApprove: (state) => {
      return state.roles.includes('ADMIN') ||
             state.roles.includes('FINANCE') ||
             state.roles.includes('MANAGER')
    }
  },
  
  actions: {
    // 登录
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        this.userInfo = res.data.employee
        this.token = res.data.token
        
        // 这里可以从后端获取角色信息,暂时根据职位判断
        this.roles = this.getUserRoles(res.data.employee)
        
        return res
      } catch (error) {
        throw error
      }
    },
    
    // 仅清除本地状态（用于 401 时，避免再请求后端）
    clearUserOnly() {
      this.userInfo = null
      this.token = null
      this.roles = []
    },

    // 登出
    async logout() {
      try {
        await logout()
      } catch (error) {
        // 即使后端报错也清除本地信息
      } finally {
        this.clearUserOnly()
      }
    },
    
    // 获取当前用户信息
    async fetchUserInfo() {
      try {
        const res = await getCurrentUser()
        this.userInfo = res.data
        this.roles = this.getUserRoles(res.data)
        return res
      } catch (error) {
        throw error
      }
    },
    
    // 根据用户信息判断角色(简化处理)
    getUserRoles(user) {
      const roles = []

      if (user.position === '系统管理员') {
        roles.push('ADMIN')
      }
      if (user.department === '财务部' || user.position?.includes('财务')) {
        roles.push('FINANCE')
      }
      if (user.department === '行政部' || user.position?.includes('人事') || user.position?.includes('HR')) {
        roles.push('HR')
      }
      if (user.position?.includes('主管') || user.position?.includes('经理')) {
        roles.push('MANAGER')
      }

      roles.push('EMPLOYEE') // 所有人都是员工

      return roles
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
    }
  },
  
  // 持久化
  persist: {
    key: 'user',
    storage: localStorage
  }
})
