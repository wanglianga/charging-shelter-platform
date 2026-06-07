<template>
  <div class="maintenance-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 100%">
            <el-option label="待维修" :value="false" />
            <el-option label="已完成" :value="true" />
          </el-select>
        </el-col>
        <el-col :span="18" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            上报故障
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredRecords" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="socketId" label="插座ID" width="100" />
        <el-table-column prop="shelterName" label="车棚" width="130">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operatorId" label="运维商ID" width="100" />
        <el-table-column prop="isCompleted" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isCompleted ? 'success' : 'warning'">
              {{ row.isCompleted ? '已完成' : '待维修' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ row.endTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceCost" label="费用(元)" width="100">
          <template #default="{ row }">
            {{ row.maintenanceCost || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" v-if="!row.isCompleted" @click="handleComplete(row)">
              完成维修
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCompleteDialog" title="完成维修" width="500px">
      <el-form label-width="80px">
        <el-form-item label="维修费用">
          <el-input-number v-model="maintenanceCost" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="remark" type="textarea" :rows="3" placeholder="请输入维修备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCompleteDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">确认完成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="上报故障" width="500px">
      <el-form :model="maintenanceForm" label-width="100px">
        <el-form-item label="车棚">
          <el-select v-model="maintenanceForm.shelterId" placeholder="请选择车棚" style="width: 100%" @change="onShelterChange">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="插座">
          <el-select v-model="maintenanceForm.socketId" placeholder="请选择插座" style="width: 100%">
            <el-option v-for="socket in shelterSockets" :key="socket.id" :label="socket.socketCode" :value="socket.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述">
          <el-input v-model="maintenanceForm.faultDescription" type="textarea" :rows="3" placeholder="请描述故障情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveMaintenance">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { maintenanceApi, shelterApi, socketApi } from '../api'

const statusFilter = ref(null)
const records = ref([])
const shelters = ref([])
const sockets = ref([])
const shelterSockets = ref([])
const showCompleteDialog = ref(false)
const showAddDialog = ref(false)
const selectedRecord = ref(null)
const maintenanceCost = ref(0)
const remark = ref('')

const maintenanceForm = reactive({
  socketId: null,
  shelterId: null,
  operatorId: 4,
  faultDescription: ''
})

const filteredRecords = computed(() => {
  return records.value.filter(r => statusFilter.value === null || r.isCompleted === statusFilter.value)
})

const getShelterName = (shelterId) => {
  if (!shelterId) return '-'
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const loadData = async () => {
  try {
    records.value = await maintenanceApi.findAll()
    shelters.value = await shelterApi.findAll()
    sockets.value = await socketApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const onShelterChange = (shelterId) => {
  shelterSockets.value = sockets.value.filter(s => s.shelterId === shelterId)
}

const handleComplete = (row) => {
  selectedRecord.value = row
  maintenanceCost.value = 0
  remark.value = ''
  showCompleteDialog.value = true
}

const confirmComplete = async () => {
  if (selectedRecord.value) {
    try {
      await maintenanceApi.complete(selectedRecord.value.id, { remark: remark.value })
      ElMessage.success('维修已完成')
      showCompleteDialog.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  }
}

const viewDetail = (row) => {
  ElMessage.info(row.faultDescription)
}

const saveMaintenance = async () => {
  try {
    await maintenanceApi.create(maintenanceForm)
    ElMessage.success('故障已上报')
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
.maintenance-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}
</style>
