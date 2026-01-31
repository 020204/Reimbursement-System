<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon approved">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.approved }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon rejected">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.rejected }}</div>
              <div class="stat-label">已驳回</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">总报销单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近的报销单 -->
    <el-card class="recent-forms" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>最近的报销单</span>
          <el-button type="primary" link @click="goToList">
            查看全部 <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </template>
      
      <el-table :data="recentForms" v-loading="loading">
        <el-table-column prop="formNo" label="报销单号" width="150" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getFormList } from '@/api/reimbursement'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const recentForms = ref([])

const stats = ref({
  pending: 0,
  approved: 0,
  rejected: 0,
  total: 0
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getFormList()
    const forms = res.data || []
    
    // 只显示当前用户的报销单
    const myForms = forms.filter(f => f.employeeId === userStore.userInfo.id)
    
    // 统计数据
    stats.value.total = myForms.length
    stats.value.pending = myForms.filter(f => f.status === 'PENDING').length
    stats.value.approved = myForms.filter(f => f.status === 'APPROVED').length
    stats.value.rejected = myForms.filter(f => f.status === 'REJECTED').length
    
    // 最近5条
    recentForms.value = myForms.slice(0, 5)
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

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

const goToList = () => {
  router.push('/reimbursement/list')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #fbc02d 0%, #f9a825 100%);
}

.stat-icon.approved {
  background: linear-gradient(135deg, #66bb6a 0%, #43a047 100%);
}

.stat-icon.rejected {
  background: linear-gradient(135deg, #ef5350 0%, #e53935 100%);
}

.stat-icon.total {
  background: linear-gradient(135deg, #42a5f5 0%, #1e88e5 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.recent-forms {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
