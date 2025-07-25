<template>
  <div class="lecture-container">
    <el-row :gutter="20">
      <!-- 左侧区域：演讲信息和统计数据 -->
      <el-col :span="16">
        <!-- 演讲信息卡片 -->
        <el-card class="speech-info-card">
          <template #header>
            <div class="card-header">
              <h3>演讲信息</h3>
              <el-button type="primary" size="small" @click="refreshData">刷新数据</el-button>
            </div>
          </template>

          <div class="speech-info">
            <p><strong>标题：</strong>{{ speech.title }}</p>
            <p><strong>描述：</strong>{{ speech.description }}</p>
            <p><strong>演讲者：</strong>{{ speech.speakerName }}</p>
            <p><strong>开始时间：</strong>{{ formatDate(speech.startTime) }}</p>

            <p>
              <strong>状态：</strong>
              <el-tag :type="speech.status === 1 ? 'success' : 'info'">{{
                speech.status === 1 ? '进行中' : '已结束'
              }}</el-tag>
            </p>
          </div>
        </el-card>

        <!-- 统计数据卡片 -->
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <h3>实时数据</h3>
            </div>
          </template>

          <!-- 总体统计 -->
          <div class="statistics-overview">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ audienceCount }}</div>
                  <div class="stat-label">参与学生数</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ averageCorrectRate }}%</div>
                  <div class="stat-label">平均正确率</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ questions.length }}</div>
                  <div class="stat-label">已发布题目数</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 答题分布图表 -->
          <div class="chart-section">
            <h4>答题分布</h4>
            <div v-if="answerDistributionData.length === 0" class="chart-placeholder">
              暂无答题数据
            </div>
            <div v-else class="answer-distribution">
              <div
                v-for="item in answerDistributionData"
                :key="item.questionId"
                class="question-stats"
              >
                <div class="question-title">{{ item.description }}</div>
                <div class="correct-rate">正确率: {{ item.correctPercentage }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧区域：学生反馈和吐槽 -->
      <el-col :span="8">
        <!-- 吐槽列表 -->
        <el-card class="spits-card">
          <template #header>
            <div class="card-header">
              <h3>演讲吐槽</h3>
              <el-button type="primary" size="small" @click="refreshSpits">刷新</el-button>
            </div>
          </template>

          <div class="spits-list">
            <el-timeline>
              <el-timeline-item
                v-for="spit in spitsList"
                :key="spit.time?.toString()"
                :timestamp="formatDate(spit.time)"
                type="primary"
              >
                <div class="spit-content">
                  <p class="spit-text">{{ spit.content }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getSpeechById,
  getSpitsBySpeechId,
  getSpeechData,
  type ReturnSpeech,
  type Spit,
  type OrganizerData,
} from '@/api/speech'
import { getQuestionsByTestId, getTestList, type Question, type test } from '@/api/question'

const route = useRoute()
const speechId = route.params.id as string

// 添加计算属性来处理答题数据
const answerDistributionData = computed(() => {
  if (
    !organizerData.value ||
    !organizerData.value.userAnswers ||
    organizerData.value.userAnswers.length === 0
  ) {
    return []
  }

  return organizerData.value.userAnswers.map((item) => ({
    questionId: item.questionId,
    description: item.description,
    correctPercentage: item.correctPercentage, // 注意这里使用正确的字段名 CorrectPercentage
  }))
})

// 组织者数据
const organizerData = ref<OrganizerData | null>(null)

// 演讲信息
const speech = ref<ReturnSpeech>({
  speechId: '',
  title: '',
  description: '',
  organizer: '',
  organizerName: '',
  speaker: '',
  speakerName: '',
  startTime: new Date(),
  endTime: new Date(),
  status: 0,
})

// 听众数量
const audienceCount = ref(0)

// 题目列表 - 仍然需要保留，因为用于显示已发布题目数
const questions = ref<Question[]>([])

// 测试列表
const tests = ref<test[]>([])

// 平均正确率
const averageCorrectRate = ref(0)

// 吐槽列表
const spitsList = ref<Spit[]>([])

// 格式化日期
const formatDate = (date: Date | undefined) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 获取演讲信息
const getSpeechInfo = async () => {
  try {
    const response = await getSpeechById(speechId)
    speech.value = response.data
  } catch (error) {
    console.error('获取演讲信息失败：', error)
    ElMessage.error('获取演讲信息失败')
  }
}

// 获取测试列表
const getTests = async () => {
  try {
    const response = await getTestList(speechId)
    tests.value = response.data
    // 获取所有测试的题目
    await getQuestionsForTests()
  } catch (error) {
    console.error('获取测试列表失败：', error)
    ElMessage.error('获取测试列表失败')
  }
}

// 获取所有测试的题目
const getQuestionsForTests = async () => {
  try {
    // 清空现有题目
    questions.value = []

    // 如果没有测试，直接返回
    if (tests.value.length === 0) return

    // 获取每个测试的题目并合并
    const allQuestions = await Promise.all(
      tests.value.map(async (test) => {
        try {
          const response = await getQuestionsByTestId(test.testId)
          return response.data
        } catch (error) {
          console.error(`获取测试 ${test.testId} 的题目失败：`, error)
          return []
        }
      }),
    )

    // 合并所有测试的题目
    questions.value = allQuestions.flat()
    console.log('获取到的题目数量:', questions.value.length)
  } catch (error) {
    console.error('获取题目失败：', error)
    ElMessage.error('获取题目失败')
  }
}

// 刷新吐槽列表
const refreshSpits = async () => {
  try {
    const response = await getSpitsBySpeechId(speechId)
    spitsList.value = response.data
  } catch (error) {
    console.error('获取吐槽列表失败：', error)
    ElMessage.error('获取吐槽列表失败')
  }
}

// 获取组织者数据
const getOrganizerData = async () => {
  try {
    console.log('正在获取组织者数据，speechId:', speechId)
    const response = await getSpeechData(speechId)
    console.log('获取到的组织者数据:', response.data)
    organizerData.value = response.data
    // 更新统计数据
    audienceCount.value = organizerData.value.audienceCount
    averageCorrectRate.value = parseFloat(organizerData.value.averageAccuracy)
    console.log('处理后的答题分布数据:', answerDistributionData.value)
  } catch (error) {
    console.error('获取组织者数据失败：', error)
    ElMessage.error('获取组织者数据失败')
  }
}

// 刷新所有数据
const refreshData = async () => {
  await Promise.all([getSpeechInfo(), getOrganizerData(), getTests(), refreshSpits()])
}

// 页面加载时获取数据
onMounted(() => {
  refreshData()
})
</script>

<style scoped>
.lecture-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
}

.speech-info-card,
.statistics-card,
.spits-card {
  margin-bottom: 20px;
}

.speech-info {
  margin-top: 10px;
}

.statistics-overview {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.stat-value {
  font-size: 24px;
  color: #409eff;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.chart-section {
  margin-top: 20px;
}

.chart-container {
  height: 300px;
  margin-top: 15px;
}

.chart-placeholder {
  height: 100%;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
}

/* 答题分布样式 */
.answer-distribution {
  margin-top: 15px;
}

.question-stats {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-title {
  font-weight: bold;
}

.correct-rate {
  color: #67c23a;
  font-weight: bold;
}

.spits-list {
  max-height: 400px;
  overflow-y: auto;
}

.spit-content {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.spit-text {
  margin: 8px 0;
  color: #606266;
}
</style>
