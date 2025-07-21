<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>注册</h2>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="role">
          <el-select v-model="registerForm.role" placeholder="请选择身份" class="role-select">
            <el-option label="学生" value="student" />
            <el-option label="老师" value="teacher" />
            <el-option label="组织者" value="organizer" />
          </el-select>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="registerForm.email" placeholder="邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="userPhone">
          <el-input v-model="registerForm.userPhone" placeholder="手机号码">
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="handleRegister"
            class="register-button"
            :loading="loading"
          >
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>
        <div class="login-link">
          <router-link to="/auth/login">已有账号？立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { User, Lock, Message, Phone } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { register } from '@/api/auth'
import type { ApiError } from '@/api/auth'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = reactive({
  email: '',
  username: '',
  userPhone: '',
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
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, message: '用户名长度不能小于2位', trigger: 'blur' },
  ],
  userPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' },
  ],
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    // 表单验证
    const valid = await registerFormRef.value.validate()
    if (!valid) return

    // 设置加载状态
    loading.value = true

    // 发送注册请求
    const response = await register({
      email: registerForm.email,
      username: registerForm.username,
      userPhone: registerForm.userPhone,
      password: registerForm.password,
      role: roleMap[registerForm.role as keyof typeof roleMap],
    })

    if (response.data === '注册成功') {
      ElMessage.success('注册成功')
      // 注册成功后跳转到登录页
      router.push('/auth/login')
    } else {
      ElMessage.error('注册失败：' + response.data)
    }
  } catch (error) {
    const err = error as ApiError
    ElMessage.error(`注册失败：${err.response?.data || err.message || '未知错误'}`)
    console.error('注册错误：', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.register-card {
  width: 400px;
}

.register-button {
  width: 100%;
}

.login-link {
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
