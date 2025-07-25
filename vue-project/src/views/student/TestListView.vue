<template>
  <div class="test-list-container">
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

        <!-- 演讲反馈卡片 -->
        <el-card class="feedback-card mt-4">
          <template #header>
            <div class="card-header">
              <h3>演讲反馈</h3>
            </div>
          </template>
          <div class="feedback-content">
            <el-input
              v-model="spitContent"
              type="textarea"
              :rows="3"
              placeholder="请输入您对演讲的反馈或吐槽..."
            />
            <el-button
              type="primary"
              class="mt-2 w-100"
              :loading="spitSubmitting"
              @click="handleSpitSubmit"
            >
              提交反馈
            </el-button>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧测试列表区域 -->
      <el-col :span="18">
        <el-card class="tests-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <h3>可用测试列表</h3>
              <el-button type="primary" @click="fetchTests">刷新列表</el-button>
            </div>
          </template>

          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="5" animated />
          </div>

          <div v-else-if="tests.length === 0" class="empty-container">
            <el-empty description="当前演讲中暂无可用测试" />
          </div>

          <div v-else>
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
                  <el-button
                    type="primary"
                    size="small"
                    @click="startTest(row)"
                    :disabled="row.state !== '0'"
                  >
                    开始答题
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getTestList } from '@/api/question'
import { spikeSpeech, joinSpeech } from '@/api/speech'
import type { test, MyData } from '@/api/question'
import { getMyData } from '@/api/question'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const speechId = route.query.code as string

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

// 测试列表
const tests = ref<test[]>([])
// 添加定时刷新计时器
const refreshTimer = ref<number | null>(null)

// 吐槽相关
const spitContent = ref('')
const spitSubmitting = ref(false)

// 初始化用户信息
const initUserInfo = async () => {
  const storedUserInfo = localStorage.getItem('userInfo')
  if (storedUserInfo) {
    const parsedUserInfo = JSON.parse(storedUserInfo)
    studentInfo.name = parsedUserInfo.username
    studentInfo.studentId = parsedUserInfo.userId
    studentInfo.avatar = parsedUserInfo.avatar || ''

    try {
      const joinResponse = await joinSpeech(speechId, studentInfo.studentId)
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

// 获取测试列表
const fetchTests = async () => {
  if (!speechId) {
    ElMessage.error('演讲ID不能为空')
    return
  }

  loading.value = true
  try {
    const response = await getTestList(speechId)
    tests.value = response.data
  } catch (error) {
    console.error('获取测试列表失败：', error)
    ElMessage.error('获取测试列表失败')
  } finally {
    loading.value = false
  }
}

// 获取个人数据
const fetchMyData = async () => {
  try {
    const response = await getMyData(studentInfo.studentId, speechId)
    Object.assign(myData, response.data)
  } catch (error) {
    console.error('获取个人数据失败：', error)
  }
}

// 开始测试
const startTest = (test: test) => {
  router.push({
    path: '/student/quiz',
    query: {
      code: speechId,
      testId: test.testId,
    },
  })
}

// 设置定时刷新函数保留但不使用

// 清除定时刷新
const clearAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
}

// 提交吐槽
const handleSpitSubmit = async () => {
  if (!spitContent.value.trim()) {
    ElMessage.warning('请输入反馈内容')
    return
  }

  spitSubmitting.value = true
  try {
    await spikeSpeech({
      SpitId: crypto.randomUUID(), // 添加SpitId字段
      speechId,
      content: spitContent.value,
      time: new Date(), // 添加time字段
    })
    ElMessage.success('反馈提交成功')
    spitContent.value = ''
  } catch (error) {
    console.error('提交反馈失败：', error)
    ElMessage.error('提交反馈失败')
  } finally {
    spitSubmitting.value = false
  }
}

onMounted(async () => {
  if (!speechId) {
    ElMessage.error('演讲ID不能为空')
    router.push('/student/dashboard')
    return
  }

  await initUserInfo()
  await Promise.all([fetchTests(), fetchMyData()])

  // 移除自动刷新设置
  // setupAutoRefresh()
})

// 组件卸载时清除定时器（保留以防万一）
onUnmounted(() => {
  clearAutoRefresh()
})
</script>

<style scoped>
.test-list-container {
  padding: 20px;
}

.info-header {
  display: flex;
  align-items: center;
  gap: 15px;
}

.info-header h3 {
  margin: 0;
}

.info-content p {
  margin: 10px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.mt-4 {
  margin-top: 16px;
}

.mt-2 {
  margin-top: 8px;
}

.w-100 {
  width: 100%;
}

.loading-container,
.empty-container {
  padding: 40px 0;
  text-align: center;
}
</style>
