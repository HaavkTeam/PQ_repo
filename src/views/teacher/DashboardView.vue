<template>
  <div class="dashboard-container">
    <el-card class="dashboard-card">
      <template #header>
        <div class="dashboard-header">
          <h2>教师控制面板</h2>
          <el-button type="primary" @click="showJoinDialog">加入演讲</el-button>
        </div>
      </template>

      <div class="lecture-list" v-if="joinedLectures.length > 0">
        <el-table :data="joinedLectures" style="width: 100%">
          <el-table-column prop="title" label="演讲标题" />
          <el-table-column prop="speaker" label="演讲者" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                {{ row.status === 'active' ? '进行中' : '已结束' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                @click="handleEnterLecture(row)"
                :disabled="row.status !== 'active'"
              >
                进入演讲
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="暂未加入任何演讲" />
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
          <el-button type="primary" @click="handleJoinLecture">加入</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 定义演讲类型接口
interface Lecture {
  title: string
  speaker: string
  description: string
  status: 'active' | 'ended'
  code?: string
}

// 已加入的演讲列表
const joinedLectures = ref<Lecture[]>([])

// 加入演讲表单
const joinDialogVisible = ref(false)
const joinFormRef = ref()
const joinForm = reactive({
  code: '',
})

// 表单验证规则
const rules = {
  code: [{ required: true, message: '请输入演讲验证码', trigger: 'blur' }],
}

// 显示加入对话框
const showJoinDialog = () => {
  joinDialogVisible.value = true
  joinForm.code = ''
}

// 加入演讲
const handleJoinLecture = async () => {
  if (!joinFormRef.value) return

  await joinFormRef.value.validate((valid: boolean) => {
    if (valid) {
      // TODO: 调用后端 API 验证并加入演讲
      // 这里模拟加入成功
      const mockLecture: Lecture = {
        title: '示例演讲',
        speaker: '张三',
        description: '这是一个示例演讲',
        status: 'active',
        code: joinForm.code,
      }
      joinedLectures.value.push(mockLecture)
      ElMessage.success('成功加入演讲')
      joinDialogVisible.value = false
    }
  })
}

// 进入演讲
const handleEnterLecture = (lecture: Lecture) => {
  ElMessageBox.confirm('确定要进入该演讲吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(() => {
      // TODO: 实现进入演讲的逻辑
      ElMessage.success('正在进入演讲：' + lecture.title)
    })
    .catch(() => {
      // 取消操作
    })
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
