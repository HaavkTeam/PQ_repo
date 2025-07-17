<template>
  <div class="lecture-container">
    <el-row :gutter="20">
      <!-- 左侧文件管理区域 -->
      <el-col :span="16">
        <el-card class="file-card">
          <template #header>
            <div class="card-header">
              <h3>文件管理</h3>
              <div class="file-tools">
                <el-upload
                  class="upload-demo"
                  action="#"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  accept=".pdf,.ppt,.pptx"
                >
                  <el-button type="primary">选择文件</el-button>
                </el-upload>
                <el-button-group>
                  <el-button :disabled="!currentFile">上一页</el-button>
                  <el-button :disabled="!currentFile">下一页</el-button>
                  <el-button type="danger" :disabled="!currentFile">删除</el-button>
                </el-button-group>
              </div>
            </div>
          </template>

          <!-- 文件预览区域 -->
          <div class="file-preview" v-if="currentFile">
            <div class="preview-placeholder">
              文件预览区域
              <!-- 这里后续集成具体的文件预览组件 -->
            </div>
          </div>
          <el-empty v-else description="请上传演讲文件" />
        </el-card>
      </el-col>

      <!-- 右侧学生反馈区域 -->
      <el-col :span="8">
        <el-card class="feedback-card">
          <template #header>
            <div class="card-header">
              <h3>学生反馈</h3>
              <el-select v-model="currentQuestion" placeholder="选择题目">
                <el-option
                  v-for="item in questions"
                  :key="item.id"
                  :label="item.title"
                  :value="item.id"
                />
              </el-select>
            </div>
          </template>

          <!-- 答题情况概览 -->
          <div class="statistics-section">
            <el-row :gutter="20">
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ statistics.totalStudents }}</div>
                  <div class="stat-label">参与学生</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ statistics.answeredCount }}</div>
                  <div class="stat-label">已答题数</div>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="stat-item">
                  <div class="stat-value">{{ statistics.correctRate }}%</div>
                  <div class="stat-label">正确率</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 反馈列表 -->
          <div class="feedback-list">
            <el-timeline>
              <el-timeline-item
                v-for="feedback in feedbacks"
                :key="feedback.id"
                :timestamp="feedback.time"
                :type="feedback.type"
              >
                <div class="feedback-content">
                  <span class="student-name">{{ feedback.studentName }}</span>
                  <p class="feedback-text">{{ feedback.content }}</p>
                  <div class="feedback-tags">
                    <el-tag
                      v-for="tag in feedback.tags"
                      :key="tag"
                      size="small"
                      :type="getTagType(tag)"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

// 文件管理
const currentFile = ref<UploadFile | null>(null)

interface UploadFile {
  name: string
  size: number
  type: string
  raw: File
}

const handleFileChange = (file: UploadFile) => {
  currentFile.value = file
  // TODO: 处理文件上传
}

// 问题选择
const currentQuestion = ref('')
const questions = [
  { id: '1', title: '第1题：Vue.js 的生命周期' },
  { id: '2', title: '第2题：组件通信方式' },
  { id: '3', title: '第3题：响应式原理' },
]

// 统计数据
const statistics = reactive({
  totalStudents: 30,
  answeredCount: 25,
  correctRate: 85,
})

// 反馈列表
const feedbacks = [
  {
    id: 1,
    studentName: '张三',
    content: '这个概念有点难理解',
    time: '14:30',
    type: 'warning',
    tags: ['疑问', '需要解释'],
  },
  {
    id: 2,
    studentName: '李四',
    content: '明白了，谢谢老师',
    time: '14:35',
    type: 'success',
    tags: ['已理解'],
  },
]

// 获取标签类型
const getTagType = (tag: string) => {
  const tagTypes: { [key: string]: string } = {
    疑问: 'warning',
    需要解释: 'danger',
    已理解: 'success',
  }
  return tagTypes[tag] || 'info'
}
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

.file-card,
.feedback-card {
  margin-bottom: 20px;
}

.file-tools {
  display: flex;
  gap: 10px;
  align-items: center;
}

.file-preview {
  min-height: 400px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-placeholder {
  color: #909399;
  font-size: 14px;
}

.statistics-section {
  margin-bottom: 20px;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  color: #409eff;
  font-weight: bold;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.feedback-list {
  max-height: 400px;
  overflow-y: auto;
}

.feedback-content {
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
}

.student-name {
  font-weight: bold;
  color: #409eff;
}

.feedback-text {
  margin: 8px 0;
  color: #606266;
}

.feedback-tags {
  display: flex;
  gap: 5px;
}
</style>
