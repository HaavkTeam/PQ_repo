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
            <p>参与演讲：{{ studentInfo.lectureCount }}次</p>
            <p>答题正确率：{{ studentInfo.accuracy }}%</p>
          </div>
        </el-card>

        <!-- 答题记录卡片 -->
        <el-card class="history-card mt-4">
          <template #header>
            <div class="card-header">
              <h3>答题记录</h3>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="submit in submissionHistory"
              :key="submit.questionId"
              :type="submit.isCorrect ? 'success' : 'danger'"
              :timestamp="submit.timestamp"
            >
              {{ submit.isCorrect ? '回答正确' : '回答错误' }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>

      <!-- 右侧答题区域 -->
      <el-col :span="18">
        <!-- 演讲吐槽卡片 -->
        <el-card class="spit-card mb-4">
          <template #header>
            <div class="card-header">
              <h3>演讲反馈</h3>
            </div>
          </template>

          <el-input
            v-model="spitContent"
            type="textarea"
            :rows="3"
            placeholder="如果您对本次演讲有任何想法或建议，请在这里填写"
          />
          <div class="action-buttons">
            <el-button type="primary" @click="handleSpitSubmit" :loading="spitSubmitting">
              提交反馈
            </el-button>
          </div>
        </el-card>

        <!-- 答题卡片 -->
        <el-card class="quiz-card" v-loading="loading">
          <template #header>
            <div class="quiz-header">
              <h2>{{ lectureInfo.title }}</h2>
              <div class="lecture-status">
                <el-tag :type="lectureInfo.status === 'active' ? 'success' : 'info'">
                  {{ lectureInfo.status === 'active' ? '演讲进行中' : '等待题目' }}
                </el-tag>
              </div>
            </div>
          </template>

          <div v-if="!hasQuestion" class="waiting-content">
            <el-empty description="当前演讲中暂无题目" />
          </div>

          <div v-else-if="!questionAccepted" class="question-notice">
            <el-result icon="info" title="新题目已发布" sub-title="准备好开始答题了吗？">
              <template #extra>
                <el-button type="primary" @click="acceptQuestion">开始答题</el-button>
              </template>
            </el-result>
          </div>

          <div v-else class="question-content">
            <p class="question-text">{{ currentQuestion.description }}</p>

            <el-radio-group v-model="selectedAnswer" class="answer-options">
              <el-radio label="A" class="answer-option">
                {{ currentQuestion.optionA }}
              </el-radio>
              <el-radio label="B" class="answer-option">
                {{ currentQuestion.optionB }}
              </el-radio>
              <el-radio label="C" class="answer-option">
                {{ currentQuestion.optionC }}
              </el-radio>
              <el-radio label="D" class="answer-option">
                {{ currentQuestion.optionD }}
              </el-radio>
            </el-radio-group>

            <!-- 评论区域 -->
            <div class="comments-section">
              <el-divider>评论区</el-divider>

              <!-- 评论输入框 -->
              <div class="comment-input">
                <el-input
                  v-model="commentContent"
                  type="textarea"
                  :rows="2"
                  placeholder="发表您的评论"
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
                <el-timeline>
                  <el-timeline-item
                    v-for="comment in comments"
                    :key="comment.commentId"
                    :timestamp="formatTime(comment.time)"
                  >
                    <div class="comment-item">
                      <div class="comment-header">
                        <span class="publisher">{{ comment.publisherName }}</span>
                        <span v-if="comment.replyName" class="reply-to">
                          回复 {{ comment.replyName }}
                        </span>
                      </div>
                      <div class="comment-content">{{ comment.content }}</div>
                      <div class="comment-actions">
                        <el-button type="text" @click="handleReply(comment)"> 回复 </el-button>
                      </div>
                    </div>
                  </el-timeline-item>
                </el-timeline>
              </div>
            </div>

            <div class="action-buttons">
              <el-button
                type="primary"
                @click="handleSubmitAnswer"
                :loading="submitting"
                :disabled="!selectedAnswer || submitting"
              >
                提交答案
              </el-button>
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
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getQuestionsById, submitAnswer, getMySubmit } from '@/api/question'
import type { Question, UserSubmit, ReturnSubmit } from '@/api/question'
import { spikeSpeech, type Spit } from '@/api/speech'
import {
  getCommentsByQuestionId,
  addComment,
  type Comment,
  type ReturnComment,
} from '@/api/comment'

const route = useRoute()
const speechId = route.query.code as string

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 学生信息
const studentInfo = reactive({
  name: '',
  studentId: '',
  avatar: '',
  lectureCount: 0,
  accuracy: 0,
})

// 演讲信息
const lectureInfo = reactive({
  title: '',
  status: 'waiting' as 'active' | 'waiting',
})

// 题目相关状态
const hasQuestion = ref(false)
const questionAccepted = ref(false)
const selectedAnswer = ref('')

// 当前题目
const currentQuestion = reactive<Question>({
  questionId: '',
  speechId: '',
  description: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: '',
  isUsed: false,
})

// 提交历史
const submissionHistory = ref<(ReturnSubmit & { timestamp: string })[]>([])

// 吐槽相关
const spitContent = ref('')
const spitSubmitting = ref(false)

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
const initUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsedUserInfo = JSON.parse(storedUserInfo)
    studentInfo.name = parsedUserInfo.username
    studentInfo.studentId = parsedUserInfo.userId
    studentInfo.avatar = parsedUserInfo.avatar || ''
  } else {
    ElMessage.error('用户未登录')
  }
}

// 获取演讲问题
const fetchQuestions = async () => {
  loading.value = true
  try {
    const response = await getQuestionsById(speechId)
    if (response.data.length > 0) {
      const question = response.data[0]
      Object.assign(currentQuestion, question)
      hasQuestion.value = true
      lectureInfo.status = 'active'
      // 获取题目评论
      await fetchComments()
    }
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
    submissionHistory.value = response.data.map((submit) => ({
      ...submit,
      timestamp: new Date().toLocaleString(),
    }))
  } catch (error) {
    console.error('获取提交历史失败：', error)
  }
}

// 接受新题目
const acceptQuestion = () => {
  questionAccepted.value = true
}

// 提交答案
const handleSubmitAnswer = async () => {
  if (!selectedAnswer.value) return

  submitting.value = true
  try {
    const userSubmit: UserSubmit = {
      userId: studentInfo.studentId,
      questionId: currentQuestion.questionId,
      selection: selectedAnswer.value,
    }

    const response = await submitAnswer(userSubmit)
    if (response.data === '答案提交成功') {
      ElMessage.success('答案提交成功')
      // 更新提交历史
      await fetchSubmissionHistory()
      // 重置状态
      selectedAnswer.value = ''
      hasQuestion.value = false
      questionAccepted.value = false
      // 获取新题目
      await fetchQuestions()
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

// 提交演讲吐槽
const handleSpitSubmit = async () => {
  if (!spitContent.value.trim()) return

  spitSubmitting.value = true
  try {
    const spit: Spit = {
      speechId,
      userId: studentInfo.studentId,
      content: spitContent.value,
    }

    const response = await spikeSpeech(spit)
    if (response.data === '演讲吐槽成功') {
      ElMessage.success('反馈提交成功')
      spitContent.value = ''
    } else {
      ElMessage.error(response.data)
    }
  } catch (error) {
    console.error('提交反馈失败：', error)
    ElMessage.error('提交反馈失败')
  } finally {
    spitSubmitting.value = false
  }
}

// 获取评论列表
const fetchComments = async () => {
  if (!currentQuestion.questionId) return

  try {
    const response = await getCommentsByQuestionId(currentQuestion.questionId)
    comments.value = response.data
  } catch (error) {
    console.error('获取评论失败：', error)
  }
}

// 提交评论
const handleCommentSubmit = async () => {
  if (!commentContent.value.trim()) return

  commentSubmitting.value = true
  try {
    const comment: Comment = {
      questionId: currentQuestion.questionId,
      publisher: studentInfo.studentId,
      content: commentContent.value,
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

// 打开回复对话框
const handleReply = (comment: ReturnComment) => {
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
      questionId: currentQuestion.questionId,
      publisher: studentInfo.studentId,
      replyId: replyTo.commentId,
      content: replyContent.value,
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

// 格式化时间
const formatTime = (time: Date) => {
  return new Date(time).toLocaleString()
}

onMounted(async () => {
  if (!speechId) {
    ElMessage.error('缺少演讲代码')
    return
  }

  initUserInfo()
  await Promise.all([fetchQuestions(), fetchSubmissionHistory()])
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

.info-content p {
  margin: 10px 0;
  color: #666;
}

.quiz-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.waiting-content {
  padding: 40px 0;
  text-align: center;
}

.question-notice {
  padding: 20px 0;
}

.question-content {
  padding: 20px 0;
}

.question-text {
  font-size: 18px;
  margin-bottom: 30px;
}

.answer-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.answer-option {
  margin-left: 0;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.mt-4 {
  margin-top: 16px;
}

.mb-4 {
  margin-bottom: 16px;
}

.history-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.spit-card {
  margin-bottom: 20px;
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
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.comment-header {
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
  margin-bottom: 8px;
  line-height: 1.5;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
