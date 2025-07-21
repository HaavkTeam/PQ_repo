<template>
  <div class="dashboard-container">
    <el-card class="dashboard-card">
      <template #header>
        <div class="dashboard-header">
          <h2>组织者控制面板</h2>
          <el-button type="primary" @click="showCreateDialog">创建新演讲</el-button>
        </div>
      </template>

      <div class="lecture-list" v-if="lectures.length > 0">
        <el-table :data="lectures" style="width: 100%">
          <el-table-column prop="title" label="演讲标题" />
          <el-table-column prop="speaker" label="演讲者" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column prop="code" label="验证码" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button
                v-if="row.status === 'active'"
                type="primary"
                size="small"
                @click="enterLecture(row)"
              >
                进入演讲
              </el-button>
              <el-button
                type="danger"
                size="small"
                @click="handleEndLecture(row)"
                :disabled="row.status !== 'active'"
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
          <el-button type="primary" @click="handleCreateLecture">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 定义演讲类型接口
interface Lecture {
  title: string
  speaker: string
  description: string
  status: 'active' | 'ended'
  createTime: string
  code: string
}

// 演讲列表数据
const lectures = ref<Lecture[]>([
  {
    title: 'Vue.js 高级开发技巧',
    speaker: '张三',
    description: 'Vue.js 高级特性和最佳实践分享',
    status: 'active',
    createTime: '2024-01-20 14:30',
    code: 'ABC123',
  },
  {
    title: 'React 状态管理',
    speaker: '李四',
    description: 'React 状态管理方案对比',
    status: 'ended',
    createTime: '2024-01-19 10:00',
    code: 'XYZ789',
  },
])

// 创建演讲表单
const createDialogVisible = ref(false)
const lectureFormRef = ref()
const lectureForm = reactive<Omit<Lecture, 'status' | 'createTime' | 'code'>>({
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

// 显示创建对话框
const showCreateDialog = () => {
  createDialogVisible.value = true
  lectureForm.title = ''
  lectureForm.speaker = ''
  lectureForm.description = ''
}

// 生成随机验证码
const generateCode = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  let code = ''
  for (let i = 0; i < 6; i++) {
    code += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return code
}

// 创建演讲
const handleCreateLecture = async () => {
  if (!lectureFormRef.value) return

  await lectureFormRef.value.validate((valid: boolean) => {
    if (valid) {
      // TODO: 调用后端 API 创建演讲
      const newLecture: Lecture = {
        ...lectureForm,
        status: 'active',
        createTime: new Date().toLocaleString(),
        code: generateCode(),
      }
      lectures.value.push(newLecture)
      ElMessage.success('演讲创建成功')
      createDialogVisible.value = false
    }
  })
}

// 结束演讲
const handleEndLecture = (lecture: Lecture) => {
  ElMessageBox.confirm('确定要结束这场演讲吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // TODO: 调用后端 API 结束演讲
      lecture.status = 'ended'
      ElMessage.success('演讲已结束')
    })
    .catch(() => {
      // 取消操作
    })
}

// 进入演讲
const enterLecture = (lecture: Lecture) => {
  router.push(`/organizer/lecture?code=${lecture.code}`)
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-card {
  margin-bottom: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
