import request from '@/utils/request'

/**
 * 部门管理API
 */

// 查询所有部门
export function getDepartmentList() {
  return request({
    url: '/department/list',
    method: 'get'
  })
}

// 根据ID查询部门
export function getDepartmentById(id) {
  return request({
    url: `/department/${id}`,
    method: 'get'
  })
}

// 条件查询部门
export function searchDepartments(params) {
  return request({
    url: '/department/search',
    method: 'get',
    params
  })
}

// 添加部门
export function addDepartment(data) {
  return request({
    url: '/department',
    method: 'post',
    data
  })
}

// 更新部门
export function updateDepartment(data) {
  return request({
    url: '/department',
    method: 'put',
    data
  })
}

// 删除部门
export function deleteDepartment(id) {
  return request({
    url: `/department/${id}`,
    method: 'delete'
  })
}
