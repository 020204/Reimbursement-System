import request from '@/utils/request'

/**
 * 员工管理API
 */

// 登录
export function login(data) {
  return request({
    url: '/employee/login',
    method: 'post',
    data
  })
}

// 登出
export function logout() {
  return request({
    url: '/employee/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getCurrentUser() {
  return request({
    url: '/employee/current',
    method: 'get'
  })
}

// 查询所有员工
export function getEmployeeList() {
  return request({
    url: '/employee/list',
    method: 'get'
  })
}

// 根据ID查询员工
export function getEmployeeById(id) {
  return request({
    url: `/employee/${id}`,
    method: 'get'
  })
}

// 条件查询员工
export function searchEmployees(params) {
  return request({
    url: '/employee/search',
    method: 'get',
    params
  })
}

// 添加员工
export function addEmployee(data) {
  return request({
    url: '/employee',
    method: 'post',
    data
  })
}

// 更新员工
export function updateEmployee(data) {
  return request({
    url: '/employee',
    method: 'put',
    data
  })
}

// 删除员工
export function deleteEmployee(id) {
  return request({
    url: `/employee/${id}`,
    method: 'delete'
  })
}
