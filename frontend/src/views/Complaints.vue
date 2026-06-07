<template>
  <div class="complaints-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="处理状态" clearable style="width: 100%">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已解决" value="RESOLVED" />
          </el-select>
        </el-col>
        <el-col :span="18" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="warning" @click="showAddDialog = true">
            <el-icon><ChatDotRound /></el-icon>
            提交投诉
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredComplaints" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="content" label="内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="shelterName" label="车棚" width="120">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提交时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" v-if="row.status === 'PENDING'" @click="handleComplaint(row)">
              处理
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showHandleDialog" title="处理投诉" width="500px">
      <el-form label-width="80px">
        <el-form-item label="处理结果">
          <el-input v-model="handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showHandleDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmHandle">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="提交投诉" width="500px">
      <el-form :model="complaintForm" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="complaintForm.title" placeholder="请输入投诉标题" />
        </el-form-item>
        <el-form-item label="车棚">
          <el-select v-model="complaintForm.shelterId" placeholder="请选择车棚" style="width: 100%">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="complaintForm.content" type="textarea" :rows="4" placeholder="请详细描述投诉内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveComplaint">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { complaintApi, shelterApi } from '../api'

const statusFilter = ref('')
const complaints = ref([])
const shelters = ref([])
const showHandleDialog = ref(false)
const showAddDialog = ref(false)
const selectedComplaint = ref(null)
const handleResult = ref('')

const complaintForm = reactive({
  userId: 2,
  shelterId: null,
  title: '',
  content: ''
})

const filteredComplaints = computed(() => {
  return complaints.value.filter(c => !statusFilter.value || c.status === statusFilter.value)
})

const getStatusName = (status) => {
  const map = { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已解决' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { PENDING: 'warning', PROCESSING: 'primary', RESOLVED: 'success' }
  return map[status] || 'info'
}

const getShelterName = (shelterId) => {
  if (!shelterId) return '-'
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const loadData = async () => {
  try {
    complaints.value = await complaintApi.findAll()
    shelters.value = await shelterApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const handleComplaint = (row) => {
  selectedComplaint.value = row
  handleResult.value = ''
  showHandleDialog.value = true
}

const confirmHandle = async () => {
  if (selectedComplaint.value) {
    try {
      await complaintApi.handle(selectedComplaint.value.id, {
        handlerId: 1,
        handleResult: handleResult.value
      })
      ElMessage.success('投诉已处理')
      showHandleDialog.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  }
}

const viewDetail = (row) => {
  ElMessage.info(row.content)
}

const saveComplaint = async () => {
  try {
    await complaintApi.create(complaintForm)
    ElMessage.success('投诉已提交')
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
.complaints-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}
</style>
