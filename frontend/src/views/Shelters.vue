<template>
  <div class="shelters-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input v-model="searchKeyword" placeholder="搜索车棚名称" prefix-icon="Search" clearable />
        </el-col>
        <el-col :span="8">
          <el-select v-model="statusFilter" placeholder="插座状态" clearable style="width: 100%">
            <el-option label="可用" value="AVAILABLE" />
            <el-option label="已占用" value="OCCUPIED" />
            <el-option label="充电中" value="CHARGING" />
            <el-option label="已预约" value="RESERVED" />
            <el-option label="故障" value="FAULTY" />
          </el-select>
        </el-col>
        <el-col :span="8" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增车棚
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20" class="shelter-list">
      <el-col :span="12" v-for="shelter in filteredShelters" :key="shelter.id">
        <el-card class="shelter-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span class="shelter-name">{{ shelter.name }}</span>
              <span class="shelter-location">
                <el-icon><Location /></el-icon>
                {{ shelter.location }}
              </span>
            </div>
          </template>
          
          <div class="socket-grid">
            <div
              v-for="socket in getShelterSockets(shelter.id)"
              :key="socket.id"
              class="socket-item"
              :class="socket.status.toLowerCase()"
              @click="handleSocketClick(socket)"
            >
              <div class="socket-icon">
                <el-icon><Connection /></el-icon>
              </div>
              <div class="socket-code">{{ socket.socketCode }}</div>
              <div class="socket-status">{{ getSocketStatusName(socket.status) }}</div>
            </div>
          </div>

          <div class="shelter-stats">
            <el-statistic title="总插座" :value="shelter.totalSockets" />
            <el-statistic title="可用" :value="getAvailableCount(shelter.id)" value-color="#67C23A" />
            <el-statistic title="充电中" :value="getChargingCount(shelter.id)" value-color="#E6A23C" />
            <el-statistic title="故障" :value="getFaultyCount(shelter.id)" value-color="#F56C6C" />
          </div>

          <div class="card-actions">
            <el-button size="small" @click="viewShelterDetail(shelter)">查看详情</el-button>
            <el-button size="small" type="primary" @click="addSocket(shelter)">添加插座</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="showSocketDialog" title="插座详情" width="500px">
      <el-descriptions v-if="selectedSocket" :column="1" border>
        <el-descriptions-item label="插座编号">{{ selectedSocket.socketCode }}</el-descriptions-item>
        <el-descriptions-item label="所在车棚">{{ getShelterName(selectedSocket.shelterId) }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ selectedSocket.location }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getSocketStatusTag(selectedSocket.status)">
            {{ getSocketStatusName(selectedSocket.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="功率">{{ selectedSocket.powerRating }} kW</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showSocketDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleReserve">预约充电</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="新增车棚" width="500px">
      <el-form :model="shelterForm" label-width="80px">
        <el-form-item label="车棚名称">
          <el-input v-model="shelterForm.name" placeholder="请输入车棚名称" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="shelterForm.location" placeholder="请输入位置" />
        </el-form-item>
        <el-form-item label="总插座数">
          <el-input-number v-model="shelterForm.totalSockets" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="shelterForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveShelter">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { shelterApi, socketApi, reservationApi } from '../api'

const searchKeyword = ref('')
const statusFilter = ref('')
const shelters = ref([])
const sockets = ref([])
const selectedSocket = ref(null)
const showSocketDialog = ref(false)
const showAddDialog = ref(false)

const shelterForm = reactive({
  name: '',
  location: '',
  totalSockets: 10,
  description: ''
})

const filteredShelters = computed(() => {
  return shelters.value.filter(s => 
    s.name.includes(searchKeyword.value)
  )
})

const getSocketStatusName = (status) => {
  const map = {
    AVAILABLE: '可用',
    OCCUPIED: '已占用',
    CHARGING: '充电中',
    RESERVED: '已预约',
    FAULTY: '故障',
    OFFLINE: '离线'
  }
  return map[status] || status
}

const getSocketStatusTag = (status) => {
  const map = {
    AVAILABLE: 'success',
    OCCUPIED: 'warning',
    CHARGING: 'primary',
    RESERVED: 'info',
    FAULTY: 'danger',
    OFFLINE: 'info'
  }
  return map[status] || 'info'
}

const getShelterSockets = (shelterId) => {
  let result = sockets.value.filter(s => s.shelterId === shelterId)
  if (statusFilter.value) {
    result = result.filter(s => s.status === statusFilter.value)
  }
  return result
}

const getShelterName = (shelterId) => {
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const getAvailableCount = (shelterId) => {
  return sockets.value.filter(s => s.shelterId === shelterId && s.status === 'AVAILABLE').length
}

const getChargingCount = (shelterId) => {
  return sockets.value.filter(s => s.shelterId === shelterId && s.status === 'CHARGING').length
}

const getFaultyCount = (shelterId) => {
  return sockets.value.filter(s => s.shelterId === shelterId && s.status === 'FAULTY').length
}

const loadData = async () => {
  try {
    shelters.value = await shelterApi.findAll()
    sockets.value = await socketApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const handleSocketClick = (socket) => {
  selectedSocket.value = socket
  showSocketDialog.value = true
}

const handleReserve = async () => {
  if (selectedSocket.value && selectedSocket.value.status === 'AVAILABLE') {
    try {
      await reservationApi.create({
        userId: 2,
        socketId: selectedSocket.value.id,
        expectedStartTime: new Date().toISOString(),
        expectedEndTime: new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString()
      })
      ElMessage.success('预约成功')
      showSocketDialog.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  } else {
    ElMessage.warning('该插座不可预约')
  }
}

const viewShelterDetail = (shelter) => {
  ElMessage.info(`查看车棚 ${shelter.name} 详情`)
}

const addSocket = (shelter) => {
  ElMessage.info(`为车棚 ${shelter.name} 添加插座`)
}

const saveShelter = async () => {
  try {
    await shelterApi.save(shelterForm)
    ElMessage.success('车棚创建成功')
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
.shelters-page {
  padding: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.shelter-list {
  margin-bottom: 20px;
}

.shelter-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.shelter-name {
  font-size: 16px;
  font-weight: bold;
}

.shelter-location {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

.socket-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 10px;
  margin: 20px 0;
}

.socket-item {
  text-align: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.socket-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.socket-item.available {
  background: #f0f9eb;
  border-color: #e1f3d8;
  color: #67c23a;
}

.socket-item.occupied {
  background: #fdf6ec;
  border-color: #faecd8;
  color: #e6a23c;
}

.socket-item.charging {
  background: #ecf5ff;
  border-color: #d9ecff;
  color: #409eff;
}

.socket-item.reserved {
  background: #f4f4f5;
  border-color: #e9e9eb;
  color: #909399;
}

.socket-item.faulty {
  background: #fef0f0;
  border-color: #fde2e2;
  color: #f56c6c;
}

.socket-icon {
  font-size: 24px;
  margin-bottom: 5px;
}

.socket-code {
  font-size: 12px;
  font-weight: bold;
}

.socket-status {
  font-size: 10px;
  margin-top: 3px;
}

.shelter-stats {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  margin: 15px 0;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
