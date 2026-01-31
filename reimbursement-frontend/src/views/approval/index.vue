<template>
  <div class="approval-center">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>审批中心</span>
          <el-tag type="warning">待审批: {{ pendingCount }}</el-tag>
        </div>
      </template>
      
      <!-- 工具栏 -->
      <div class="toolbar">
        <el-button 
          type="success" 
          :disabled="selectedRows.length === 0"
          @click="handleBatchApprove('APPROVED')"
        >
          <el-icon><Check /></el-icon>
          批量通过
        </el-button>
        
        <el-button 
          type="danger" 
          :disabled="selectedRows.length === 0"
          @click="handleBatchApprove('REJECTED')"
        >
          <el-icon><Close /></el-icon>
          批量驳回
        </el-button>
        
        <span class="selected-info" v-if="selectedRows.length > 0">
          已选择 {{ selectedRows.length }} 项
        </span>
      </div>
      
      <!-- 表格 -->
      <el-table 
        :data="tableData" 
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="formNo" label="报销单号" width="150" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="employeeName" label="申请人" width="100" />
        
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
        
        <el-table-column prop="submitTime" label="提交时间" width="160" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              link 
              @click="viewDetail(row.id)"
            >
              查看
            </el-button>
            
            <el-button 
              type="success" 
              link 
              @click="handleApprove(row, 'APPROVED')"
            >
              通过
            </el-button>
            
            <el-button 
              type="danger" 
              link 
              @click="handleApprove(row, 'REJECTED')"
            >
              驳回
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
    
    <!-- 审批对话框 -->
    <el-dialog
      v-model="approvalDialog.visible"
      :title="approvalDialog.title"
      width="500px"
    >
      <el-form :model="approvalDialog.form" label-width="80px">
        <el-form-item label="审批意见">
          <el-input
            v-model="approvalDialog.form.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入审批意见(可选)"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="approvalDialog.visible = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="confirmApproval"
          :loading="approvalDialog.loading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFormPage, approveForm, batchApprove } from '@/api/reimbursement'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const approvalDialog = reactive({
  visible: false,
  title: '',
  loading: false,
  form: {
    ids: [],
    result: '',
    comment: ''
  }
})

// 待审批数量
const pendingCount = computed(() => pagination.total)

// 获取数据
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      status: 'PENDING' // 只查询待审批的
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

// 选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 查看详情
const viewDetail = (id) => {
  router.push(`/reimbursement/detail/${id}`)
}

// 单个审批
const handleApprove = (row, result) => {
  approvalDialog.visible = true
  approvalDialog.title = result === 'APPROVED' ? '通过审批' : '驳回审批'
  approvalDialog.form.ids = [row.id]
  approvalDialog.form.result = result
  approvalDialog.form.comment = ''
}

// 批量审批
const handleBatchApprove = (result) => {
  approvalDialog.visible = true
  approvalDialog.title = result === 'APPROVED' ? '批量通过' : '批量驳回'
  approvalDialog.form.ids = selectedRows.value.map(row => row.id)
  approvalDialog.form.result = result
  approvalDialog.form.comment = ''
}

// 确认审批
const confirmApproval = async () => {
  approvalDialog.loading = true
  try {
    const isBatch = approvalDialog.form.ids.length > 1
    
    const data = {
      approverId: userStore.userInfo.id,
      result: approvalDialog.form.result,
      comment: approvalDialog.form.comment
    }
    
    if (isBatch) {
      // 批量审批
      await batchApprove({
        ids: approvalDialog.form.ids,
        ...data
      })
      ElMessage.success(`批量${approvalDialog.form.result === 'APPROVED' ? '通过' : '驳回'}成功`)
    } else {
      // 单个审批
      await approveForm({
        id: approvalDialog.form.ids[0],
        ...data
      })
      ElMessage.success(`${approvalDialog.form.result === 'APPROVED' ? '通过' : '驳回'}成功`)
    }
    
    approvalDialog.visible = false
    selectedRows.value = []
    fetchData()
  } catch (error) {
    console.error('审批失败:', error)
  } finally {
    approvalDialog.loading = false
  }
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

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.approval-center {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.toolbar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.selected-info {
  color: #409eff;
  font-size: 14px;
  margin-left: 10px;
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
