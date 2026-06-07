<template>
  <div class="reservations-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="预约状态" clearable style="width: 100%">
            <el-option label="待确认" value="PENDING" />
            <el-option label="已确认" value="CONFIRMED" />
            <el-option label="已入棚" value="CHECKED_IN" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="已过期" value="EXPIRED" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增预约
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="reservations" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="socketCode" label="插座编号" width="120">
          <template #default="{ row }">
            {{ getSocketCode(row.socketId) }}
          </template>
        </el-table-column>
        <el-table-column prop="shelterName" label="车棚" width="150">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedStartTime" label="预计开始" width="160" />
        <el-table-column prop="expectedEndTime" label="预计结束" width="160" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-if="row.status === 'CONFIRMED'" @click="handleCheckIn(row)">
              确认入棚
            </el-button>
            <el-button size="small" type="warning" v-if="row.status === 'CONFIRMED' || row.status === 'PENDING'" @click="handleCancel(row)">
              取消预约
            </el-button>
            <el-button size="small" type="success" v-if="row.status === 'CHECKED_IN'" @click="handleStartCharging(row)">
              开始充电
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="reservations.length"
        :page-size="10"
      />
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增预约" width="500px">
      <el-form :model="reservationForm" label-width="100px">
        <el-form-item label="选择车棚">
          <el-select v-model="reservationForm.shelterId" placeholder="请选择车棚" style="width: 100%" @change="onShelterChange">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择插座">
          <el-select v-model="reservationForm.socketId" placeholder="请选择插座" style="width: 100%">
            <el-option v-for="socket in availableSockets" :key="socket.id" :label="socket.socketCode" :value="socket.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="reservationForm.expectedStartTime" type="datetime" placeholder="选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="reservationForm.expectedEndTime" type="datetime" placeholder="选择结束时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveReservation">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { reservationApi, shelterApi, socketApi, orderApi } from '../api'

const router = useRouter()
const statusFilter = ref('')
const dateRange = ref([])
const reservations = ref([])
const shelters = ref([])
const sockets = ref([])
const availableSockets = ref([])
const showAddDialog = ref(false)

const reservationForm = reactive({
  userId: 2,
  shelterId: null,
  socketId: null,
  expectedStartTime: null,
  expectedEndTime: null
})

const getStatusName = (status) => {
  const map = {
    PENDING: '待确认',
    CONFIRMED: '已确认',
    CHECKED_IN: '已入棚',
    CANCELLED: '已取消',
    EXPIRED: '已过期',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    PENDING: 'warning',
    CONFIRMED: 'primary',
    CHECKED_IN: 'success',
    CANCELLED: 'info',
    EXPIRED: 'danger',
    COMPLETED: 'success'
  }
  return map[status] || 'info'
}

const getSocketCode = (socketId) => {
  const socket = sockets.value.find(s => s.id === socketId)
  return socket ? socket.socketCode : '-'
}

const getShelterName = (shelterId) => {
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const loadData = async () => {
  try {
    reservations.value = await reservationApi.findAll()
    shelters.value = await shelterApi.findAll()
    sockets.value = await socketApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const onShelterChange = (shelterId) => {
  availableSockets.value = sockets.value.filter(
    s => s.shelterId === shelterId && s.status === 'AVAILABLE'
  )
}

const handleCheckIn = async (row) => {
  try {
    await reservationApi.checkIn(row.id)
    ElMessage.success('入棚确认成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消该预约吗？', '提示', { type: 'warning' })
    await reservationApi.cancel(row.id)
    ElMessage.success('预约已取消')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleStartCharging = async (row) => {
  try {
    await orderApi.startCharging({
      userId: row.userId,
      socketId: row.socketId,
      reservationId: row.id
    })
    ElMessage.success('开始充电')
    router.push('/orders')
  } catch (error) {
    console.error(error)
  }
}

const saveReservation = async () => {
  try {
    await reservationApi.create(reservationForm)
    ElMessage.success('预约成功')
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
.reservations-page {
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
