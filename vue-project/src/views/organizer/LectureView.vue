<template>
  <div class="lecture-container">
    <el-row :gutter="20">
      <!-- 左侧区域：实时数据展示 -->
      <el-col :span="16">
        <!-- 演讲信息卡片 -->
        <el-card class="speech-info-card">
          <template #header>
            <div class="card-header">
              <h3>演讲信息</h3>
            </div>
          </template>

          <div class="speech-info">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="演讲标题">{{ speech.title }}</el-descriptions-item>
              <el-descriptions-item label="演讲者">{{ speech.speakerName }}</el-descriptions-item>
              <el-descriptions-item label="开始时间">{{
                formatDate(speech.startTime)
              }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="speech.status === 1 ? 'success' : 'info'">
                  {{ speech.status === 1 ? '进行中' : '已结束' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="演讲简介" :span="2">
                {{ speech.description }}
              </el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <h3>实时数据</h3>
              <el-button type="primary" size="small" @click="refreshData">刷新</el-button>
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
            <div class="chart-container">
              <!-- 这里后续集成echarts图表 -->
              <div class="chart-placeholder">答题分布图表</div>
            </div>
          </div>
        </el-card>

        <!-- 题目列表 -->
        <el-card class="questions-card">
          <template #header>
            <div class="card-header">
              <h3>题目列表</h3>
            </div>
          </template>

          <el-table :data="questions" style="width: 100%">
            <el-table-column label="序号" type="index" width="80" />
            <el-table-column prop="description" label="题目内容" />
            <el-table-column label="选项" width="200">
              <template #default="{ row }">
                <div>A: {{ row.optionA }}</div>
                <div>B: {{ row.optionB }}</div>
                <div>C: {{ row.optionC }}</div>
                <div>D: {{ row.optionD }}</div>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isUsed ? 'info' : 'success'">
                  {{ row.isUsed ? '已使用' : '未使用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
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

        <!-- 评论列表 -->
        <el-card class="comments-card">
          <template #header>
            <div class="card-header">
              <h3>题目评论</h3>
              <el-button type="primary" size="small" @click="refreshComments">刷新</el-button>
            </div>
          </template>

          <div class="comments-list">
            <el-timeline>
              <el-timeline-item
                v-for="comment in comments"
                :key="comment.commentId"
                :timestamp="formatDate(comment.time)"
                type="primary"
              >
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="publisher-name">{{ comment.publisherName }}</span>
                    <template v-if="comment.replyName">
                      <span class="reply-to">回复</span>
                      <span class="reply-name">{{ comment.replyName }}</span>
                    </template>
                  </div>
                  <p class="comment-text">{{ comment.content }}</p>
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getSpeechById,
  getSpitsBySpeechId,
  getAudienceBySpeechId,
  type ReturnSpeech,
  type Spit,
} from '@/api/speech'
import { getQuestionsById, type Question } from '@/api/question'
import { getCommentsByQuestionId } from '@/api/comment'

interface ReturnComment {
  commentId: string
  questionId: string
  publisher: string
  publisherName: string
  replyId?: string // 标记为可选
  replyName?: string // 标记为可选
  time: Date
  content: string
}

const route = useRoute()
const speechId = route.params.id as string

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

// 题目列表
const questions = ref<Question[]>([])

// 平均正确率
const averageCorrectRate = ref(0)

// 吐槽列表
const spitsList = ref<Spit[]>([])

// 评论列表
const comments = ref<ReturnComment[]>([])

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

// 获取听众数量
const getAudienceCount = async () => {
  try {
    const response = await getAudienceBySpeechId(speechId)
    audienceCount.value = response.data.length
  } catch (error) {
    console.error('获取听众数量失败：', error)
    ElMessage.error('获取听众数量失败')
  }
}

// 获取题目列表
const getQuestions = async () => {
  try {
    const response = await getQuestionsById(speechId)
    questions.value = response.data
    // 获取每个题目的评论
    await Promise.all(questions.value.map((question) => getQuestionComments(question.questionId)))
  } catch (error) {
    console.error('获取题目列表失败：', error)
    ElMessage.error('获取题目列表失败')
  }
}

// 获取题目评论
const getQuestionComments = async (questionId: string) => {
  try {
    const response = await getCommentsByQuestionId(questionId)
    // 处理返回的评论数据，确保可选字段存在
    const processedComments = response.data.map((comment) => ({
      ...comment,
      replyId: comment.replyId || '',
      replyName: comment.replyName || '',
    }))
    comments.value = [...comments.value, ...processedComments]
  } catch (error) {
    console.error('获取题目评论失败：', error)
    ElMessage.error('获取题目评论失败')
  }
}

// 刷新评论列表
const refreshComments = async () => {
  comments.value = []
  await Promise.all(questions.value.map((question) => getQuestionComments(question.questionId)))
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

// 刷新所有数据
const refreshData = async () => {
  await Promise.all([getSpeechInfo(), getAudienceCount(), getQuestions(), refreshSpits()])
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
.questions-card,
.spits-card,
.comments-card {
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

.spits-list,
.comments-list {
  max-height: 400px;
  overflow-y: auto;
}

.spit-content,
.comment-content {
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.comment-header {
  margin-bottom: 8px;
}

.publisher-name {
  font-weight: bold;
  color: #409eff;
}

.reply-to {
  margin: 0 4px;
  color: #909399;
}

.reply-name {
  color: #409eff;
}

.spit-text,
.comment-text {
  margin: 8px 0;
  color: #606266;
}
</style>
