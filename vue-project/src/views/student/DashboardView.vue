<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="user-card">
          <template #header>
            <div class="user-header">
              <el-avatar :size="64" :src="userInfo.avatar" />
              <h3>{{ userInfo.username }}</h3>
            </div>
          </template>
          <div class="user-info">
            <p>参与演讲：{{ userInfo.lectureCount }} 次</p>
            <p>完成测验：{{ userInfo.quizCount }} 次</p>
            <p>平均得分：{{ userInfo.averageScore }}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <h3>最近活动</h3>
              <el-button type="primary" @click="joinLecture">加入演讲</el-button>
            </div>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="activity in activities"
              :key="activity.id"
              :timestamp="activity.time"
              :type="activity.type"
            >
              {{ activity.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>

        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <h3>我的演讲记录</h3>
              <el-select v-model="filterStatus" placeholder="状态筛选" class="filter-select">
                <el-option label="全部" value="" />
                <el-option label="进行中" value="ongoing" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </div>
          </template>
          <el-table :data="filteredLectures" style="width: 100%">
            <el-table-column prop="title" label="演讲标题" />
            <el-table-column prop="speaker" label="演讲者" />
            <el-table-column prop="date" label="日期" width="180" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'ongoing' ? 'success' : 'info'">
                  {{ row.status === 'ongoing' ? '进行中' : '已完成' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="答题情况" width="200">
              <template #default="{ row }">
                <div class="quiz-stats">
                  <el-progress
                    :percentage="row.correctRate"
                    :format="(val: number) => val + '%'"
                    :status="getProgressStatus(row.correctRate)"
                  />
                </div>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="viewLectureDetail(row)">
                  查看详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="joinDialogVisible" title="加入演讲" width="30%">
      <el-form :model="joinForm">
        <el-form-item label="演讲代码">
          <el-input v-model="joinForm.code" placeholder="请输入演讲代码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleJoinLecture">加入</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="答题详情" width="50%">
      <div v-if="selectedLecture">
        <h4>{{ selectedLecture.title }}</h4>
        <el-descriptions border>
          <el-descriptions-item label="演讲者">{{ selectedLecture.speaker }}</el-descriptions-item>
          <el-descriptions-item label="日期">{{ selectedLecture.date }}</el-descriptions-item>
          <el-descriptions-item label="正确率"
            >{{ selectedLecture.correctRate }}%</el-descriptions-item
          >
        </el-descriptions>

        <div class="quiz-details mt-4">
          <h5>题目详情</h5>
          <el-table :data="selectedLecture.quizDetails" style="width: 100%">
            <el-table-column prop="questionNumber" label="题号" width="80" />
            <el-table-column prop="question" label="题目" show-overflow-tooltip />
            <el-table-column prop="yourAnswer" label="你的答案" width="120" />
            <el-table-column prop="correctAnswer" label="正确答案" width="120" />
            <el-table-column prop="isCorrect" label="结果" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isCorrect ? 'success' : 'danger'">
                  {{ row.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userInfo = reactive({
  username: '张三',
  avatar: '',
  lectureCount: 10,
  quizCount: 25,
  averageScore: 85,
})

const activities = ref([
  {
    id: 1,
    content: '参加了《Vue.js进阶开发》演讲',
    time: '2024-01-20 14:00',
    type: 'primary',
  },
  {
    id: 2,
    content: '完成测验《组件化开发基础》，得分90分',
    time: '2024-01-20 14:30',
    type: 'success',
  },
])

interface Lecture {
  id: number
  title: string
  speaker: string
  date: string
  status: 'ongoing' | 'completed'
  correctRate: number
  quizDetails: {
    questionNumber: number
    question: string
    yourAnswer: string
    correctAnswer: string
    isCorrect: boolean
  }[]
}

const lectures = ref<Lecture[]>([
  {
    id: 1,
    title: 'Vue.js进阶开发',
    speaker: '李老师',
    date: '2024-01-20',
    status: 'completed',
    correctRate: 90,
    quizDetails: [
      {
        questionNumber: 1,
        question: '什么是Vue的响应式原理？',
        yourAnswer: 'Proxy代理',
        correctAnswer: 'Proxy代理',
        isCorrect: true,
      },
      {
        questionNumber: 2,
        question: '组件通信的方式有哪些？',
        yourAnswer: 'props, emit',
        correctAnswer: 'props, emit, provide/inject',
        isCorrect: false,
      },
    ],
  },
  {
    id: 2,
    title: '组件化开发基础',
    speaker: '王老师',
    date: '2024-01-19',
    status: 'completed',
    correctRate: 85,
    quizDetails: [
      {
        questionNumber: 1,
        question: '什么是组件？',
        yourAnswer: '可复用的代码片段',
        correctAnswer: '可复用的代码片段',
        isCorrect: true,
      },
    ],
  },
  {
    id: 3,
    title: 'TypeScript实战',
    speaker: '张老师',
    date: '2024-01-21',
    status: 'ongoing',
    correctRate: 0,
    quizDetails: [],
  },
])

const filterStatus = ref('')
const filteredLectures = computed(() => {
  if (!filterStatus.value) return lectures.value
  return lectures.value.filter((lecture) => lecture.status === filterStatus.value)
})

const joinDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedLecture = ref<Lecture | null>(null)

const joinForm = reactive({
  code: '',
})

const joinLecture = () => {
  joinDialogVisible.value = true
}

const handleJoinLecture = () => {
  // TODO: 实现加入演讲逻辑
  router.push(`/student/quiz?code=${joinForm.code}`)
  joinDialogVisible.value = false
}

const getProgressStatus = (rate: number) => {
  if (rate >= 90) return 'success'
  if (rate >= 60) return 'warning'
  return 'exception'
}

const viewLectureDetail = (lecture: Lecture) => {
  selectedLecture.value = lecture
  detailDialogVisible.value = true
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.user-card .user-header {
  text-align: center;
}

.user-card .user-info {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.mt-4 {
  margin-top: 16px;
}

.filter-select {
  width: 120px;
}

.quiz-stats {
  padding: 0 10px;
}

.quiz-details {
  margin-top: 20px;
}

.quiz-details h5 {
  margin-bottom: 16px;
}
</style>
