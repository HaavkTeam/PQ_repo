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
              <el-button type="primary" @click="joinLecture" :loading="loading">加入演讲</el-button>
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
                <el-option label="进行中" value="1" />
                <el-option label="已完成" value="2" />
              </el-select>
            </div>
          </template>
          <el-table :data="filteredLectures" style="width: 100%" v-loading="loading">
            <el-table-column prop="title" label="演讲标题" />
            <el-table-column prop="speakerName" label="演讲者" />
            <el-table-column label="日期" width="180">
              <template #default="{ row }">
                {{ formatDate(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '进行中' : '已完成' }}
                </el-tag>
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
      <el-form :model="joinForm" :rules="rules" ref="joinFormRef">
        <el-form-item label="演讲代码" prop="code">
          <el-input v-model="joinForm.code" placeholder="请输入演讲代码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleJoinLecture" :loading="submitting"
            >加入</el-button
          >
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="detailDialogVisible" title="演讲详情" width="50%">
      <div v-if="selectedLecture">
        <h4>{{ selectedLecture.title }}</h4>
        <el-descriptions border>
          <el-descriptions-item label="演讲者">{{
            selectedLecture.speakerName
          }}</el-descriptions-item>
          <el-descriptions-item label="开始时间">{{
            formatDate(selectedLecture.startTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{
            formatDate(selectedLecture.endTime)
          }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{
            selectedLecture.status === 1 ? '进行中' : '已完成'
          }}</el-descriptions-item>
          <el-descriptions-item label="组织者">{{
            selectedLecture.organizerName
          }}</el-descriptions-item>
        </el-descriptions>

        <div class="description mt-4">
          <h5>演讲描述</h5>
          <p>{{ selectedLecture.description }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMySpeeches, joinSpeech, type ReturnSpeech } from '@/api/speech'
import type { ApiError } from '@/api/auth'
import type { FormInstance } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)

// 用户信息
const userInfo = reactive({
  username: '',
  avatar: '',
  lectureCount: 0,
  quizCount: 0,
  averageScore: 0,
})
// 活动类型定义
interface Activity {
  id: string
  time: string
  type: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  content: string
}

// 在 onMounted 中获取用户信息
onMounted(() => {
  // 从 localStorage 获取用户信息
  const storedUserInfo = localStorage.getItem('userInfo')
  if (!storedUserInfo) {
    ElMessage.error('用户未登录')
    router.push('/auth/login')
    return
  }
  const parsedUserInfo = JSON.parse(storedUserInfo)
  userInfo.username = parsedUserInfo.username || '未知用户'
  userInfo.avatar = parsedUserInfo.avatar || ''
  fetchLectures()
})
// 活动列表
const activities = ref<Activity[]>([])

// 演讲列表
const lectures = ref<ReturnSpeech[]>([])

// 状态筛选
const filterStatus = ref('')
const filteredLectures = computed(() => {
  if (!filterStatus.value) return lectures.value
  return lectures.value.filter((lecture) => String(lecture.status) === filterStatus.value)
})

// 对话框控制
const joinDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const selectedLecture = ref<ReturnSpeech | null>(null)

// 表单相关
const joinFormRef = ref<FormInstance>()
const joinForm = reactive({
  code: '',
})
const rules = {
  code: [{ required: true, message: '请输入演讲代码', trigger: 'blur' }],
}

// 加入演讲
const joinLecture = () => {
  joinDialogVisible.value = true
}

// 修改错误处理部分
const handleJoinLecture = async () => {
  if (!joinFormRef.value) return

  await joinFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const storedUserInfo = localStorage.getItem('userInfo')
        const userId = storedUserInfo ? JSON.parse(storedUserInfo).userId : ''
        if (!userId) {
          throw new Error('用户未登录')
        }
        const response = await joinSpeech(joinForm.code, userId)
        if (response.data === '用户已加入演讲') {
          ElMessage.success('成功加入演讲')
          router.push(`/student/quiz?code=${joinForm.code}`)
          joinDialogVisible.value = false
        } else {
          ElMessage.error(response.data)
        }
      } catch (error) {
        const err = error as ApiError
        console.error('加入演讲失败：', err)
        ElMessage.error(err.response?.data || err.message || '加入演讲失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

// 查看演讲详情
const viewLectureDetail = (lecture: ReturnSpeech) => {
  selectedLecture.value = lecture
  detailDialogVisible.value = true
}

// 格式化日期
const formatDate = (date: Date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 获取演讲列表
// 修改 fetchLectures 函数中的 userId 获取方式
const fetchLectures = async () => {
  loading.value = true
  try {
    const storedUserInfo = localStorage.getItem('userInfo')
    if (!storedUserInfo) {
      throw new Error('用户未登录')
    }
    const parsedUserInfo = JSON.parse(storedUserInfo)
    const userId = parsedUserInfo.userId
    if (!userId) {
      throw new Error('用户信息不完整')
    }
    const response = await getMySpeeches(userId)
    lectures.value = response.data
    userInfo.lectureCount = lectures.value.length
  } catch (error) {
    const err = error as ApiError
    console.error('获取演讲列表失败：', err)
    ElMessage.error(err.response?.data || err.message || '获取演讲列表失败')
    if (err.message === '用户未登录') {
      router.push('/auth/login')
    }
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchLectures()
})
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

.description {
  margin-top: 20px;
}

.description h5 {
  margin-bottom: 12px;
}
</style>
