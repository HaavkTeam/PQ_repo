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
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
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
          <el-button type="primary" @click="handleLogin" class="login-button">登录</el-button>
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

const router = useRouter()
const loginFormRef = ref()

const loginForm = reactive({
  username: '',
  password: '',
  role: '',
})

const rules = {
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate((valid: boolean) => {
    if (valid) {
      // TODO: 实现登录逻辑
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
    }
  })
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
</style>
