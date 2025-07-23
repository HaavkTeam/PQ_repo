import api from './config'

export interface Question {
  questionId: string
  speechId: string
  description: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  answer: string
  isUsed: boolean
}

export interface UserSubmit {
  questionId: string
  userId: string
  selection: string // 改为selection而不是answer
  isCorrect?: number // 添加isCorrect字段
}

export interface ReturnSubmit {
  questionId: string
  answer: string
  isCorrect: boolean
}

// 获取演讲的所有问题
export const getQuestionsById = (speechId: string) => {
  return api.get<Question[]>('/getQuestionsById', { params: { speechId } })
}

// 获取未使用的问题
export const launchQuestion = (speechId: string) => {
  return api.get<Question[]>('/launchQuestion', { params: { speechId } })
}

// 提交答案
export const submitAnswer = (submitAnswer: UserSubmit) => {
  return api.post<string>('/submitAnswer', submitAnswer)
}

// 获取我的提交记录
export const getMySubmit = (userId: string, speechId: string) => {
  return api.get<ReturnSubmit[]>('/getMySubmit', { params: { userId, speechId } })
}
