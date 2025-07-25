<template>
  <div class="dashboard-container">
    <!-- 个人信息卡片 -->
    <el-card class="info-card">
      <div class="user-info">
        <el-avatar :size="64" src="https://placeholder.com/150" />
        <div class="user-details">
          <h3>{{ userInfo.username || '未知用户' }}</h3>
          <p>邮箱：{{ userInfo.email }}</p>
          <p>身份：组织者</p>
          <p>组织演讲次数：{{ lectures.length }}</p>
        </div>
      </div>
    </el-card>

    <el-card class="dashboard-card">
      <template #header>
        <div class="dashboard-header">
          <h2>组织者控制面板</h2>
          <el-button type="primary" @click="showCreateDialog">创建新演讲</el-button>
        </div>
      </template>

      <div class="filter-section">
        <el-select v-model="filterStatus" placeholder="状态筛选" clearable>
          <el-option label="进行中" value="1" />
          <el-option label="已结束" value="0" />
        </el-select>
      </div>

      <div class="lecture-list" v-if="filteredLectures.length > 0">
        <el-table :data="filteredLectures" style="width: 100%" v-loading="loading">
          <el-table-column prop="title" label="演讲标题" />
          <el-table-column prop="speakerName" label="演讲者" />
          <el-table-column prop="startTime" label="开始时间">
            <template #default="{ row }">
              {{ formatDate(row.startTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="speechId" label="验证码" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">
                {{ row.status === 1 ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="enterLecture(row)">
                {{ row.status === 1 ? '进入演讲' : '查看数据' }}
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleEndLecture(row)"
                :disabled="row.status !== 1"
              >
                结束演讲
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="暂无演讲" />
    </el-card>

    <!-- 创建演讲对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建新演讲" width="500px">
      <el-form :model="lectureForm" :rules="rules" ref="lectureFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="lectureForm.title" placeholder="请输入演讲标题" />
        </el-form-item>
        <el-form-item label="演讲者" prop="speaker">
          <el-input v-model="lectureForm.speaker" placeholder="请输入演讲者姓名" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="lectureForm.description"
            type="textarea"
            placeholder="请输入演讲描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreateLecture" :loading="creating"
            >创建</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import type { AxiosError } from 'axios'
import { getSpeechesByOrganizer, addSpeech, endSpeech } from '@/api/speech'
import type { ReturnSpeech, Speech } from '@/api/speech'
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
const creating = ref(false)

// 筛选后的演讲列表
const filteredLectures = computed(() => {
  if (!filterStatus.value) return lectures.value
  return lectures.value.filter((lecture) => String(lecture.status) === filterStatus.value)
})

// 创建演讲表单
const createDialogVisible = ref(false)
const lectureFormRef = ref()
const lectureForm = reactive({
  title: '',
  speaker: '',
  description: '',
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入演讲标题', trigger: 'blur' }],
  speaker: [{ required: true, message: '请输入演讲者姓名', trigger: 'blur' }],
  description: [{ required: true, message: '请输入演讲描述', trigger: 'blur' }],
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
    const response = await getSpeechesByOrganizer(userInfo.userId)
    lectures.value = response.data
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('获取演讲列表失败：', error)
    ElMessage.error((axiosError.response?.data as string) || '获取演讲列表失败')
  } finally {
    loading.value = false
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  createDialogVisible.value = true
  lectureForm.title = ''
  lectureForm.speaker = ''
  lectureForm.description = ''
}

// 创建演讲
const handleCreateLecture = async () => {
  if (!lectureFormRef.value) return

  try {
    const valid = await lectureFormRef.value.validate()
    if (!valid) return

    creating.value = true
    const newSpeech: Speech = {
      speechId: '', // 后端会自动生成
      title: lectureForm.title,
      speaker: lectureForm.speaker,
      description: lectureForm.description,
      organizer: userInfo.userId,
      startTime: new Date(),
      endTime: new Date(),
      status: 1,
    }

    const response = await addSpeech(newSpeech)
    ElMessage.success(response.data)
    createDialogVisible.value = false
    // 重新获取演讲列表
    fetchLectures()
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    ElMessage.error((axiosError.response?.data as string) || '创建演讲失败')
  } finally {
    creating.value = false
  }
}
// 结束演讲
const handleEndLecture = (lecture: ReturnSpeech) => {
  ElMessageBox.confirm('确定要结束这场演讲吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const response = await endSpeech(lecture.speechId)
        ElMessage.success(response.data)
        // 重新获取演讲列表
        fetchLectures()
      } catch (error: unknown) {
        const axiosError = error as AxiosError
        ElMessage.error((axiosError.response?.data as string) || '结束演讲失败')
      }
    })
    .catch(() => {
      // 取消操作
    })
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

// 进入演讲
const enterLecture = (lecture: ReturnSpeech) => {
  router.push(`/organizer/lecture/${lecture.speechId}`)
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
</style>
