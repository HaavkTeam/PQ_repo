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

        <!-- 可用题目列表卡片 -->
        <el-card class="questions-card">
          <template #header>
            <div class="card-header">
              <h3>可用题目列表</h3>
              <el-button type="primary" @click="refreshUnusedQuestions">刷新题目</el-button>
            </div>
          </template>

          <el-table
            :data="unusedQuestions"
            style="width: 100%"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55" />
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
            <el-table-column prop="answer" label="答案" width="80" />
          </el-table>

          <div class="table-footer" v-if="selectedQuestions.length > 0">
            <el-button type="primary" @click="handleLaunchTest">发布测试</el-button>
          </div>
        </el-card>

        <!-- 已发布的测试列表 -->
        <el-card class="tests-card">
          <template #header>
            <div class="card-header">
              <h3>已发布的测试</h3>
              <el-button type="primary" @click="handleGetTests">刷新列表</el-button>
            </div>
          </template>

          <el-table :data="tests" style="width: 100%">
            <el-table-column label="序号" type="index" width="80" />
            <el-table-column prop="testId" label="测试ID" />
            <el-table-column prop="state" label="状态">
              <template #default="{ row }">
                <el-tag :type="row.state === '0' ? 'success' : 'info'">
                  {{ row.state === '0' ? '进行中' : '未开始' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button type="primary" size="small" @click="getTestQuestions(row)">
                  查看题目
                </el-button>
                <el-button
                  :type="row.state === '0' ? 'warning' : 'success'"
                  size="small"
                  @click="handleChangeTestStatus(row)"
                >
                  {{ row.state === '0' ? '停用' : '激活' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧区域：演讲吐槽、当前测试题目和答题数据 -->
      <el-col :span="8">
        <!-- 当前测试的题目列表 -->
        <el-card v-if="currentTest && questions.length > 0" class="current-test-card">
          <template #header>
            <div class="card-header">
              <h3>当前测试题目</h3>
              <div class="test-info">
                <span>测试ID：{{ currentTest.testId }}</span>
              </div>
            </div>
          </template>

          <el-collapse v-model="activeQuestions">
            <el-collapse-item
              v-for="question in questions"
              :key="question.questionId"
              :title="question.description"
              :name="question.questionId"
            >
              <div class="question-details">
                <div class="options">
                  <div>A: {{ question.optionA }}</div>
                  <div>B: {{ question.optionB }}</div>
                  <div>C: {{ question.optionC }}</div>
                  <div>D: {{ question.optionD }}</div>
                </div>
                <div class="answer">
                  <strong>答案：{{ question.answer }}</strong>
                </div>
                <div class="actions">
                  <el-button type="info" size="small" @click="showAnswerData(question)">
                    答题数据
                  </el-button>
                  <el-button type="info" size="small" @click="showComments(question)">
                    查看评论
                  </el-button>
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>

        <!-- 答题数据卡片 -->
        <el-card class="answer-data-card" v-if="currentAnswerData">
          <template #header>
            <div class="card-header">
              <h3>答题数据</h3>
            </div>
          </template>

          <div class="answer-statistics">
            <div class="stat-item">
              <h4>总答题人数</h4>
              <div class="stat-value">{{ currentAnswerData.answerNumber }}</div>
            </div>
            <div class="stat-item">
              <h4>正确率</h4>
              <div class="stat-value">{{ currentAnswerData.correctPercentage }}</div>
            </div>
          </div>

          <el-divider>选项分布</el-divider>

          <div class="option-distribution">
            <div class="option-item">
              <span class="option-label">选项 A</span>
              <el-progress
                :percentage="(currentAnswerData.anumber / currentAnswerData.answerNumber) * 100"
                :format="format"
                :stroke-width="20"
                :color="currentAnswerData.answer === 'A' ? '#67C23A' : '#909399'"
              />
            </div>
            <div class="option-item">
              <span class="option-label">选项 B</span>
              <el-progress
                :percentage="(currentAnswerData.bnumber / currentAnswerData.answerNumber) * 100"
                :format="format"
                :stroke-width="20"
                :color="currentAnswerData.answer === 'B' ? '#67C23A' : '#909399'"
              />
            </div>
            <div class="option-item">
              <span class="option-label">选项 C</span>
              <el-progress
                :percentage="(currentAnswerData.cnumber / currentAnswerData.answerNumber) * 100"
                :format="format"
                :stroke-width="20"
                :color="currentAnswerData.answer === 'C' ? '#67C23A' : '#909399'"
              />
            </div>
            <div class="option-item">
              <span class="option-label">选项 D</span>
              <el-progress
                :percentage="(currentAnswerData.dnumber / currentAnswerData.answerNumber) * 100"
                :format="format"
                :stroke-width="20"
                :color="currentAnswerData.answer === 'D' ? '#67C23A' : '#909399'"
              />
            </div>
          </div>
        </el-card>

        <!-- 演讲吐槽展示卡片 -->
        <el-card class="spits-card">
          <template #header>
            <div class="card-header">
              <h3>演讲吐槽</h3>
              <el-button type="primary" size="small" @click="refreshSpits">刷新吐槽</el-button>
            </div>
          </template>

          <div class="spits-list">
            <el-empty v-if="spitsList.length === 0" description="暂无吐槽" />
            <el-timeline v-else>
              <el-timeline-item
                v-for="spit in spitsList"
                :key="`${spit.speechId}-${spit.SpitId}-${spit.time}`"
                :timestamp="formatSpitTime(spit.time)"
                type="primary"
              >
                <div class="spit-item">
                  <p class="spit-content">{{ spit.content }}</p>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
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
            :timestamp="formatDate(comment.time)"
            type="primary"
          >
            <div class="comment-item">
              <div class="comment-header">
                <span class="publisher">{{ comment.publisherName }}</span>
                <span
                  v-if="comment.replyName && !isSystemAccount(comment.replyName)"
                  class="reply-to"
                  >回复 {{ comment.replyName }}</span
                >
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
import { useRoute } from 'vue-router'
import { UploadFilled } from '@element-plus/icons-vue'
import {
  getTestList,
  getQuestionsByTestId,
  getUnusedQuestions,
  launchTest,
  getUserData, // 添加这一行
  changeTestStatus,
  type Question,
  type test,
  type UserAnswerData,
} from '@/api/question'
import { getCommentsByQuestionId, type ReturnComment } from '@/api/comment'
import { getSpitsBySpeechId, type Spit } from '@/api/speech'

const route = useRoute()
const speechId = route.params.id as string

// 文件上传相关
interface UploadFile {
  name: string
  size: number
  type: string
  raw: File
}

// 判断是否是系统账户
const isSystemAccount = (name: string | undefined) => {
  if (!name) return false
  return name === '系统账户' // 替换为实际的系统账户名称
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
    formData.append('speechId', speechId)
    await fetch('/api/question/uploadQuestionFile', {
      method: 'POST',
      body: formData,
    })
    ElMessage.success('文件上传成功')
    // 上传成功后刷新未使用的题目列表
    await refreshUnusedQuestions()
  } catch (error) {
    console.error('文件上传失败：', error)
    ElMessage.error('文件上传失败')
  } finally {
    uploading.value = false
    currentFile.value = null
  }
}

// 未使用的题目列表
const unusedQuestions = ref<Question[]>([])
const selectedQuestions = ref<Question[]>([])

// 刷新未使用的题目列表
const refreshUnusedQuestions = async () => {
  try {
    const response = await getUnusedQuestions(speechId)
    unusedQuestions.value = response.data
  } catch (error) {
    console.error('获取未使用题目失败：', error)
    ElMessage.error('获取未使用题目失败')
  }
}

// 处理题目选择变化
const handleSelectionChange = (selection: Question[]) => {
  selectedQuestions.value = selection
}

// 发布测试
const handleLaunchTest = async () => {
  if (selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择要发布的题目')
    return
  }

  try {
    const questionIds = selectedQuestions.value.map((q) => q.questionId)
    await launchTest(speechId, questionIds)
    ElMessage.success('测试发布成功')
    // 刷新列表
    await Promise.all([refreshUnusedQuestions(), handleGetTests()])
    // 清空选择
    selectedQuestions.value = []
  } catch (error) {
    console.error('发布测试失败：', error)
    ElMessage.error('发布测试失败')
  }
}

// 已发布的测试列表
const tests = ref<test[]>([])
const currentTest = ref<test | null>(null)

// 获取已发布的测试列表
const handleGetTests = async () => {
  if (!speechId) {
    ElMessage.error('演讲ID不能为空')
    return
  }
  try {
    const response = await getTestList(speechId)
    tests.value = response.data
  } catch (error) {
    console.error('获取测试列表失败：', error)
    ElMessage.error('获取测试列表失败')
  }
}

// 当前测试的题目列表
const questions = ref<Question[]>([])
const activeQuestions = ref<string[]>([])

// 获取测试的题目
const getTestQuestions = async (test: test) => {
  try {
    currentTest.value = test
    const response = await getQuestionsByTestId(test.testId)
    questions.value = response.data
    // 默认展开第一个题目
    if (response.data.length > 0) {
      activeQuestions.value = [response.data[0].questionId]
    }
  } catch (error) {
    console.error('获取题目失败：', error)
    ElMessage.error('获取题目失败')
  }
}

// 改变测试状态（激活/停用）
const handleChangeTestStatus = async (test: test) => {
  try {
    const response = await changeTestStatus(test.testId)
    if (response.data === '测试状态已改变') {
      ElMessage.success(test.state === 'active' ? '测试已停用' : '测试已激活')
      // 刷新测试列表
      await handleGetTests()
    } else {
      ElMessage.error('操作失败：' + response.data)
    }
  } catch (error) {
    console.error('改变测试状态失败：', error)
    ElMessage.error('改变测试状态失败')
  }
}

// 答题数据
const currentAnswerData = ref<UserAnswerData | null>(null)

// 显示答题数据
const showAnswerData = async (question: Question) => {
  try {
    if (!currentTest.value) {
      ElMessage.warning('请先选择一个测试')
      return
    }

    // 调用后端API获取答题数据
    const response = await getUserData(currentTest.value.testId)

    // 打印整个响应数据，查看API返回的完整结构
    console.log('API返回的完整数据：', response)
    console.log('API返回的data数据：', response.data)

    // 从返回的数据中找到当前问题的答题数据
    const questionData = response.data.find((item) => item.questionId === question.questionId)

    // 打印找到的问题数据和当前问题ID
    console.log('当前问题ID：', question.questionId)
    console.log('找到的问题数据：', questionData)

    if (questionData) {
      // 打印问题数据的correctPercentage字段及其类型
      console.log('correctPercentage值：', questionData.correctPercentage)
      console.log('correctPercentage类型：', typeof questionData.correctPercentage)

      // 确保correctPercentage是字符串格式并包含百分号
      if (
        typeof questionData.correctPercentage === 'string' &&
        !questionData.correctPercentage.includes('%')
      ) {
        questionData.correctPercentage = `${questionData.correctPercentage}%`
      } else if (typeof questionData.correctPercentage === 'number') {
        questionData.correctPercentage = `${questionData.correctPercentage}%`
      }

      // 打印处理后的数据
      console.log('处理后的数据：', questionData)

      currentAnswerData.value = questionData
    } else {
      // 如果没有找到对应的答题数据，显示一个默认的空数据
      console.log('未找到对应的答题数据，使用默认空数据')

      currentAnswerData.value = {
        questionId: question.questionId,
        description: question.description,
        optionA: question.optionA,
        optionB: question.optionB,
        optionC: question.optionC,
        optionD: question.optionD,
        answer: question.answer,
        anumber: 0,
        bnumber: 0,
        cnumber: 0,
        dnumber: 0,

        answerNumber: 0,
        correctPercentage: '0%',
      }
      ElMessage.info('该题目暂无答题数据')
    }
  } catch (error) {
    console.error('获取答题数据失败：', error)
    ElMessage.error('获取答题数据失败')
    currentAnswerData.value = null
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

// 吐槽列表
const spitsList = ref<Spit[]>([])

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
// 格式化吐槽时间
const formatSpitTime = (time: Date | undefined) => {
  if (!time) return ''
  return new Date(time).toLocaleString()
}

// 格式化日期
const formatDate = (date: Date | undefined) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 格式化进度条显示
const format = (percentage: number) => `${Math.round(percentage)}%`

// 页面加载时获取数据
onMounted(() => {
  handleGetTests()
  refreshUnusedQuestions()
  refreshSpits() // 添加获取吐槽列表
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

.upload-card,
.tests-card,
.questions-card,
.answer-data-card,
.current-test-card,
.spits-card {
  margin-bottom: 20px;
}

.file-upload {
  width: 100%;
}

.upload-actions {
  margin-top: 20px;
  text-align: center;
}

.table-footer {
  margin-top: 20px;
  text-align: right;
}

.test-info {
  color: #909399;
  font-size: 14px;
}

.question-details {
  padding: 10px;
}

.options {
  margin-bottom: 10px;
}

.answer {
  margin-bottom: 10px;
  color: #67c23a;
}

.actions {
  display: flex;
  gap: 10px;
}

.answer-statistics {
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

.spits-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 0 10px;
}

.spit-item {
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.spit-content {
  margin: 0;
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  word-break: break-all;
}
</style>
