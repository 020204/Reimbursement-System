<template>
  <div class="reimbursement-list">
    <el-card shadow="hover">
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable>
            <el-option label="草稿" value="DRAFT" />
            <el-option label="待审批" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="全部" clearable>
            <el-option label="差旅" value="TRAVEL" />
            <el-option label="办公" value="OFFICE" />
            <el-option label="通讯" value="COMMUNICATION" />
            <el-option label="招待" value="ENTERTAINMENT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 操作按钮 -->
      <div class="toolbar">
        <el-button type="primary" @click="goToCreate">
          <el-icon><Plus /></el-icon>
          新建报销单
        </el-button>
      </div>
      
      <!-- 表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        border
        stripe
      >
        <el-table-column prop="formNo" label="报销单号" width="150" fixed />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="employeeName" label="申请人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link 
              @click="viewDetail(row.id)"
            >
              查看
            </el-button>
            
            <el-button 
              v-if="row.status === 'DRAFT'"
              type="success" 
              link 
              @click="handleSubmit(row.id)"
            >
              提交
            </el-button>
            
            <el-button 
              v-if="row.status === 'DRAFT'"
              type="warning" 
              link 
              @click="handleEdit(row.id)"
            >
              编辑
            </el-button>
            
            <el-button 
              v-if="row.status === 'DRAFT'"
              type="danger" 
              link 
              @click="handleDelete(row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFormPage, submitForm, deleteForm } from '@/api/reimbursement'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])

// 判断是否是 ADMIN 或 FINANCE（可以查看所有报销单）
const canViewAll = computed(() => {
  return userStore.isAdmin || userStore.isFinance
})

const queryForm = reactive({
  status: '',
  type: '',
  employeeId: canViewAll.value ? null : userStore.userInfo?.id // ADMIN/FINANCE 查看所有，其他只看自己的
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...queryForm
    }
    
    const res = await getFormPage(params)
    tableData.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  fetchData()
}

// 重置
const handleReset = () => {
  queryForm.status = ''
  queryForm.type = ''
  handleSearch()
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/reimbursement/detail/${id}`)
}

// 编辑
const handleEdit = (id) => {
  router.push(`/reimbursement/edit/${id}`)
}

// 提交
const handleSubmit = async (id) => {
  try {
    await ElMessageBox.confirm('确定要提交此报销单吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await submitForm(id)
    ElMessage.success('提交成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败:', error)
    }
  }
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除此报销单吗?', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteForm(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 新建
const goToCreate = () => {
  router.push('/reimbursement/create')
}

// 分页
const handleSizeChange = (val) => {
  pagination.pageSize = val
  fetchData()
}

const handleCurrentChange = (val) => {
  pagination.pageNum = val
  fetchData()
}

// 辅助函数
const getTypeText = (type) => {
  const map = {
    'TRAVEL': '差旅',
    'OFFICE': '办公',
    'COMMUNICATION': '通讯',
    'ENTERTAINMENT': '招待',
    'OTHER': '其他'
  }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = {
    'TRAVEL': '',
    'OFFICE': 'success',
    'COMMUNICATION': 'warning',
    'ENTERTAINMENT': 'danger',
    'OTHER': 'info'
  }
  return map[type] || ''
}

const getStatusText = (status) => {
  const map = {
    'DRAFT': '草稿',
    'PENDING': '待审批',
    'APPROVED': '已通过',
    'REJECTED': '已驳回',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    'DRAFT': 'info',
    'PENDING': 'warning',
    'APPROVED': 'success',
    'REJECTED': 'danger',
    'CANCELLED': 'info'
  }
  return map[status] || ''
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.reimbursement-list {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
