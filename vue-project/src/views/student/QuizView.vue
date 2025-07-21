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
            <p>班级：{{ studentInfo.className }}</p>
            <p>参与演讲：{{ studentInfo.lectureCount }}次</p>
            <p>答题正确率：{{ studentInfo.accuracy }}%</p>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧答题区域 -->
      <el-col :span="18">
        <el-card class="quiz-card">
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
            <p class="question-text">{{ currentQuestion.content }}</p>

            <el-radio-group v-model="selectedAnswer" class="answer-options">
              <el-radio
                v-for="option in currentQuestion.options"
                :key="option.key"
                :label="option.key"
                class="answer-option"
              >
                {{ option.text }}
              </el-radio>
            </el-radio-group>

            <div class="feedback-section">
              <el-input
                v-model="feedback"
                type="textarea"
                :rows="3"
                placeholder="如果您对这道题目有任何疑问或反馈，请在这里填写"
              />
            </div>

            <div class="action-buttons">
              <el-button type="primary" @click="submitAnswer" :disabled="!selectedAnswer">
                提交答案
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

// 学生信息
const studentInfo = reactive({
  name: '张三',
  studentId: '2024001',
  className: '计算机科学2024级1班',
  avatar: '',
  lectureCount: 10,
  accuracy: 85,
})

// 演讲信息
const lectureInfo = reactive({
  title: 'Vue.js 高级开发技巧',
  status: 'active' as 'active' | 'waiting',
})

// 题目相关状态
const hasQuestion = ref(false)
const questionAccepted = ref(false)
const selectedAnswer = ref('')
const feedback = ref('')

// 当前题目
const currentQuestion = reactive({
  content: '',
  options: [] as { key: string; text: string }[],
})

// 接受新题目
const acceptQuestion = () => {
  questionAccepted.value = true
  // 模拟题目数据
  currentQuestion.content = '以下哪个不是 Vue.js 的生命周期钩子？'
  currentQuestion.options = [
    { key: 'A', text: 'mounted' },
    { key: 'B', text: 'created' },
    { key: 'C', text: 'rendered' },
    { key: 'D', text: 'updated' },
  ]
}

// 提交答案
const submitAnswer = () => {
  // TODO: 提交答案到后端
  console.log('提交答案:', {
    answer: selectedAnswer.value,
    feedback: feedback.value,
  })

  // 重置状态
  selectedAnswer.value = ''
  feedback.value = ''
  hasQuestion.value = false
  questionAccepted.value = false
}

// 模拟接收新题目（实际项目中应该通过 WebSocket 接收）
setTimeout(() => {
  hasQuestion.value = true
}, 2000)
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

.feedback-section {
  margin: 20px 0;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
