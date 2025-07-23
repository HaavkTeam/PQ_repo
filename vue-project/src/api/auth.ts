import api from '@/api/config'

export interface User {
  userId?: string
  email: string
  password: string
  username: string
  userPhone: string
  role: number
}

export interface LoginData {
  email: string
  password: string
  role?: number
}
export interface ApiError extends Error {
  response?: {
    data?: string
    status?: number
  }
}

export interface ReturnLogin {
  user: User
  isOK: number
  message: string
}

// ... existing code ...

export const login = (data: LoginData) => {
  // 使用 params 发送查询参数，匹配后端的 @RequestParam
  return api.get<ReturnLogin>('/login', {
    params: {
      email: data.email,
      password: data.password,
      role: data.role, // 添加 role 参数
    },
  })
}

// ... existing code ...

export const register = (data: User) => {
  return api.post('/register', data)
}
