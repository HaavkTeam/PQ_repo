<template>
  <div class="quiz-container">
    <el-row :gutter="20">
      <!-- 左侧个人信息卡片 -->
      <el-col :span="6">
        <el-card class="info-card">
          <template #header>
            <div class="info-header">
              <el-avatar :size="64" :src="studentInfo.avatar" />
              <h3>{{ studentInfo.name }}</h3>
            </div>
          </template>
          <div class="info-content">
            <p>学号：{{ studentInfo.studentId }}</p>
            <p>已答题数：{{ myData.answerNumber }}/{{ myData.allnumber }}</p>
            <p>答题正确率：{{ myData.correctPercentage }}</p>
          </div>
        </el-card>

        <!-- 测试信息卡片 -->
        <el-card class="test-info-card mt-4">
          <template #header>
            <div class="card-header">
              <h3></h3>
            </div>
          </template>
          <div class="test-info-content">
            <el-button type="primary" @click="backToTestList" class="mt-2">返回测试列表</el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧答题区域 -->
      <el-col :span="18">
        <!-- 答题卡片 -->
        <el-card class="quiz-card" v-loading="loading">
          <template #header>
            <div class="quiz-header">
              <h2>测试：{{ testId }}</h2>
              <div class="quiz-navigation">
                <el-pagination
                  v-model:current-page="currentIndex"
                  :page-size="1"
                  layout="prev, pager, next"
                  :total="questions.length"
                  @current-change="handlePageChange"
                  hide-on-single-page
                />
              </div>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <div v-else-if="questions.length === 0" class="empty-container">
            <el-empty description="当前测试中暂无题目" />
          </div>

          <div v-else class="question-content">
            <div class="question-navigation">
              <span class="question-counter"
                >当前第 {{ currentIndex }} 题，共 {{ questions.length }} 题</span
              >
            </div>

            <p class="question-text">{{ currentQuestion.description }}</p>

            <el-radio-group
              v-model="selectedAnswer"
              class="answer-options"
              :disabled="isAnswered(currentQuestion.questionId)"
            >
              <el-radio
                label="A"
                class="answer-option"
                :class="{
                  'correct-answer':
                    isAnswered(currentQuestion.questionId) && currentQuestion.answer === 'A',
                  'wrong-answer':
                    isAnswered(currentQuestion.questionId) &&
                    userAnswer === 'A' &&
                    currentQuestion.answer !== 'A',
                }"
              >
                {{ currentQuestion.optionA }}
              </el-radio>
              <el-radio
                label="B"
                class="answer-option"
                :class="{
                  'correct-answer':
                    isAnswered(currentQuestion.questionId) && currentQuestion.answer === 'B',
                  'wrong-answer':
                    isAnswered(currentQuestion.questionId) &&
                    userAnswer === 'B' &&
                    currentQuestion.answer !== 'B',
                }"
              >
                {{ currentQuestion.optionB }}
              </el-radio>
              <el-radio
                label="C"
                class="answer-option"
                :class="{
                  'correct-answer':
                    isAnswered(currentQuestion.questionId) && currentQuestion.answer === 'C',
                  'wrong-answer':
                    isAnswered(currentQuestion.questionId) &&
                    userAnswer === 'C' &&
                    currentQuestion.answer !== 'C',
                }"
              >
                {{ currentQuestion.optionC }}
              </el-radio>
              <el-radio
                label="D"
                class="answer-option"
                :class="{
                  'correct-answer':
                    isAnswered(currentQuestion.questionId) && currentQuestion.answer === 'D',
                  'wrong-answer':
                    isAnswered(currentQuestion.questionId) &&
                    userAnswer === 'D' &&
                    currentQuestion.answer !== 'D',
                }"
              >
                {{ currentQuestion.optionD }}
              </el-radio>
            </el-radio-group>

            <div v-if="isAnswered(currentQuestion.questionId)" class="answer-result">
              <el-alert
                :title="userAnswer === currentQuestion.answer ? '回答正确' : '回答错误'"
                :type="userAnswer === currentQuestion.answer ? 'success' : 'error'"
                show-icon
              >
                <template #default>
                  <p>您的答案：{{ userAnswer }}</p>
                  <p>正确答案：{{ currentQuestion.answer }}</p>
                </template>
              </el-alert>
            </div>

            <div class="action-buttons">
              <el-button
                type="primary"
                @click="handleSubmitAnswer"
                :loading="submitting"
                :disabled="!selectedAnswer || submitting || isAnswered(currentQuestion.questionId)"
              >
                提交答案
              </el-button>
            </div>

            <!-- 评论区域 -->
            <div class="comments-section">
              <el-divider>评论区</el-divider>

              <!-- 评论输入框 -->
              <div class="comment-input">
                <el-input
                  v-model="commentContent"
                  type="textarea"
                  :rows="2"
                  placeholder="发表你的评论"
                />
                <div class="action-buttons">
                  <el-button
                    type="primary"
                    @click="handleCommentSubmit"
                    :loading="commentSubmitting"
                    :disabled="!commentContent.trim()"
                  >
                    发表评论
                  </el-button>
                </div>
              </div>

              <!-- 评论列表 -->
              <div class="comment-list">
                <div v-for="comment in comments" :key="comment.commentId" class="comment-item">
                  <div class="comment-header">
                    <span class="publisher">{{ comment.publisherName }}</span>
                    <span
                      v-if="comment.replyName && !isSystemAccount(comment.replyName)"
                      class="reply-to"
                      >回复 {{ comment.replyName }}
                    </span>
                    <span class="comment-time">{{ formatDate(comment.time) }}</span>
                  </div>
                  <p class="comment-content">{{ comment.content }}</p>
                  <div class="comment-actions">
                    <el-button type="text" @click="showReplyDialog(comment)">回复</el-button>
                  </div>
                </div>
                <div v-if="comments.length === 0" class="no-comments">
                  暂无评论，快来发表第一条评论吧！
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 回复对话框 -->
    <el-dialog v-model="replyDialogVisible" title="回复评论" width="50%">
      <el-input
        v-model="replyContent"
        type="textarea"
        :rows="3"
        :placeholder="`回复 ${replyTo.publisherName}`"
      />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleReplySubmit" :loading="replySubmitting">
            发表回复
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getQuestionsByTestId, submitAnswer, getMySubmit, getMyData } from '@/api/question'
import type { Question, UserSubmit, ReturnSubmit, MyData } from '@/api/question'
import { joinSpeech } from '@/api/speech'
import {
  getCommentsByQuestionId,
  addComment,
  type Comment,
  type ReturnComment,
} from '@/api/comment'

const route = useRoute()
const router = useRouter()
const speechId = route.query.code as string
const testId = route.query.testId as string

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 判断是否是系统账户
const isSystemAccount = (name: string | undefined) => {
  if (!name) return false
  return name === '系统账户' // 替换为实际的系统账户名称
}

// 学生信息
const studentInfo = reactive({
  name: '',
  studentId: '',
  avatar: '',
})

// 个人数据
const myData = reactive<MyData>({
  userId: '',
  answerNumber: 0, // 改为小写开头
  allnumber: 0, // 改为小写开头
  correctPercentage: '0%', // 改为小写开头
})

// 题目相关状态
const selectedAnswer = ref('')
const answeredQuestions = ref<ReturnSubmit[]>([]) // 修改为保存完整的提交记录

// 题目列表和当前索引
const questions = ref<Question[]>([])
const currentIndex = ref(1) // 从1开始，用于分页

// 当前题目
const currentQuestion = computed(() => {
  if (questions.value.length === 0 || currentIndex.value === 0) {
    return {
      questionId: '',
      testId: '',
      description: '',
      optionA: '',
      optionB: '',
      optionC: '',
      optionD: '',
      answer: '',
      isUsed: false,
    }
  }
  return questions.value[currentIndex.value - 1]
})

// 当前题目的用户答案
const userAnswer = computed(() => {
  const submission = answeredQuestions.value.find(
    (submit) => submit.questionId === currentQuestion.value.questionId,
  )
  // 如果selection为-1，表示未回答，返回空字符串
  return submission ? (submission.selection === '-1' ? '' : submission.selection) : ''
})

// 评论相关
const comments = ref<ReturnComment[]>([])
const commentContent = ref('')
const commentSubmitting = ref(false)
const replyDialogVisible = ref(false)
const replyContent = ref('')
const replySubmitting = ref(false)
const replyTo = reactive({
  commentId: '',
  publisherName: '',
})

// 初始化用户信息
const initUserInfo = async () => {
  console.log('开始 initUserInfo，speechId:', speechId)
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsedUserInfo = JSON.parse(storedUserInfo)
    studentInfo.name = parsedUserInfo.username
    studentInfo.studentId = parsedUserInfo.userId
    studentInfo.avatar = parsedUserInfo.avatar || ''

    console.log('准备调用 joinSpeech，参数:', { speechId, userId: studentInfo.studentId })

    try {
      const joinResponse = await joinSpeech(speechId, studentInfo.studentId)
      console.log('加入演讲响应:', joinResponse)
      if (joinResponse.data !== '用户已加入演讲') {
        ElMessage.error('用户未加入演讲或演讲不存在')
        router.push('/student/dashboard')
        return
      }
    } catch (error) {
      console.error('加入演讲失败:', error)
      ElMessage.error('加入演讲失败')
      router.push('/student/dashboard')
      return
    }
  } else {
    ElMessage.error('用户信息不存在，请重新登录')
    router.push('/auth/login')
  }
}

// 获取演讲问题
const fetchQuestions = async () => {
  if (!testId) return

  loading.value = true
  try {
    const response = await getQuestionsByTestId(testId)
    // 先检查是否有题目
    if (!response.data || response.data.length === 0) {
      questions.value = []
      ElMessage.warning('当前测试暂无题目')
      return
    }

    // 保存所有题目
    questions.value = response.data
    currentIndex.value = 1

    // 获取题目评论
    await fetchComments()
  } catch (error) {
    console.error('获取问题失败：', error)
    ElMessage.error('获取问题失败')
  } finally {
    loading.value = false
  }
}

// 获取提交历史
const fetchSubmissionHistory = async () => {
  try {
    const response = await getMySubmit(studentInfo.studentId, speechId)
    // 过滤掉没有selection的记录
    answeredQuestions.value = response.data.filter((submit) => submit.selection)
  } catch (error) {
    console.error('获取提交历史失败：', error)
  }
}
//获取个人信息
const fetchMyData = async () => {
  try {
    const response = await getMyData(studentInfo.studentId, speechId)
    console.log('获取个人数据返回：', response.data)

    // 直接赋值，不需要手动映射
    Object.assign(myData, response.data)
  } catch (error) {
    console.error('获取个人数据失败：', error)
  }
}
// 检查题目是否已回答
const isAnswered = (questionId: string) => {
  const submission = answeredQuestions.value.find((submit) => submit.questionId === questionId)
  // 如果submission存在且selection不为-1，则表示已回答
  return submission && submission.selection && submission.selection !== '-1' ? true : false
}

// 处理分页变化
const handlePageChange = (page: number) => {
  currentIndex.value = page
  // 如果已经回答过，显示用户的选择，否则清空选择
  selectedAnswer.value = userAnswer.value || ''
  fetchComments() // 确保在题目切换时获取新题目的评论
}

// 格式化日期
const formatDate = (date: Date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 提交答案
const handleSubmitAnswer = async () => {
  if (!selectedAnswer.value || !testId) return

  submitting.value = true
  try {
    const userSubmit: UserSubmit = {
      userId: studentInfo.studentId,
      questionId: currentQuestion.value.questionId,
      selection: selectedAnswer.value,
      // 让后端计算是否正确，不在前端判断
      // isCorrect: selectedAnswer.value === currentQuestion.value.answer ? 1 : 0,
    }

    const response = await submitAnswer(userSubmit)
    if (response.data === '答案提交成功') {
      ElMessage.success('答案提交成功')
      // 更新提交历史和个人数据
      await Promise.all([fetchSubmissionHistory(), fetchMyData()])
    } else {
      ElMessage.error(response.data)
    }
  } catch (error) {
    console.error('提交答案失败：', error)
    ElMessage.error('提交答案失败')
  } finally {
    submitting.value = false
  }
}

// 返回测试列表
const backToTestList = () => {
  router.push({
    path: '/student/tests',
    query: { code: speechId },
  })
}

// 获取题目评论
const fetchComments = async () => {
  if (!currentQuestion.value.questionId) return

  try {
    const response = await getCommentsByQuestionId(currentQuestion.value.questionId)
    comments.value = response.data
  } catch (error) {
    console.error('获取评论失败：', error)
    ElMessage.error('获取评论失败')
  }
}

// 提交评论
const handleCommentSubmit = async () => {
  if (!commentContent.value.trim()) return

  commentSubmitting.value = true
  try {
    const comment: Comment = {
      questionId: currentQuestion.value.questionId,
      publisher: studentInfo.studentId,
      content: commentContent.value,
      time: new Date(), // 添加当前时间
    }

    const success = await addComment(comment)
    if (success) {
      ElMessage.success('评论发表成功')
      commentContent.value = ''
      await fetchComments()
    } else {
      ElMessage.error('评论发表失败')
    }
  } catch (error) {
    console.error('发表评论失败：', error)
    ElMessage.error('发表评论失败')
  } finally {
    commentSubmitting.value = false
  }
}
// 显示回复对话框
const showReplyDialog = (comment: ReturnComment) => {
  replyTo.commentId = comment.commentId
  replyTo.publisherName = comment.publisherName
  replyDialogVisible.value = true
}

// 提交回复
const handleReplySubmit = async () => {
  if (!replyContent.value.trim()) return

  replySubmitting.value = true
  try {
    const comment: Comment = {
      questionId: currentQuestion.value.questionId,
      publisher: studentInfo.studentId,
      replyId: replyTo.commentId,
      content: replyContent.value,
      time: new Date(), // 添加当前时间
    }

    const success = await addComment(comment)
    if (success) {
      ElMessage.success('回复发表成功')
      replyContent.value = ''
      replyDialogVisible.value = false
      await fetchComments()
    } else {
      ElMessage.error('回复发表失败')
    }
  } catch (error) {
    console.error('发表回复失败：', error)
    ElMessage.error('发表回复失败')
  } finally {
    replySubmitting.value = false
  }
}

// 监听路由参数变化
watch(
  () => route.query,
  async () => {
    if (route.query.testId !== testId) {
      await fetchQuestions()
      await fetchSubmissionHistory()
    }
  },
)

onMounted(async () => {
  if (!speechId || !testId) {
    ElMessage.error('缺少必要参数')
    router.push('/student/dashboard')
    return
  }

  await initUserInfo()
  await Promise.all([fetchQuestions(), fetchSubmissionHistory(), fetchMyData()])
})
</script>

<style scoped>
.quiz-container {
  padding: 20px;
}

.info-card .info-header {
  text-align: center;
}

.info-card .info-content {
  margin-top: 20px;
}

.quiz-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-content {
  padding: 20px 0;
}

.question-text {
  font-size: 18px;
  margin-bottom: 20px;
}

.answer-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.answer-option {
  padding: 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.correct-answer {
  background-color: rgba(103, 194, 58, 0.1);
  border: 1px solid #67c23a;
}

.wrong-answer {
  background-color: rgba(245, 108, 108, 0.1);
  border: 1px solid #f56c6c;
}

.answer-result {
  margin: 16px 0;
}

.action-buttons {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.comments-section {
  margin-top: 30px;
}

.comment-input {
  margin-bottom: 20px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 12px;
  border-bottom: 1px solid #eee;
  margin-bottom: 12px;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.publisher {
  font-weight: bold;
  margin-right: 8px;
}

.reply-to {
  color: #909399;
  font-size: 0.9em;
}

.comment-content {
  margin: 8px 0;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.mt-2 {
  margin-top: 8px;
}

.mt-4 {
  margin-top: 16px;
}

.loading-container,
.empty-container {
  padding: 40px 0;
  text-align: center;
}
</style>
