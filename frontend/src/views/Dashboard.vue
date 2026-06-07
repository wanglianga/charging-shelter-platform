<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #409EFF">
              <el-icon><House /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalShelters }}</div>
              <div class="stat-label">车棚总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #67C23A">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.availableSockets }}</div>
              <div class="stat-label">可用插座</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #E6A23C">
              <el-icon><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: #F56C6C">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingAlarms }}</div>
              <div class="stat-label">待处理告警</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>车棚占用情况</span>
          </template>
          <div ref="shelterChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>近7日充电量趋势</span>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="bottom-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新告警</span>
            <el-button type="primary" link size="small" @click="$router.push('/alarms')">查看全部</el-button>
          </template>
          <el-table :data="recentAlarms" size="small">
            <el-table-column prop="type" label="告警类型" width="120">
              <template #default="{ row }">
                <el-tag :type="getAlarmTypeTag(row.type)">{{ getAlarmTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="描述" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getAlarmStatusTag(row.status)">{{ getAlarmStatusName(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最新巡检记录</span>
            <el-button type="primary" link size="small" @click="$router.push('/inspections')">查看全部</el-button>
          </template>
          <el-table :data="recentInspections" size="small">
            <el-table-column prop="type" label="巡检类型" width="120">
              <template #default="{ row }">
                <el-tag>{{ getInspectionTypeName(row.type) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="issueDescription" label="问题描述" />
            <el-table-column prop="hasIssue" label="是否有问题" width="100">
              <template #default="{ row }">
                <el-tag :type="row.hasIssue ? 'danger' : 'success'">{{ row.hasIssue ? '是' : '否' }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { shelterApi, socketApi, alarmApi, inspectionApi, orderApi } from '../api'

const shelterChartRef = ref()
const trendChartRef = ref()

const stats = reactive({
  totalShelters: 0,
  availableSockets: 0,
  todayOrders: 0,
  pendingAlarms: 0
})

const recentAlarms = ref([])
const recentInspections = ref([])

const getAlarmTypeName = (type) => {
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

const getAlarmTypeTag = (type) => {
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

const getAlarmStatusName = (status) => {
  const map = { PENDING: '待处理', PROCESSING: '处理中', RESOLVED: '已解决', DISMISSED: '已忽略' }
  return map[status] || status
}

const getAlarmStatusTag = (status) => {
  const map = { PENDING: 'warning', PROCESSING: 'primary', RESOLVED: 'success', DISMISSED: 'info' }
  return map[status] || 'info'
}

const getInspectionTypeName = (type) => {
  const map = {
    FIRE_EXIT: '消防通道',
    FLYING_WIRE: '飞线充电',
    SMOKE_DETECTOR: '烟感检测',
    NIGHT_PATROL: '夜间巡查',
    SAFETY_CHECK: '安全检查'
  }
  return map[type] || type
}

const loadData = async () => {
  try {
    const shelters = await shelterApi.findAll()
    const sockets = await socketApi.findAll()
    const alarms = await alarmApi.findByStatus('PENDING')
    const allAlarms = await alarmApi.findAll()
    const allInspections = await inspectionApi.findAll()
    const orders = await orderApi.findAll()

    stats.totalShelters = shelters.length
    stats.availableSockets = sockets.filter(s => s.status === 'AVAILABLE').length
    stats.pendingAlarms = alarms.length
    stats.todayOrders = orders.length

    recentAlarms.value = allAlarms.slice(0, 5)
    recentInspections.value = allInspections.slice(0, 5)

    initShelterChart(shelters, sockets)
    initTrendChart()
  } catch (error) {
    console.error(error)
  }
}

const initShelterChart = (shelters, sockets) => {
  const chart = echarts.init(shelterChartRef.value)
  const shelterNames = shelters.map(s => s.name)
  const totalData = shelters.map(s => s.totalSockets || 0)
  const availableData = shelters.map(s => {
    const shelterSockets = sockets.filter(sock => sock.shelterId === s.id)
    return shelterSockets.filter(sock => sock.status === 'AVAILABLE').length
  })

  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['总插座数', '可用插座数'] },
    xAxis: { type: 'category', data: shelterNames },
    yAxis: { type: 'value' },
    series: [
      { name: '总插座数', type: 'bar', data: totalData, itemStyle: { color: '#409EFF' } },
      { name: '可用插座数', type: 'bar', data: availableData, itemStyle: { color: '#67C23A' } }
    ]
  })
}

const initTrendChart = () => {
  const chart = echarts.init(trendChartRef.value)
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const data = [120, 132, 101, 134, 90, 230, 210]

  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days },
    yAxis: { type: 'value', name: 'kWh' },
    series: [{
      data: data,
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(64, 158, 255, 0.3)' },
      itemStyle: { color: '#409EFF' }
    }]
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-container {
  height: 300px;
}

.bottom-row {
  margin-bottom: 20px;
}
</style>
