import axios from 'axios'

const api = axios.create({
  baseURL: '/api', // 修改为使用代理路径
  timeout: 5000,
})

export default api
