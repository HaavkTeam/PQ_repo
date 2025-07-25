<template>
  <div class="dashboard-container">
    <!-- 个人信息卡片 -->
    <el-card class="info-card">
      <div class="user-info">
        <el-avatar :size="64" src="https://placeholder.com/150" />
        <div class="user-details">
          <h3>{{ userInfo.username || '未知用户' }}</h3>
          <p>用户ID：{{ userInfo.userId }}</p>
          <p>邮箱：{{ userInfo.email }}</p>
          <p>身份：教师</p>
          <p>主讲演讲次数：{{ lectures.length }}</p>
        </div>
      </div>
    </el-card>

    <!-- 演讲记录卡片 -->
    <el-card class="dashboard-card">
      <template #header>
        <div class="dashboard-header">
          <h2>演讲记录</h2>
          <el-button type="primary" @click="showJoinDialog">加入演讲</el-button>
        </div>
      </template>

      <div class="filter-section">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable>
          <el-option label="进行中" value="1" />
          <el-option label="已结束" value="0" />
        </el-select>
      </div>

      <div class="lecture-list" v-if="filteredLectures.length > 0">
        <el-table :data="filteredLectures" style="width: 100%">
          <el-table-column prop="title" label="演讲标题" />
          <el-table-column prop="organizerName" label="组织者" />
          <el-table-column prop="startTime" label="开始时间">
            <template #default="{ row }">
              {{ formatDate(row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                @click="handleEnterLecture(row)"
                :disabled="row.status !== 1"
              >
                进入演讲
              </el-button>
              <el-button type="info" size="small" @click="showLectureDetails(row)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="暂无演讲记录" />
    </el-card>

    <!-- 加入演讲对话框 -->
    <el-dialog v-model="joinDialogVisible" title="加入演讲" width="400px">
      <el-form :model="joinForm" :rules="rules" ref="joinFormRef" label-width="80px">
        <el-form-item label="验证码" prop="code">
          <el-input v-model="joinForm.code" placeholder="请输入演讲验证码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="joinDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleJoinLecture" :loading="joining">加入</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 演讲详情对话框 -->
    <el-dialog v-model="detailsDialogVisible" title="演讲详情" width="600px">
      <template v-if="selectedLecture">
        <div class="lecture-details">
          <h3>{{ selectedLecture.title }}</h3>
          <p><strong>组织者：</strong>{{ selectedLecture.organizerName }}</p>
          <p><strong>开始时间：</strong>{{ formatDate(selectedLecture.startTime) }}</p>
          <p><strong>结束时间：</strong>{{ formatDate(selectedLecture.endTime) }}</p>
          <p><strong>描述：</strong>{{ selectedLecture.description }}</p>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSpeechesBySpeaker, joinSpeech } from '@/api/speech'
import type { ReturnSpeech } from '@/api/speech'
import type { AxiosError } from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
// 用户信息
const userInfo = reactive({
  username: '',
  email: '',
  userId: '',
})

// 状态筛选
const filterStatus = ref('')

// 演讲列表
const lectures = ref<ReturnSpeech[]>([])

// 加载状态
const loading = ref(false)
const joining = ref(false)

// 筛选后的演讲列表
const filteredLectures = computed(() => {
  if (!filterStatus.value) return lectures.value
  return lectures.value.filter((lecture) => String(lecture.status) === filterStatus.value)
})

// 对话框控制
const joinDialogVisible = ref(false)
const detailsDialogVisible = ref(false)
const selectedLecture = ref<ReturnSpeech | null>(null)

// 加入演讲表单
const joinFormRef = ref()
const joinForm = reactive({
  code: '',
})

// 表单验证规则
const rules = {
  code: [{ required: true, message: '请输入演讲验证码', trigger: 'blur' }],
}

// 获取用户信息
const initUserInfo = () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsedUserInfo = JSON.parse(storedUserInfo)
    userInfo.username = parsedUserInfo.username
    userInfo.email = parsedUserInfo.email
    userInfo.userId = parsedUserInfo.userId
    // 获取演讲列表
    fetchLectures()
  } else {
    ElMessage.error('用户未登录')
    // 可以在这里添加重定向到登录页面的逻辑
  }
}

// 获取演讲列表
const fetchLectures = async () => {
  try {
    loading.value = true
    const response = await getSpeechesBySpeaker(userInfo.userId)
    lectures.value = response.data
  } catch (error) {
    console.error('获取演讲列表失败：', error)
    ElMessage.error('获取演讲列表失败')
  } finally {
    loading.value = false
  }
}

// 显示加入对话框
const showJoinDialog = () => {
  joinDialogVisible.value = true
  joinForm.code = ''
}

// 显示演讲详情
const showLectureDetails = (lecture: ReturnSpeech) => {
  selectedLecture.value = lecture
  detailsDialogVisible.value = true
}

// 格式化日期
const formatDate = (date: Date) => {
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 加入演讲
const handleJoinLecture = async () => {
  if (!joinFormRef.value) return

  try {
    const valid = await joinFormRef.value.validate()
    if (!valid) return

    joining.value = true
    const { data } = await joinSpeech(joinForm.code, userInfo.userId)
    if (data === 'success') {
      ElMessage.success('成功加入演讲')
      joinDialogVisible.value = false

      // 获取最新的演讲列表
      await fetchLectures()

      // 找到刚刚加入的演讲（通常是最新的一个进行中的演讲）
      const joinedLecture = lectures.value.find(
        (lecture) =>
          lecture.speechId === joinForm.code ||
          (lecture.status === 1 && lecture.speechId.includes(joinForm.code)),
      )

      // 如果找到了演讲，自动跳转到演讲页面
      if (joinedLecture) {
        ElMessage.success('正在进入演讲：' + joinedLecture.title)
        router.push(`/teacher/lecture/${joinedLecture.speechId}`)
      }
    } else {
      ElMessage.error(data || '加入演讲失败')
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    ElMessage.error((axiosError.response?.data as string) || '加入演讲失败')
  } finally {
    joining.value = false
  }
}
// 进入演讲
const handleEnterLecture = (lecture: ReturnSpeech) => {
  ElMessageBox.confirm('确定要进入该演讲吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(() => {
      ElMessage.success('正在进入演讲：' + lecture.title)
      router.push(`/teacher/lecture/${lecture.speechId}`)
    })
    .catch(() => {
      // 取消操作
    })
}

// 页面加载时初始化
onMounted(() => {
  initUserInfo()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.user-details h3 {
  margin: 0 0 10px 0;
}

.user-details p {
  margin: 5px 0;
  color: #666;
}

.dashboard-card {
  margin-bottom: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  margin-bottom: 20px;
}

.lecture-list {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.lecture-details h3 {
  margin-top: 0;
}

.lecture-details p {
  margin: 10px 0;
}
</style>
