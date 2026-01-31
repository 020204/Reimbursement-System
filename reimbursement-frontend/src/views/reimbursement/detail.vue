<template>
  <div class="reimbursement-detail">
    <el-card shadow="hover" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>报销单详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>
      
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="报销单号">
          {{ detail.formNo }}
        </el-descriptions-item>
        
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(detail.status)">
            {{ getStatusText(detail.status) }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="报销标题">
          {{ detail.title }}
        </el-descriptions-item>
        
        <el-descriptions-item label="报销类型">
          <el-tag :type="getTypeTag(detail.type)">
            {{ getTypeText(detail.type) }}
          </el-tag>
        </el-descriptions-item>
        
        <el-descriptions-item label="申请人">
          {{ detail.employeeName }}
        </el-descriptions-item>
        
        <el-descriptions-item label="总金额">
          <span class="amount">¥{{ detail.amount }}</span>
        </el-descriptions-item>
        
        <el-descriptions-item label="创建时间">
          {{ detail.createTime }}
        </el-descriptions-item>
        
        <el-descriptions-item label="提交时间">
          {{ detail.submitTime || '-' }}
        </el-descriptions-item>
        
        <el-descriptions-item label="报销说明" :span="2">
          {{ detail.description || '-' }}
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 报销明细 -->
      <el-divider content-position="left">报销明细</el-divider>
      <el-table :data="details" border>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="itemName" label="费用项目" />
        <el-table-column prop="amount" label="金额" width="120" align="right">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="occurrenceDate" label="发生日期" width="120" />
        <el-table-column prop="description" label="说明" />
      </el-table>
      
      <!-- 审批记录 -->
      <el-divider content-position="left">审批记录</el-divider>
      <el-timeline v-if="approvalRecords.length > 0">
        <el-timeline-item
          v-for="record in approvalRecords"
          :key="record.id"
          :timestamp="record.approvalTime"
          placement="top"
        >
          <el-card>
            <h4>
              {{ record.approverName }} 
              <el-tag 
                :type="record.result === 'APPROVED' ? 'success' : 'danger'"
                size="small"
              >
                {{ record.result === 'APPROVED' ? '通过' : '驳回' }}
              </el-tag>
            </h4>
            <p v-if="record.comment">审批意见: {{ record.comment }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审批记录" :image-size="80" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getFormById } from '@/api/reimbursement'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref(null)
const details = ref([])
const approvalRecords = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getFormById(route.params.id)
    detail.value = res.data
    
    // 这里需要后端返回明细和审批记录
    // 暂时模拟数据
    details.value = []
    approvalRecords.value = []
  } catch (error) {
    console.error('获取详情失败:', error)
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

const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.reimbursement-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.amount {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

:deep(.el-descriptions__label) {
  font-weight: 600;
}
</style>
