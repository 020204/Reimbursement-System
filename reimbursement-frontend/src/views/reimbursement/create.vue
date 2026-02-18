<template>
  <div class="reimbursement-create">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ isEditMode ? '编辑报销单' : '创建报销单' }}</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <!-- 基本信息 -->
        <el-divider content-position="left">基本信息</el-divider>
        
        <el-form-item label="报销标题" prop="title">
          <el-input 
            v-model="form.title" 
            placeholder="请输入报销标题"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="报销类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择报销类型">
            <el-option label="差旅费" value="TRAVEL" />
            <el-option label="办公费" value="OFFICE" />
            <el-option label="通讯费" value="COMMUNICATION" />
            <el-option label="招待费" value="ENTERTAINMENT" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="报销说明" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入报销说明"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 报销明细 -->
        <el-divider content-position="left">
          报销明细
          <el-tag type="info" class="total-amount">
            总金额: ¥{{ totalAmount }}
          </el-tag>
        </el-divider>
        
        <div class="details-section">
          <el-button 
            type="primary" 
            @click="addDetail"
            class="add-detail-btn"
          >
            <el-icon><Plus /></el-icon>
            添加明细
          </el-button>
          
          <el-table :data="form.details" border class="details-table">
            <el-table-column type="index" label="序号" width="60" />
            
            <el-table-column label="费用项目" min-width="150">
              <template #default="{ row }">
                <el-input 
                  v-model="row.itemName" 
                  placeholder="如: 高铁票"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="金额" width="150">
              <template #default="{ row }">
                <el-input-number
                  v-model="row.amount"
                  :precision="2"
                  :step="0.1"
                  :min="0.01"
                  :max="999999.99"
                  controls-position="right"
                  style="width: 100%;"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="发生日期" width="180">
              <template #default="{ row }">
                <el-date-picker
                  v-model="row.occurrenceDate"
                  type="date"
                  placeholder="选择日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  style="width: 100%;"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="说明" min-width="200">
              <template #default="{ row }">
                <el-input 
                  v-model="row.description" 
                  placeholder="费用说明"
                />
              </template>
            </el-table-column>
            
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ $index }">
                <el-button 
                  type="danger" 
                  link 
                  @click="removeDetail($index)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty 
            v-if="form.details.length === 0" 
            description="暂无明细,请点击上方按钮添加"
            :image-size="100"
          />
        </div>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">
            {{ isEditMode ? '保存修改' : '保存草稿' }}
          </el-button>
          <el-button 
            v-if="!isEditMode" 
            type="success" 
            @click="handleSubmit" 
            :loading="submitting"
          >
            提交审批
          </el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createForm, updateForm, getFormById } from '@/api/reimbursement'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 判断是否为编辑模式
const isEditMode = computed(() => route.name === 'ReimbursementEdit')
const formId = computed(() => route.params.id)

const formRef = ref(null)
const saving = ref(false)
const submitting = ref(false)

const form = reactive({
  employeeId: userStore.userInfo.id,
  title: '',
  type: '',
  description: '',
  details: []
})

const rules = {
  title: [
    { required: true, message: '请输入报销标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择报销类型', trigger: 'change' }
  ]
}

// 计算总金额
const totalAmount = computed(() => {
  return form.details.reduce((sum, item) => {
    return sum + (Number(item.amount) || 0)
  }, 0).toFixed(2)
})

// 添加明细
const addDetail = () => {
  form.details.push({
    itemName: '',
    amount: 0,
    occurrenceDate: '',
    description: ''
  })
}

// 删除明细
const removeDetail = (index) => {
  form.details.splice(index, 1)
}

// 加载报销单数据（编辑模式）
const loadFormData = async () => {
  if (!isEditMode.value || !formId.value) return
  
  try {
    const res = await getFormById(formId.value)
    const data = res.data
    
    // 填充表单数据
    form.id = data.id
    form.employeeId = data.employeeId
    form.title = data.title
    form.type = data.type
    form.description = data.description
    form.attachment = data.attachment
    
    // 填充明细数据（需要转换日期格式）
    if (data.details && data.details.length > 0) {
      form.details = data.details.map(d => ({
        ...d,
        occurrenceDate: d.occurrenceDate ? d.occurrenceDate.split('T')[0] : ''
      }))
    }
  } catch (error) {
    console.error('加载报销单失败:', error)
    ElMessage.error('加载报销单失败')
    router.push('/reimbursement/list')
  }
}

// 保存草稿
const handleSave = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.details.length === 0) {
        ElMessage.warning('请至少添加一条报销明细')
        return
      }
      
      // 验证明细
      const invalidDetail = form.details.find(d => !d.itemName || !d.amount || !d.occurrenceDate)
      if (invalidDetail) {
        ElMessage.warning('请完善报销明细信息')
        return
      }
      
      saving.value = true
      try {
        if (isEditMode.value) {
          await updateForm(form)
          ElMessage.success('修改成功')
        } else {
          await createForm(form)
          ElMessage.success('保存成功')
        }
        router.push('/reimbursement/list')
      } catch (error) {
        console.error('保存失败:', error)
      } finally {
        saving.value = false
      }
    }
  })
}

// 页面加载时如果是编辑模式则加载数据
onMounted(() => {
  loadFormData()
})

// 提交审批
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.details.length === 0) {
        ElMessage.warning('请至少添加一条报销明细')
        return
      }
      
      // 验证明细
      const invalidDetail = form.details.find(d => !d.itemName || !d.amount || !d.occurrenceDate)
      if (invalidDetail) {
        ElMessage.warning('请完善报销明细信息')
        return
      }
      
      submitting.value = true
      try {
        // 先创建,后端会自动提交
        await createForm(form)
        ElMessage.success('提交成功,请等待审批')
        router.push('/reimbursement/list')
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitting.value = false
      }
    }
  })
}

// 返回
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.reimbursement-create {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.total-amount {
  margin-left: 10px;
  font-size: 14px;
  font-weight: bold;
}

.details-section {
  margin: 20px 0;
}

.add-detail-btn {
  margin-bottom: 15px;
}

.details-table {
  margin-top: 10px;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
}
</style>
