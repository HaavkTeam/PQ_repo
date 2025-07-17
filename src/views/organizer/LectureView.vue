<template>
  <div class="lecture-container">
    <el-row :gutter="20">
      <!-- 左侧区域：文件接收和AI处理 -->
      <el-col :span="16">
        <!-- 文件接收区域 -->
        <el-card class="file-card">
          <template #header>
            <div class="card-header">
              <h3>文件处理</h3>
              <div class="file-status" v-if="currentFile">
                <el-tag>{{ currentFile.name }}</el-tag>
                <el-button type="danger" size="small" @click="handleRemoveFile">移除</el-button>
              </div>
            </div>
          </template>

          <el-upload
            class="file-upload"
            drag
            action="#"
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            accept=".pdf,.ppt,.pptx"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持 PDF、PPT 格式文件</div>
            </template>
          </el-upload>

          <!-- AI处理状态和结果 -->
          <div class="ai-processing" v-if="currentFile">
            <el-steps :active="processingStep" finish-status="success">
              <el-step title="文件上传" />
              <el-step title="AI分析" />
              <el-step title="生成题目" />
            </el-steps>

            <div class="processing-status" v-if="isProcessing">
              <el-progress :percentage="processingProgress" />
              <p>{{ processingStatus }}</p>
            </div>

            <div class="action-buttons" v-if="!isProcessing">
              <el-button type="primary" @click="startProcessing" :loading="isProcessing">
                开始处理
              </el-button>
            </div>
          </div>
        </el-card>

        <!-- 题目管理面板 -->
        <el-card class="questions-card">
          <template #header>
            <div class="card-header">
              <h3>题目管理</h3>
              <div class="question-tools">
                <el-button-group>
                  <el-button :disabled="!selectedQuestion">上移</el-button>
                  <el-button :disabled="!selectedQuestion">下移</el-button>
                </el-button-group>
                <el-button type="primary" @click="saveQuestions">保存修改</el-button>
              </div>
            </div>
          </template>

          <el-table :data="questions" @selection-change="handleSelectionChange" style="width: 100%">
            <el-table-column type="selection" width="55" />
            <el-table-column label="序号" type="index" width="80" />
            <el-table-column prop="content" label="题目内容">
              <template #default="{ row }">
                <el-input v-model="row.content" type="textarea" :rows="2" placeholder="题目内容" />
              </template>
            </el-table-column>
            <el-table-column prop="time" label="答题时间(秒)" width="120">
              <template #default="{ row }">
                <el-input-number v-model="row.time" :min="30" :max="300" :step="30" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="danger" size="small" @click="removeQuestion(row)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧区域：数据统计 -->
      <el-col :span="8">
        <el-card class="statistics-card">
          <template #header>
            <div class="card-header">
              <h3>数据统计</h3>
              <el-button type="primary" size="small" @click="refreshStatistics"> 刷新 </el-button>
            </div>
          </template>

          <!-- 总体统计 -->
          <div class="statistics-overview">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="stat-item">
                  <div class="stat-value">{{ statistics.correctRate }}%</div>
                  <div class="stat-label">平均正确率</div>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="stat-item">
                  <div class="stat-value">{{ statistics.notAnswered }}</div>
                  <div class="stat-label">未答题人数</div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 答题分布 -->
          <div class="answer-distribution">
            <h4>答题分布</h4>
            <div class="distribution-chart">
              <!-- 这里后续集成图表组件 -->
              <div class="chart-placeholder">答题分布图表</div>
            </div>
          </div>

          <!-- 参与度分析 -->
          <div class="participation-analysis">
            <h4>学生参与度</h4>
            <el-progress
              :percentage="statistics.participationRate"
              :format="format"
              :stroke-width="10"
            />
            <div class="participation-details">
              <p>活跃学生：{{ statistics.activeStudents }} 人</p>
              <p>总计学生：{{ statistics.totalStudents }} 人</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { UploadFilled } from '@element-plus/icons-vue'

// 文件处理
interface UploadFile {
  name: string
  size: number
  type: string
  raw: File
}

const currentFile = ref<UploadFile | null>(null)
const processingStep = ref(0)
const isProcessing = ref(false)
const processingProgress = ref(0)
const processingStatus = ref('')

const handleFileChange = (file: UploadFile) => {
  currentFile.value = file
  processingStep.value = 0
  processingProgress.value = 0
}

const handleRemoveFile = () => {
  currentFile.value = null
  processingStep.value = 0
  processingProgress.value = 0
  processingStatus.value = ''
}

const startProcessing = async () => {
  if (!currentFile.value) return

  isProcessing.value = true
  processingStatus.value = '正在上传文件...'

  try {
    // 模拟处理过程
    for (let i = 1; i <= 3; i++) {
      processingStep.value = i
      for (let j = 0; j <= 100; j += 10) {
        processingProgress.value = j
        await new Promise((resolve) => setTimeout(resolve, 100))
      }
    }

    // TODO: 实际的文件处理和AI分析逻辑

    processingStatus.value = '处理完成'
  } catch (error: unknown) {
    processingStatus.value = error instanceof Error ? error.message : '处理失败'
  }
}

// 题目管理
interface Question {
  id: string
  content: string
  time: number
  options: string[]
}

const questions = ref<Question[]>([
  {
    id: '1',
    content: '这是第一个示例题目',
    time: 60,
    options: ['选项A', '选项B', '选项C', '选项D'],
  },
  {
    id: '2',
    content: '这是第二个示例题目',
    time: 90,
    options: ['选项A', '选项B', '选项C'],
  },
])

const selectedQuestion = ref<Question | null>(null)

const handleSelectionChange = (selection: Question[]) => {
  selectedQuestion.value = selection[0] || null
}

const removeQuestion = (question: Question) => {
  const index = questions.value.findIndex((q) => q.id === question.id)
  if (index > -1) {
    questions.value.splice(index, 1)
  }
}

const saveQuestions = () => {
  // TODO: 保存题目修改到后端
  console.log('保存题目修改', questions.value)
}

// 统计数据
const statistics = reactive({
  correctRate: 75,
  notAnswered: 5,
  participationRate: 85,
  activeStudents: 42,
  totalStudents: 50,
})

const format = (percentage: number) => `${percentage}% 参与度`

const refreshStatistics = () => {
  // TODO: 从后端刷新统计数据
  console.log('刷新统计数据')
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
.questions-card,
.statistics-card {
  margin-bottom: 20px;
}

.file-upload {
  width: 100%;
}

.file-status {
  display: flex;
  gap: 10px;
  align-items: center;
}

.ai-processing {
  margin-top: 20px;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.processing-status {
  margin: 20px 0;
  text-align: center;
}

.action-buttons {
  text-align: center;
  margin-top: 20px;
}

.question-tools {
  display: flex;
  gap: 10px;
}

.statistics-overview {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
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

.answer-distribution,
.participation-analysis {
  margin-top: 20px;
}

.chart-placeholder {
  height: 200px;
  background: #f5f7fa;
  border-radius: 4px;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #909399;
}

.participation-details {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
}

.participation-details p {
  margin: 5px 0;
}
</style>
