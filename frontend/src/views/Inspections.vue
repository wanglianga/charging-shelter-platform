<template>
  <div class="inspections-page">
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select v-model="typeFilter" placeholder="巡检类型" clearable style="width: 100%">
            <el-option label="消防通道" value="FIRE_EXIT" />
            <el-option label="飞线充电" value="FLYING_WIRE" />
            <el-option label="烟感检测" value="SMOKE_DETECTOR" />
            <el-option label="夜间巡查" value="NIGHT_PATROL" />
            <el-option label="安全检查" value="SAFETY_CHECK" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="issueFilter" placeholder="问题状态" clearable style="width: 100%">
            <el-option label="有问题" :value="true" />
            <el-option label="无问题" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="12" style="text-align: right">
          <el-button type="primary" @click="loadData">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
          <el-button type="success" @click="showAddDialog = true">
            <el-icon><Plus /></el-icon>
            新增巡检
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card>
      <el-table :data="filteredInspections" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="type" label="巡检类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="officerId" label="安全员ID" width="100" />
        <el-table-column prop="shelterName" label="车棚" width="130">
          <template #default="{ row }">
            {{ getShelterName(row.shelterId) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column prop="hasIssue" label="是否有问题" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hasIssue ? 'danger' : 'success'">{{ row.hasIssue ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="issueDescription" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="isResolved" label="是否已解决" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.hasIssue" :type="row.isResolved ? 'success' : 'warning'">
              {{ row.isResolved ? '已解决' : '未解决' }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="inspectionTime" label="巡检时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="success" v-if="row.hasIssue && !row.isResolved" @click="handleResolve(row)">
              标记解决
            </el-button>
            <el-button size="small" @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="filteredInspections.length"
        :page-size="10"
      />
    </el-card>

    <el-dialog v-model="showResolveDialog" title="处理问题" width="500px">
      <el-form label-width="100px">
        <el-form-item label="处理建议">
          <el-input v-model="handleSuggestion" type="textarea" :rows="4" placeholder="请输入处理建议" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showResolveDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmResolve">确认解决</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showAddDialog" title="新增巡检记录" width="500px">
      <el-form :model="inspectionForm" label-width="100px">
        <el-form-item label="巡检类型">
          <el-select v-model="inspectionForm.type" placeholder="请选择巡检类型" style="width: 100%">
            <el-option label="消防通道" value="FIRE_EXIT" />
            <el-option label="飞线充电" value="FLYING_WIRE" />
            <el-option label="烟感检测" value="SMOKE_DETECTOR" />
            <el-option label="夜间巡查" value="NIGHT_PATROL" />
            <el-option label="安全检查" value="SAFETY_CHECK" />
          </el-select>
        </el-form-item>
        <el-form-item label="车棚">
          <el-select v-model="inspectionForm.shelterId" placeholder="请选择车棚" style="width: 100%">
            <el-option v-for="shelter in shelters" :key="shelter.id" :label="shelter.name" :value="shelter.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="inspectionForm.location" placeholder="请输入具体位置" />
        </el-form-item>
        <el-form-item label="是否有问题">
          <el-radio-group v-model="inspectionForm.hasIssue">
            <el-radio :value="false">无问题</el-radio>
            <el-radio :value="true">有问题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="问题描述" v-if="inspectionForm.hasIssue">
          <el-input v-model="inspectionForm.issueDescription" type="textarea" :rows="3" placeholder="请描述发现的问题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveInspection">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { inspectionApi, shelterApi } from '../api'

const typeFilter = ref('')
const issueFilter = ref(null)
const inspections = ref([])
const shelters = ref([])
const showResolveDialog = ref(false)
const showAddDialog = ref(false)
const selectedInspection = ref(null)
const handleSuggestion = ref('')

const inspectionForm = reactive({
  type: '',
  officerId: 5,
  shelterId: null,
  location: '',
  hasIssue: false,
  issueDescription: ''
})

const filteredInspections = computed(() => {
  return inspections.value.filter(item => {
    const matchType = !typeFilter.value || item.type === typeFilter.value
    const matchIssue = issueFilter.value === null || item.hasIssue === issueFilter.value
    return matchType && matchIssue
  })
})

const getTypeName = (type) => {
  const map = {
    FIRE_EXIT: '消防通道',
    FLYING_WIRE: '飞线充电',
    SMOKE_DETECTOR: '烟感检测',
    NIGHT_PATROL: '夜间巡查',
    SAFETY_CHECK: '安全检查'
  }
  return map[type] || type
}

const getShelterName = (shelterId) => {
  if (!shelterId) return '-'
  const shelter = shelters.value.find(s => s.id === shelterId)
  return shelter ? shelter.name : '-'
}

const loadData = async () => {
  try {
    inspections.value = await inspectionApi.findAll()
    shelters.value = await shelterApi.findAll()
  } catch (error) {
    console.error(error)
  }
}

const handleResolve = (row) => {
  selectedInspection.value = row
  handleSuggestion.value = ''
  showResolveDialog.value = true
}

const confirmResolve = async () => {
  if (selectedInspection.value) {
    try {
      await inspectionApi.resolve(selectedInspection.value.id, { handleSuggestion: handleSuggestion.value })
      ElMessage.success('已标记为已解决')
      showResolveDialog.value = false
      loadData()
    } catch (error) {
      console.error(error)
    }
  }
}

const viewDetail = (row) => {
  const detail = row.issueDescription || '未发现问题，巡检正常'
  ElMessage.info(detail)
}

const saveInspection = async () => {
  try {
    await inspectionApi.save(inspectionForm)
    ElMessage.success('巡检记录已保存')
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
.inspections-page {
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
