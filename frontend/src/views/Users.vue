<template>
  <div class="users-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="roleFilter" placeholder="用户角色" clearable style="width: 100%">
            <el-option label="业主" value="OWNER" />
            <el-option label="物业" value="PROPERTY" />
            <el-option label="运维商" value="OPERATOR" />
            <el-option label="安全员" value="SAFETY_OFFICER" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="keywordFilter" placeholder="搜索用户名/姓名" clearable />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredUsers" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="role" label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTag(row.role)">{{ getRoleName(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="150" show-overflow-tooltip />
        <el-table-column prop="carNumber" label="车牌号" width="120" />
        <el-table-column prop="isBlacklisted" label="黑名单" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isBlacklisted ? 'danger' : 'success'">
              {{ row.isBlacklisted ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.isBlacklisted ? 'success' : 'warning'" @click="toggleBlacklist(row)">
              {{ row.isBlacklisted ? '移出黑名单' : '加入黑名单' }}
            </el-button>
            <el-button size="small" @click="editUser(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增用户" width="500px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="userForm.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="userForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="业主" value="OWNER" />
            <el-option label="物业" value="PROPERTY" />
            <el-option label="运维商" value="OPERATOR" />
            <el-option label="安全员" value="SAFETY_OFFICER" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="userForm.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="车牌号">
          <el-input v-model="userForm.carNumber" placeholder="请输入车牌号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '../api'

const roleFilter = ref('')
const keywordFilter = ref('')
const users = ref([])
const showAddDialog = ref(false)

const userForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  role: 'OWNER',
  address: '',
  carNumber: ''
})

const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchRole = !roleFilter.value || user.role === roleFilter.value
    const matchKeyword = !keywordFilter.value ||
      user.username.includes(keywordFilter.value) ||
      user.realName.includes(keywordFilter.value)
    return matchRole && matchKeyword
  })
})

const getRoleName = (role) => {
  const map = {
    OWNER: '业主',
    PROPERTY: '物业',
    OPERATOR: '运维商',
    SAFETY_OFFICER: '安全员'
  }
  return map[role] || role
}

const getRoleTag = (role) => {
  const map = {
    OWNER: '',
    PROPERTY: 'primary',
    OPERATOR: 'success',
    SAFETY_OFFICER: 'warning'
  }
  return map[role] || 'info'
}

const loadData = async () => {
  try {
    users.value = await userApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const toggleBlacklist = async (row) => {
  try {
    const action = row.isBlacklisted ? '移出' : '加入'
    await ElMessageBox.confirm(`确定要将该用户${action}黑名单吗？`, '提示', { type: 'warning' })
    await userApi.setBlacklist(row.id, { isBlacklisted: !row.isBlacklisted })
    ElMessage.success(`已${action}黑名单`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const editUser = (row) => {
  ElMessage.info('编辑用户功能')
}

const saveUser = async () => {
  try {
    await userApi.save(userForm)
    ElMessage.success('用户创建成功')
    showAddDialog.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.users-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}
</style>
