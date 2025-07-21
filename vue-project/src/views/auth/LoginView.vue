<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>登录</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择身份" class="role-select">
            <el-option label="学生" value="student" />
            <el-option label="老师" value="teacher" />
            <el-option label="组织者" value="organizer" />
          </el-select>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="loginForm.email" placeholder="邮箱">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password>
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-button" :loading="loading">
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
        <div class="register-link">
          <router-link to="/auth/register">没有账号？立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import type { FormInstance } from 'element-plus'
import type { AxiosError } from 'axios'
const router = useRouter()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  email: '',
  password: '',
  role: '',
})

// 角色映射
const roleMap = {
  student: 1,
  teacher: 2,
  organizer: 3,
}

const rules = {
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
  ],
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  try {
    // 表单验证
    const valid = await loginFormRef.value.validate()
    if (!valid) return

    // 设置加载状态
    loading.value = true

    // 发送登录请求
    const response = await login({
      email: loginForm.email,
      password: loginForm.password,
      role: roleMap[loginForm.role as keyof typeof roleMap], // 添加角色参数，使用 roleMap 转换
    })

    if (response.data === '登录成功') {
      ElMessage.success('登录成功')
      // 根据不同角色跳转到不同的页面
      switch (loginForm.role) {
        case 'student':
          router.push('/student/dashboard')
          break
        case 'teacher':
          router.push('/teacher/dashboard')
          break
        case 'organizer':
          router.push('/organizer/dashboard')
          break
      }
    } else {
      ElMessage.error('登录失败：用户名或密码错误')
    }
  } catch (error: unknown) {
    // 处理表单验证错误和网络请求错误
    if (error instanceof Error) {
      if ((error as AxiosError).response) {
        const axiosError = error as AxiosError
        ElMessage.error(`登录失败：${axiosError.response?.data || '服务器错误'}`)
      } else {
        ElMessage.error(`登录失败：${error.message}`)
      }
    } else {
      ElMessage.error('登录失败：未知错误')
    }
    console.error('登录错误：', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.login-button {
  width: 100%;
}

.register-link {
  text-align: center;
  margin-top: 16px;
}

.role-select {
  width: 100%;
}

.el-card :deep(.el-card__header) {
  text-align: center;
  padding: 16px;
}

.el-card :deep(.el-card__header h2) {
  margin: 0;
  font-size: 24px;
  color: #303133;
}
</style>
