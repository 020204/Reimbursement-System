import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true // 携带 cookie，与后端 session 一致
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 可以在这里添加token等认证信息
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200,说明接口请求失败
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      // 401: 未登录或 session 失效，清除本地状态
      if (res.code === 401) {
        useUserStore().clearUserOnly()
        router.push('/login')
      }
      
      // 403: 无权限
      if (res.code === 403) {
        ElMessage.error('您没有权限执行此操作')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const status = error.response.status
      
      switch (status) {
        case 401:
          ElMessage.error('未登录或登录已过期,请重新登录')
          useUserStore().clearUserOnly()
          router.push('/login')
          break
        case 403:
          ElMessage.error('没有权限访问此资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误,请检查您的网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default request
