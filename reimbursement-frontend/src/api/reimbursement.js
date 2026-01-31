import request from '@/utils/request'

/**
 * 报销单管理API
 */

// 根据ID查询报销单
export function getFormById(id) {
  return request({
    url: `/reimbursement/${id}`,
    method: 'get'
  })
}

// 分页查询报销单
export function getFormPage(params) {
  return request({
    url: '/reimbursement/page',
    method: 'get',
    params
  })
}

// 查询所有报销单
export function getFormList() {
  return request({
    url: '/reimbursement/list',
    method: 'get'
  })
}

// 条件查询报销单
export function searchForms(params) {
  return request({
    url: '/reimbursement/search',
    method: 'get',
    params
  })
}

// 创建报销单
export function createForm(data) {
  return request({
    url: '/reimbursement',
    method: 'post',
    data
  })
}

// 更新报销单
export function updateForm(data) {
  return request({
    url: '/reimbursement',
    method: 'put',
    data
  })
}

// 提交报销单
export function submitForm(id) {
  return request({
    url: `/reimbursement/submit/${id}`,
    method: 'put'
  })
}

// 审批报销单
export function approveForm(data) {
  return request({
    url: '/reimbursement/approve',
    method: 'put',
    data
  })
}

// 批量审批报销单
export function batchApprove(data) {
  return request({
    url: '/reimbursement/batch-approve',
    method: 'put',
    data
  })
}

// 删除报销单
export function deleteForm(id) {
  return request({
    url: `/reimbursement/${id}`,
    method: 'delete'
  })
}
