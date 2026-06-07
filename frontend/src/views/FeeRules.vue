<template>
  <div class="fee-rules-page">
    <el-card class="active-rule-card">
      <template #header>
        <span>当前生效规则</span>
      </template>
      <el-descriptions v-if="activeRule" :column="3" border>
        <el-descriptions-item label="规则名称">{{ activeRule.name }}</el-descriptions-item>
        <el-descriptions-item label="单价">
          <span style="color: #f56c6c; font-size: 18px; font-weight: bold">¥{{ activeRule.unitPrice }}</span>
          / kWh
        </el-descriptions-item>
        <el-descriptions-item label="基础费">¥{{ activeRule.baseFee }}</el-descriptions-item>
        <el-descriptions-item label="超时罚款">¥{{ activeRule.overtimePenalty }}</el-descriptions-item>
        <el-descriptions-item label="免费时长">{{ activeRule.freeMinutes }} 分钟</el-descriptions-item>
        <el-descriptions-item label="最长充电">{{ activeRule.maxChargingHours }} 小时</el-descriptions-item>
        <el-descriptions-item label="说明" :span="3">{{ activeRule.description }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card>
      <template #header>
        <span>所有收费规则</span>
        <el-button type="primary" size="small" @click="showAddDialog = true" style="float: right">
          <el-icon><Plus /></el-icon>
          新增规则
        </el-button>
      </template>
      <el-table :data="feeRules" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="规则名称" width="150" />
        <el-table-column prop="unitPrice" label="单价(元/kWh)" width="120" />
        <el-table-column prop="baseFee" label="基础费(元)" width="100" />
        <el-table-column prop="overtimePenalty" label="超时罚款(元)" width="120" />
        <el-table-column prop="freeMinutes" label="免费时长(分钟)" width="120" />
        <el-table-column prop="maxChargingHours" label="最长充电(小时)" width="120" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '生效中' : '未生效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-if="!row.isActive" @click="setActive(row)">
              设为生效
            </el-button>
            <el-button size="small" type="danger" @click="deleteRule(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showAddDialog" title="新增收费规则" width="500px">
      <el-form :model="ruleForm" label-width="120px">
        <el-form-item label="规则名称">
          <el-input v-model="ruleForm.name" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="单价(元/kWh)">
          <el-input-number v-model="ruleForm.unitPrice" :min="0" :precision="2" :step="0.1" />
        </el-form-item>
        <el-form-item label="基础费(元)">
          <el-input-number v-model="ruleForm.baseFee" :min="0" :precision="2" :step="0.5" />
        </el-form-item>
        <el-form-item label="超时罚款(元)">
          <el-input-number v-model="ruleForm.overtimePenalty" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="免费时长(分钟)">
          <el-input-number v-model="ruleForm.freeMinutes" :min="0" :step="5" />
        </el-form-item>
        <el-form-item label="最长充电(小时)">
          <el-input-number v-model="ruleForm.maxChargingHours" :min="1" :max="24" />
        </el-form-item>
        <el-form-item label="设为生效">
          <el-switch v-model="ruleForm.isActive" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="ruleForm.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveRule">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { feeRuleApi } from '../api'

const activeRule = ref(null)
const feeRules = ref([])
const showAddDialog = ref(false)

const ruleForm = reactive({
  name: '',
  unitPrice: 1.5,
  baseFee: 2.0,
  overtimePenalty: 5.0,
  freeMinutes: 30,
  maxChargingHours: 8,
  isActive: true,
  description: ''
})

const loadData = async () => {
  try {
    feeRules.value = await feeRuleApi.findAll()
    activeRule.value = feeRules.value.find(r => r.isActive) || null
  } catch (error) {
    console.error(error)
  }
}

const setActive = async (row) => {
  try {
    row.isActive = true
    await feeRuleApi.update(row.id, row)
    ElMessage.success('已设为生效规则')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const deleteRule = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该规则吗？', '提示', { type: 'warning' })
    await feeRuleApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const saveRule = async () => {
  try {
    await feeRuleApi.save(ruleForm)
    ElMessage.success('规则创建成功')
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
.fee-rules-page {
  padding: 0;
}

.active-rule-card {
  margin-bottom: 20px;
}
</style>
