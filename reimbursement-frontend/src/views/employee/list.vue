<template>
  <div class="employee-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>员工管理</span>
          <el-button type="primary" @click="handleAdd" v-if="canManageEmployee">
            <el-icon><Plus /></el-icon>
            新增员工
          </el-button>
        </div>
      </template>
      
      <!-- 搜索 -->
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable />
        </el-form-item>
        
        <el-form-item label="部门">
          <el-input v-model="queryForm.department" placeholder="请输入部门" clearable />
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
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe v-if="canViewEmployee">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="department" label="部门" width="100" />
        <el-table-column prop="position" label="职位" width="100" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hireDate" label="入职时间" width="240" />
        <el-table-column prop="updateTime" label="修改时间" width="240" />
        <el-table-column label="操作" width="160" fixed="right" v-if="canManageEmployee">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 无权限提示 -->
      <el-empty v-else description="您没有权限访问员工管理" />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑员工' : '新增员工'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="formData.username" placeholder="请输入用户名" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password" v-if="!isEdit">
              <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="departmentId">
              <el-select v-model="formData.departmentId" placeholder="请选择部门" style="width: 100%">
                <el-option
                  v-for="dept in departmentList"
                  :key="dept.id"
                  :label="dept.name"
                  :value="dept.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-input v-model="formData.position" placeholder="请输入职位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="在职" :value="1" />
                <el-option label="离职" :value="0" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="formData.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入职时间" prop="hireDate">
              <el-date-picker
                v-model="formData.hireDate"
                type="date"
                placeholder="请选择入职时间"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { searchEmployees, addEmployee, updateEmployee, deleteEmployee } from '@/api/employee'
import { getDepartmentList } from '@/api/department'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const tableData = ref([])
const departmentList = ref([])
const formRef = ref(null)

// 是否可查看员工管理（仅 ADMIN）
const canViewEmployee = computed(() => {
  return userStore.isAdmin
})

// 是否可管理员工（新增、编辑、删除）- 仅 ADMIN
const canManageEmployee = computed(() => {
  return userStore.isAdmin
})

const queryForm = reactive({
  name: '',
  department: ''
})

const formData = reactive({
  id: null,
  username: '',
  password: '',
  name: '',
  departmentId: null,
  department: '',
  position: '',
  email: '',
  phone: '',
  status: 1,
  hireDate: null
})

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await searchEmployees(queryForm)
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const res = await getDepartmentList()
    departmentList.value = res.data || []
  } catch (error) {
    console.error('获取部门列表失败:', error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  formData.id = null
  formData.username = ''
  formData.password = ''
  formData.name = ''
  formData.departmentId = null
  formData.department = ''
  formData.position = ''
  formData.email = ''
  formData.phone = ''
  formData.status = 1
  formData.hireDate = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  formData.id = row.id
  formData.username = row.username
  formData.name = row.name
  formData.departmentId = row.departmentId
  formData.department = row.department
  formData.position = row.position
  formData.email = row.email
  formData.phone = row.phone
  formData.status = row.status
  formData.hireDate = row.hireDate
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  // 设置部门名称
  const dept = departmentList.value.find(d => d.id === formData.departmentId)
  if (dept) {
    formData.department = dept.name
  }

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateEmployee(formData)
      ElMessage.success('更新成功')
    } else {
      await addEmployee(formData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
      type: 'warning'
    })
    await deleteEmployee(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}

const handleSearch = () => {
  fetchData()
}

const handleReset = () => {
  queryForm.name = ''
  queryForm.department = ''
  fetchData()
}

onMounted(() => {
  fetchData()
  fetchDepartments()
})
</script>

<style scoped>
.employee-list {
  padding: 20px;
}

.card-header {
  display: flex;            /* 啟用 Flex 佈局 */
  justify-content: space-between; /* 關鍵：讓子元素分居左右兩端 */
  align-items: center;      /* 垂直居中 */
  width: 100%;             /* 確保容器寬度為 100% */
}


.search-form {
  margin-bottom: 20px;
}
</style>
