<template>
  <div class="orders-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="订单状态" clearable style="width: 100%">
            <el-option label="待充电" value="PENDING" />
            <el-option label="充电中" value="CHARGING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
            <el-option label="超时" value="OVERTIME" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="orderNoFilter" placeholder="订单号搜索" clearable />
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showStartDialog = true">
            <el-icon><Plus /></el-icon>
            开始充电
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredOrders" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="socketCode" label="插座" width="100">
          <template #default="{ row }">
            {{ getSocketCode(row.socketId) }}
          </template>
        </el-table-column>
        <el-table-column prop="shelterName" label="车棚" width="130">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)">{{ getStatusName(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" />
        <el-table-column prop="endTime" label="结束时间" width="160" />
        <el-table-column prop="chargedKwh" label="充电量(kWh)" width="110" />
        <el-table-column prop="batteryType" label="电池类型" width="110">
          <template #default="{ row }">
            {{ getBatteryTypeName(row.batteryType) }}
          </template>
        </el-table-column>
        <el-table-column prop="currentTemperature" label="温度(°C)" width="100">
          <template #default="{ row }">
            <span v-if="row.currentTemperature" :style="{ color: row.currentTemperature >= 50 ? '#f56c6c' : row.currentTemperature >= 40 ? '#e6a23c' : '#67c23a', fontWeight: 'bold' }">
              {{ row.currentTemperature.toFixed(1) }}
            </span>
            <span v-else style="color: #909399">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="费用(元)" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">¥{{ row.totalAmount || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="warning" v-if="row.status === 'CHARGING'" @click="handleStop(row)">
              结束充电
            </el-button>
            <el-button size="small" type="success" v-if="row.status === 'COMPLETED' || row.status === 'OVERTIME'" @click="handlePay(row)">
              支付
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredOrders.length"
        :page-size="10"
      />
    </el-card>

    <el-dialog v-model="showStartDialog" title="开始充电" width="500px">
      <el-form :model="startForm" label-width="100px">
        <el-form-item label="用户ID">
          <el-input-number v-model="startForm.userId" :min="1" />
        </el-form-item>
        <el-form-item label="选择车棚">
          <el-select v-model="startForm.shelterId" placeholder="请选择车棚" style="width: 100%" @change="onShelterChange">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择插座">
          <el-select v-model="startForm.socketId" placeholder="请选择插座" style="width: 100%">
            <el-option v-for="socket in availableSockets" :key="socket.id" :label="socket.socketCode" :value="socket.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showStartDialog = false">取消</el-button>
        <el-button type="primary" @click="startCharging">确认开始</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showDetailDialog" title="订单详情" width="600px">
      <el-descriptions v-if="selectedOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTag(selectedOrder.status)">{{ getStatusName(selectedOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="插座">{{ getSocketCode(selectedOrder.socketId) }}</el-descriptions-item>
        <el-descriptions-item label="车棚">{{ getShelterName(selectedOrder.shelterId) }}</el-descriptions-item>
        <el-descriptions-item label="充电量">{{ selectedOrder.chargedKwh || '-' }} kWh</el-descriptions-item>
        <el-descriptions-item label="单价">¥{{ selectedOrder.unitPrice }} / kWh</el-descriptions-item>
        <el-descriptions-item label="基础费">¥{{ selectedOrder.baseFee }}</el-descriptions-item>
        <el-descriptions-item label="超时费">¥{{ selectedOrder.penaltyFee || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="总费用" :span="2">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ selectedOrder.totalAmount || '0.00' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="电池类型">{{ getBatteryTypeName(selectedOrder.batteryType) }}</el-descriptions-item>
        <el-descriptions-item label="当前温度">
          <span v-if="selectedOrder.currentTemperature" :style="{ color: selectedOrder.currentTemperature >= 50 ? '#f56c6c' : selectedOrder.currentTemperature >= 40 ? '#e6a23c' : '#67c23a', fontWeight: 'bold' }">
            {{ selectedOrder.currentTemperature.toFixed(1) }}°C
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ selectedOrder.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ selectedOrder.endTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, shelterApi, socketApi } from '../api'

const statusFilter = ref('')
const orderNoFilter = ref('')
const orders = ref([])
const shelters = ref([])
const sockets = ref([])
const availableSockets = ref([])
const showStartDialog = ref(false)
const showDetailDialog = ref(false)
const selectedOrder = ref(null)

const startForm = reactive({
  userId: 2,
  shelterId: null,
  socketId: null
})

const filteredOrders = computed(() => {
  return orders.value.filter(order => {
    const matchStatus = !statusFilter.value || order.status === statusFilter.value
    const matchOrderNo = !orderNoFilter.value || order.orderNo.includes(orderNoFilter.value)
    return matchStatus && matchOrderNo
  })
})

const getStatusName = (status) => {
  const map = {
    PENDING: '待充电',
    CHARGING: '充电中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    OVERTIME: '超时'
  }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = {
    PENDING: 'info',
    CHARGING: 'primary',
    COMPLETED: 'success',
    CANCELLED: 'info',
    OVERTIME: 'danger'
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

const getBatteryTypeName = (type) => {
  const map = {
    LEAD_ACID: '铅酸电池',
    LITHIUM_ION: '锂离子电池',
    LITHIUM_IRON_PHOSPHATE: '磷酸铁锂电池',
    NICKEL_METAL_HYDRIDE: '镍氢电池',
    UNKNOWN: '未知类型'
  }
  return map[type] || type || '未知类型'
}

const loadData = async () => {
  try {
    orders.value = await orderApi.findAll()
    shelters.value = await shelterApi.findAll()
    sockets.value = await socketApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const onShelterChange = (shelterId) => {
  availableSockets.value = sockets.value.filter(
    s => s.shelterId === shelterId && (s.status === 'AVAILABLE' || s.status === 'OCCUPIED')
  )
}

const handleStop = async (row) => {
  try {
    await ElMessageBox.confirm('确定要结束充电吗？', '提示', { type: 'warning' })
    await orderApi.stopCharging(row.id)
    ElMessage.success('充电已结束')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handlePay = async (row) => {
  try {
    await orderApi.pay(row.id)
    ElMessage.success('支付成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const viewDetail = (row) => {
  selectedOrder.value = row
  showDetailDialog.value = true
}

const startCharging = async () => {
  try {
    await orderApi.startCharging(startForm)
    ElMessage.success('开始充电')
    showStartDialog.value = false
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
.orders-page {
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
