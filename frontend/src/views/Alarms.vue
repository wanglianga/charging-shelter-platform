<template>
  <div class="alarms-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="typeFilter" placeholder="告警类型" clearable style="width: 100%">
            <el-option label="插座跳闸" value="SOCKET_TRIP" />
            <el-option label="超时占位" value="OVERTIME_OCCUPANCY" />
            <el-option label="消防通道堵塞" value="FIRE_EXIT_BLOCKED" />
            <el-option label="飞线充电" value="FLYING_WIRE_CHARGING" />
            <el-option label="烟感报警" value="SMOKE_ALARM" />
            <el-option label="业主投诉" value="OWNER_COMPLAINT" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="处理状态" clearable style="width: 100%">
            <el-option label="待处理" value="PENDING" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="已解决" value="RESOLVED" />
            <el-option label="已忽略" value="DISMISSED" />
          </el-select>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="danger" @click="showAddDialog = true">
            <el-icon><Warning /></el-icon>
            上报告警
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredAlarms" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" label="告警类型" width="130">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shelterName" label="车棚" width="130">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="socketId" label="插座" width="80">
          <template #default="{ row }">
            {{ row.socketId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="createdAt" label="创建时间" width="160" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-if="row.status === 'PENDING'" @click="handleProcess(row)">
              开始处理
            </el-button>
            <el-button size="small" type="success" v-if="row.status === 'PROCESSING'" @click="handleResolve(row)">
              解决
            </el-button>
            <el-button size="small" type="info" v-if="row.status === 'PENDING' || row.status === 'PROCESSING'" @click="handleDismiss(row)">
              忽略
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredAlarms.length"
        :page-size="10"
      />
    </el-card>

    <el-dialog v-model="showResolveDialog" title="处理告警" width="500px">
      <el-form label-width="80px">
        <el-form-item label="处理结果">
          <el-input v-model="handleResult" type="textarea" :rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showResolveDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmResolve">确认解决</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="上报告警" width="500px">
      <el-form :model="alarmForm" label-width="100px">
        <el-form-item label="告警类型">
          <el-select v-model="alarmForm.type" placeholder="请选择告警类型" style="width: 100%">
            <el-option label="插座跳闸" value="SOCKET_TRIP" />
            <el-option label="超时占位" value="OVERTIME_OCCUPANCY" />
            <el-option label="消防通道堵塞" value="FIRE_EXIT_BLOCKED" />
            <el-option label="飞线充电" value="FLYING_WIRE_CHARGING" />
            <el-option label="烟感报警" value="SMOKE_ALARM" />
            <el-option label="业主投诉" value="OWNER_COMPLAINT" />
          </el-select>
        </el-form-item>
        <el-form-item label="车棚">
          <el-select v-model="alarmForm.shelterId" placeholder="请选择车棚" style="width: 100%">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="alarmForm.location" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="alarmForm.description" type="textarea" :rows="3" placeholder="请输入告警描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAlarm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { alarmApi, shelterApi } from '../api'

const typeFilter = ref('')
const statusFilter = ref('')
const alarms = ref([])
const shelters = ref([])
const showResolveDialog = ref(false)
const showAddDialog = ref(false)
const selectedAlarm = ref(null)
const handleResult = ref('')

const alarmForm = reactive({
  type: '',
  shelterId: null,
  location: '',
  description: '',
  reporterId: 5
})

const filteredAlarms = computed(() => {
  return alarms.value.filter(alarm => {
    const matchType = !typeFilter.value || alarm.type === typeFilter.value
    const matchStatus = !statusFilter.value || alarm.status === statusFilter.value
    return matchType && matchStatus
  })
})

const getTypeName = (type) => {
  const map = {
    SOCKET_TRIP: '插座跳闸',
    OVERTIME_OCCUPANCY: '超时占位',
    FIRE_EXIT_BLOCKED: '消防通道堵塞',
    FLYING_WIRE_CHARGING: '飞线充电',
    SMOKE_ALARM: '烟感报警',
    OWNER_COMPLAINT: '业主投诉'
  }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = {
    SOCKET_TRIP: 'warning',
    OVERTIME_OCCUPANCY: 'warning',
    FIRE_EXIT_BLOCKED: 'danger',
    FLYING_WIRE_CHARGING: 'danger',
    SMOKE_ALARM: 'danger',
    OWNER_COMPLAINT: 'info'
  }
  return map[type] || 'info'
}

const getStatusName = (status) => {
  const map = {
    PENDING: '待处理',
    PROCESSING: '处理中',
    RESOLVED: '已解决',
    DISMISSED: '已忽略'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    PENDING: 'warning',
    PROCESSING: 'primary',
    RESOLVED: 'success',
    DISMISSED: 'info'
  }
  return map[status] || 'info'
}

const getShelterName = (shelterId) => {
  if (!shelterId) return '-'
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const loadData = async () => {
  try {
    alarms.value = await alarmApi.findAll()
    shelters.value = await shelterApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const handleProcess = async (row) => {
  try {
    await alarmApi.process(row.id, { handlerId: 1 })
    ElMessage.success('已开始处理')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleResolve = (row) => {
  selectedAlarm.value = row
  handleResult.value = ''
  showResolveDialog.value = true
}

const handleDismiss = async (row) => {
  try {
    await ElMessageBox.confirm('确定要忽略该告警吗？', '提示', { type: 'warning' })
    await alarmApi.dismiss(row.id, { handleResult: '经核查，无需处理' })
    ElMessage.success('已忽略')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const confirmResolve = async () => {
  if (selectedAlarm.value) {
    try {
      await alarmApi.resolve(selectedAlarm.value.id, { handleResult: handleResult.value })
      ElMessage.success('告警已解决')
      showResolveDialog.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  }
}

const viewDetail = (row) => {
  ElMessageBox.alert(row.description, '告警详情', {
    confirmButtonText: '确定'
  })
}

const saveAlarm = async () => {
  try {
    await alarmApi.create(alarmForm)
    ElMessage.success('告警已上报')
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
.alarms-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
  display: flex;
}
</style>
