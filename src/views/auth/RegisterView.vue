<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <h2>注册</h2>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="用户名"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
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
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="确认密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="registerForm.role" placeholder="选择角色" class="w-100">
            <el-option label="听众" value="student" />
            <el-option label="演讲者" value="teacher" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="register-button">注册</el-button>
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
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormItemRule } from 'element-plus'

const router = useRouter()
const registerFormRef = ref()

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  role: ''
})

const validatePass2 = (rule: FormItemRule, value: string, callback: (error?: Error) => void) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validatePass2, trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate((valid: boolean) => {
    if (valid) {
      // TODO: 实现注册逻辑
      ElMessage.success('注册成功')
      router.push('/auth/login')
    }
  })
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

.w-100 {
  width: 100%;
}
</style>