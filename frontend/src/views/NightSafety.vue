<template>
  <div class="night-safety-page">
    <el-card class="header-card">
      <div class="header-content">
        <div>
          <h2 style="margin: 0; color: #303133">夜间安全巡检</h2>
          <p style="margin: 8px 0 0 0; color: #909399">{{ summary?.nightPeriodDescription || '夜间高风险时段: 22:00 - 次日06:00' }}</p>
        </div>
        <div>
          <el-tag :type="isNightTime ? 'danger' : 'success'" size="large">
            {{ isNightTime ? '当前处于夜间高风险时段' : '当前非夜间时段' }}
          </el-tag>
          <el-button type="primary" style="margin-left: 16px" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon total"></div>
            <div class="stat-info">
              <div class="stat-value">{{ summary?.totalChargingOrders || 0 }}</div>
              <div class="stat-label">正在充电订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card critical">
          <div class="stat-item">
            <div class="stat-icon critical"></div>
            <div class="stat-info">
              <div class="stat-value">{{ summary?.criticalRiskCount || 0 }}</div>
              <div class="stat-label">极高风险</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card high">
          <div class="stat-item">
            <div class="stat-icon high"></div>
            <div class="stat-info">
              <div class="stat-value">{{ summary?.highRiskCount || 0 }}</div>
              <div class="stat-label">高风险</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card medium">
          <div class="stat-item">
            <div class="stat-icon medium"></div>
            <div class="stat-info">
              <div class="stat-value">{{ summary?.mediumRiskCount || 0 }}</div>
              <div class="stat-label">中风险</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-weight: bold; font-size: 16px">重点巡检车位</span>
          <el-tag type="danger" v-if="highRiskOrders.length > 0">共 {{ highRiskOrders.length }} 个需重点检查</el-tag>
        </div>
      </template>

      <el-empty v-if="highRiskOrders.length === 0" description="暂无高风险订单" />

      <el-table :data="highRiskOrders" stripe v-else>
        <el-table-column label="风险等级" width="100" fixed="left">
          <template #default="{ row }">
            <el-tag :type="getRiskTagType(row.riskLevel)" effect="dark">
              {{ getRiskLevelName(row.riskLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="风险分值" width="90">
          <template #default="{ row }">
            <span :style="{ color: getRiskScoreColor(row.riskScore), fontWeight: 'bold' }">
              {{ row.riskScore }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="socketCode" label="插座编号" width="110" />
        <el-table-column prop="shelterName" label="车棚" width="130" />
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="充电时长" width="110">
          <template #default="{ row }">
            {{ formatDuration(row.chargingDurationMinutes) }}
          </template>
        </el-table-column>
        <el-table-column label="当前温度" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.currentTemperature >= 50 ? '#f56c6c' : row.currentTemperature >= 40 ? '#e6a23c' : '#67c23a', fontWeight: 'bold' }">
              {{ row.currentTemperature?.toFixed(1) }}°C
            </span>
          </template>
        </el-table-column>
        <el-table-column label="电池类型" width="110">
          <template #default="{ row }">
            {{ getBatteryTypeName(row.batteryType) }}
          </template>
        </el-table-column>
        <el-table-column label="跳闸记录" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.tripCount >= 3 ? '#f56c6c' : row.tripCount >= 1 ? '#e6a23c' : '#67c23a' }">
              {{ row.tripCount || 0 }} 次
            </span>
          </template>
        </el-table-column>
        <el-table-column label="最近异常" min-width="200">
          <template #default="{ row }">
            <div v-if="row.lastAlarmDescription">
              <div style="color: #f56c6c; font-weight: 500">{{ row.lastAlarmDescription }}</div>
              <div style="color: #909399; font-size: 12px; margin-top: 4px">
                {{ row.lastAlarmTime }}
              </div>
            </div>
            <span v-else style="color: #909399">暂无异常记录</span>
          </template>
        </el-table-column>
        <el-table-column label="风险因素" min-width="250">
          <template #default="{ row }">
            <el-tag
              v-for="(factor, index) in row.riskFactors"
              :key="index"
              size="small"
              style="margin: 2px"
              :type="getRiskFactorType(index)"
            >
              {{ factor }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showDetailDialog" title="风险详情" width="700px">
      <div v-if="selectedOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="风险等级" :span="2">
            <el-tag :type="getRiskTagType(selectedOrder.riskLevel)" effect="dark" size="large">
              {{ getRiskLevelName(selectedOrder.riskLevel) }} ({{ selectedOrder.riskScore }}分)
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="插座编号">{{ selectedOrder.socketCode }}</el-descriptions-item>
          <el-descriptions-item label="车棚">{{ selectedOrder.shelterName }}</el-descriptions-item>
          <el-descriptions-item label="位置">{{ selectedOrder.location || '-' }}</el-descriptions-item>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
          <el-descriptions-item label="电池类型">{{ getBatteryTypeName(selectedOrder.batteryType) }}</el-descriptions-item>
          <el-descriptions-item label="开始充电时间">{{ selectedOrder.startTime }}</el-descriptions-item>
          <el-descriptions-item label="已充电时长">{{ formatDuration(selectedOrder.chargingDurationMinutes) }}</el-descriptions-item>
          <el-descriptions-item label="当前温度">
            <span :style="{ color: selectedOrder.currentTemperature >= 50 ? '#f56c6c' : selectedOrder.currentTemperature >= 40 ? '#e6a23c' : '#67c23a', fontWeight: 'bold' }">
              {{ selectedOrder.currentTemperature?.toFixed(1) }}°C
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="历史跳闸次数">
            <span :style="{ color: selectedOrder.tripCount >= 3 ? '#f56c6c' : selectedOrder.tripCount >= 1 ? '#e6a23c' : '#67c23a' }">
              {{ selectedOrder.tripCount || 0 }} 次
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="最近跳闸时间">
            {{ selectedOrder.lastTripTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="最近异常" :span="2">
            <div v-if="selectedOrder.lastAlarmDescription">
              <div style="color: #f56c6c; font-weight: 500">{{ selectedOrder.lastAlarmDescription }}</div>
              <div style="color: #909399; font-size: 12px; margin-top: 4px">
                发生时间: {{ selectedOrder.lastAlarmTime }}
              </div>
            </div>
            <span v-else style="color: #909399">暂无异常记录</span>
          </el-descriptions-item>
        </el-descriptions>

        <div style="margin-top: 20px">
          <h4 style="margin: 0 0 12px 0; color: #303133">风险因素分析</h4>
          <el-tag
            v-for="(factor, index) in selectedOrder.riskFactors"
            :key="index"
            style="margin: 4px"
            :type="getRiskFactorType(index)"
          >
            {{ factor }}
          </el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { nightSafetyApi } from '../api'

const summary = ref(null)
const highRiskOrders = ref([])
const isNightTime = ref(false)
const showDetailDialog = ref(false)
const selectedOrder = ref(null)

const getRiskLevelName = (level) => {
  const map = {
    LOW: '低风险',
    MEDIUM: '中风险',
    HIGH: '高风险',
    CRITICAL: '极高风险'
  }
  return map[level] || level
}

const getRiskTagType = (level) => {
  const map = {
    LOW: 'success',
    MEDIUM: 'warning',
    HIGH: 'danger',
    CRITICAL: 'danger'
  }
  return map[level] || 'info'
}

const getRiskScoreColor = (score) => {
  if (score >= 80) return '#f56c6c'
  if (score >= 50) return '#e6a23c'
  if (score >= 25) return '#67c23a'
  return '#909399'
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

const formatDuration = (minutes) => {
  if (!minutes) return '-'
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  if (hours > 0) {
    return `${hours}小时${mins}分钟`
  }
  return `${mins}分钟`
}

const getRiskFactorType = (index) => {
  const types = ['danger', 'warning', 'info', 'success']
  return types[index % types.length]
}

const loadData = async () => {
  try {
    summary.value = await nightSafetyApi.getSummary()
    highRiskOrders.value = summary.value.highRiskOrders || []
    isNightTime.value = await nightSafetyApi.isNightTime()
  } catch (error) {
    console.error(error)
    ElMessage.error('加载数据失败')
  }
}

const viewDetail = (row) => {
  selectedOrder.value = row
  showDetailDialog.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.night-safety-page {
  padding: 0;
}

.header-card {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  border-radius: 8px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin-right: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.total {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.stat-icon.critical {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-icon.high {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.stat-icon.medium {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
</style>
