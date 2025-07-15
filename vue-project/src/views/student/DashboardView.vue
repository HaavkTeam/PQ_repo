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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userInfo = reactive({
  username: '张三',
  avatar: '',
  lectureCount: 10,
  quizCount: 25,
  averageScore: 85
})

const activities = ref([
  {
    id: 1,
    content: '参加了《Vue.js进阶开发》演讲',
    time: '2024-01-20 14:00',
    type: 'primary'
  },
  {
    id: 2,
    content: '完成测验《组件化开发基础》，得分90分',
    time: '2024-01-20 14:30',
    type: 'success'
  }
])

const joinDialogVisible = ref(false)
const joinForm = reactive({
  code: ''
})

const joinLecture = () => {
  joinDialogVisible.value = true
}

const handleJoinLecture = () => {
  // TODO: 实现加入演讲逻辑
  router.push(`/student/quiz?code=${joinForm.code}`)
  joinDialogVisible.value = false
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
</style>