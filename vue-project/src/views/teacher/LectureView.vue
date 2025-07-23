<template>
  <div class="lecture-container">
    <el-row :gutter="20">
      <!-- 左侧题目管理区域 -->
      <el-col :span="16">
        <!-- 文件上传卡片 -->
        <el-card class="upload-card">
          <template #header>
            <div class="card-header">
              <h3>文件上传</h3>
            </div>
          </template>

          <el-upload
            class="file-upload"
            drag
            action="#"
            :auto-upload="false"
            :show-file-list="true"
            :on-change="handleFileChange"
            accept=".pdf,.ppt,.pptx"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 PDF、PPT 格式文件</div>
            </template>
          </el-upload>

          <div class="upload-actions" v-if="currentFile">
            <el-button type="primary" @click="handleUploadFile" :loading="uploading">
              开始处理
            </el-button>
          </div>
        </el-card>

        <el-card class="questions-card">
          <template #header>
            <div class="card-header">
              <h3>题目管理</h3>
              <el-button type="primary" @click="handleGetQuestions">获取题目</el-button>
            </div>
          </template>

          <el-table :data="questions" style="width: 100%">
            <el-table-column label="序号" type="index" width="80" />
            <el-table-column prop="description" label="题目内容" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isUsed ? 'info' : 'success'">
                  {{ row.isUsed ? '已使用' : '未使用' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  :disabled="row.isUsed"
                  @click="startQuestion(row)"
                >
                  开始
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  :disabled="!row.isUsed"
                  @click="endQuestion(row)"
                >
                  结束
                </el-button>
                <el-button type="info" size="small" @click="showComments(row)"> 评论 </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧学生反馈区域 -->
      <el-col :span="8">
        <!-- 演讲吐槽卡片 -->
        <el-card class="spits-card">
          <template #header>
            <div class="card-header">
              <h3>演讲反馈</h3>
              <el-button type="primary" @click="refreshSpits">刷新</el-button>
            </div>
          </template>

          <el-timeline>
            <el-timeline-item
              v-for="spit in spitsList"
              :key="spit.time?.toString()"
              :timestamp="spit.time?.toLocaleString()"
              type="primary"
            >
              <p class="spit-content">{{ spit.content }}</p>
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <el-card class="feedback-card">
          <template #header>
            <div class="card-header">
              <h3>实时统计</h3>
              <el-button type="primary" @click="refreshStatistics">刷新</el-button>
            </div>
          </template>

          <div class="statistics">
            <div class="stat-item">
              <h4>当前答题人数</h4>
              <div class="stat-value">{{ statistics.totalAnswers }}</div>
            </div>
            <div class="stat-item">
              <h4>正确率</h4>
              <div class="stat-value">{{ statistics.correctRate }}%</div>
            </div>
          </div>

          <el-divider>选项分布</el-divider>

          <div class="option-distribution">
            <div
              v-for="(count, option) in statistics.optionCounts"
              :key="option"
              class="option-item"
            >
              <span class="option-label">选项 {{ option }}</span>
              <el-progress
                :percentage="(count / statistics.totalAnswers) * 100"
                :format="format"
                :stroke-width="20"
                :color="option === currentQuestion?.answer ? '#67C23A' : '#909399'"
              />
            </div>
          </div>
        </el-card>

        <el-card class="feedback-list-card">
          <template #header>
            <div class="card-header">
              <h3>学生反馈</h3>
            </div>
          </template>

          <el-timeline>
            <el-timeline-item
              v-for="feedback in feedbackList"
              :key="feedback.id"
              :timestamp="feedback.time"
              :type="feedback.isCorrect ? 'success' : 'danger'"
            >
              {{ feedback.studentName }} 选择了 {{ feedback.selection }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 评论对话框 -->
    <el-dialog v-model="commentDialogVisible" title="题目评论" width="50%">
      <div class="comments-container">
        <el-timeline>
          <el-timeline-item
            v-for="comment in commentsList"
            :key="comment.commentId"
            :timestamp="comment.time?.toLocaleString()"
            type="primary"
          >
            <div class="comment-item">
              <div class="comment-header">
                <span class="publisher">{{ comment.publisherName }}</span>
                <span v-if="comment.replyName" class="reply-to">回复 {{ comment.replyName }}</span>
              </div>
              <p class="comment-content">{{ comment.content }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { launchQuestion } from '@/api/question'
import type { Question } from '@/api/question'
import { getSpitsBySpeechId, type Spit } from '@/api/speech'
import { getCommentsByQuestionId, type ReturnComment } from '@/api/comment'

// 文件上传相关
interface UploadFile {
  name: string
  size: number
  type: string
  raw: File
}

const currentFile = ref<UploadFile | null>(null)
const uploading = ref(false)

const handleFileChange = (file: UploadFile) => {
  currentFile.value = file
}

const handleUploadFile = async () => {
  if (!currentFile.value) return

  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', currentFile.value.raw)
    // TODO: 调用后端文件上传API
    await fetch('/api/uploadFile', {
      method: 'POST',
      body: formData,
    })
    ElMessage.success('文件上传成功')
    // 上传成功后获取题目
    await handleGetQuestions()
  } catch (error) {
    console.error('文件上传失败：', error)
    ElMessage.error('文件上传失败')
  } finally {
    uploading.value = false
  }
}

// 题目列表
const questions = ref<Question[]>([])
const speechId = ref('') // 从路由参数获取
const currentQuestion = ref<Question | null>(null)

// 获取题目列表
const handleGetQuestions = async () => {
  try {
    const response = await launchQuestion(speechId.value)
    questions.value = response.data
  } catch (error) {
    console.error('获取题目失败：', error)
    ElMessage.error('获取题目失败')
  }
}

// 开始题目
const startQuestion = async (question: Question) => {
  try {
    // TODO: 调用后端API开始题目
    await fetch(`/api/startQuestion/${question.questionId}`, {
      method: 'POST',
    })
    question.isUsed = true
    currentQuestion.value = question
    ElMessage.success('题目已开始')
    // 开始题目后刷新统计数据
    refreshStatistics()
  } catch (error) {
    console.error('开始题目失败：', error)
    ElMessage.error('开始题目失败')
  }
}

// 结束题目
const endQuestion = async (question: Question) => {
  try {
    // TODO: 调用后端API结束题目
    await fetch(`/api/endQuestion/${question.questionId}`, {
      method: 'POST',
    })
    question.isUsed = false
    currentQuestion.value = null
    ElMessage.success('题目已结束')
  } catch (error) {
    console.error('结束题目失败：', error)
    ElMessage.error('结束题目失败')
  }
}

// 统计数据
interface Statistics {
  totalAnswers: number
  correctRate: number
  optionCounts: Record<string, number>
}

const statistics = ref<Statistics>({
  totalAnswers: 0,
  correctRate: 0,
  optionCounts: {
    A: 0,
    B: 0,
    C: 0,
    D: 0,
  },
})

// 刷新统计数据
const refreshStatistics = async () => {
  if (!currentQuestion.value) return

  try {
    // TODO: 调用后端API获取统计数据
    const response = await fetch(`/api/statistics/${currentQuestion.value.questionId}`)
    const data = await response.json()
    statistics.value = data
  } catch (error) {
    console.error('获取统计数据失败：', error)
    ElMessage.error('获取统计数据失败')
  }
}

// 格式化进度条显示
const format = (percentage: number) => `${Math.round(percentage)}%`

// 学生反馈列表
interface Feedback {
  id: string
  studentName: string
  selection: string
  isCorrect: boolean
  time: string
}

const feedbackList = ref<Feedback[]>([])

// 模拟WebSocket接收新的反馈
setInterval(() => {
  if (currentQuestion.value) {
    const feedback: Feedback = {
      id: Math.random().toString(36).substr(2, 9),
      studentName: `学生${Math.floor(Math.random() * 100)}`,
      selection: ['A', 'B', 'C', 'D'][Math.floor(Math.random() * 4)],
      isCorrect: Math.random() > 0.5,
      time: new Date().toLocaleTimeString(),
    }
    feedbackList.value.unshift(feedback)
    // 保持最新的10条记录
    if (feedbackList.value.length > 10) {
      feedbackList.value.pop()
    }
  }
}, 5000)

// 演讲吐槽列表
const spitsList = ref<Spit[]>([])

// 刷新吐槽列表
const refreshSpits = async () => {
  try {
    const response = await getSpitsBySpeechId(speechId.value)
    spitsList.value = response.data
  } catch (error) {
    console.error('获取吐槽列表失败：', error)
    ElMessage.error('获取吐槽列表失败')
  }
}

// 评论相关
const commentDialogVisible = ref(false)
const commentsList = ref<ReturnComment[]>([])

// 显示评论对话框
const showComments = async (question: Question) => {
  try {
    const response = await getCommentsByQuestionId(question.questionId)
    commentsList.value = response.data
    commentDialogVisible.value = true
  } catch (error) {
    console.error('获取评论失败：', error)
    ElMessage.error('获取评论失败')
  }
}

// 页面加载时获取吐槽列表
onMounted(() => {
  refreshSpits()
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

.upload-card {
  margin-bottom: 20px;
}

.file-upload {
  width: 100%;
}

.upload-actions {
  margin-top: 20px;
  text-align: center;
}

.questions-card,
.feedback-card,
.feedback-list-card,
.spits-card {
  margin-bottom: 20px;
}

.statistics {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-item h4 {
  margin: 0 0 10px;
  color: #606266;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409eff;
}

.option-distribution {
  padding: 0 20px;
}

.option-item {
  margin-bottom: 15px;
}

.option-label {
  display: inline-block;
  width: 80px;
  margin-right: 10px;
}

.spit-content {
  margin: 0;
  color: #606266;
}

.comments-container {
  max-height: 400px;
  overflow-y: auto;
}

.comment-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.comment-header {
  margin-bottom: 8px;
}

.publisher {
  font-weight: bold;
  color: #409eff;
}

.reply-to {
  margin-left: 10px;
  color: #909399;
}

.comment-content {
  margin: 0;
  color: #606266;
}
</style>
