import api from './config'

export interface Question {
  questionId: string
  testId: string
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
  selection: string
  isCorrect?: number
}

export interface ReturnSubmit {
  questionId: string
  speechId?: string
  description?: string
  optionA?: string
  optionB?: string
  optionC?: string
  optionD?: string
  answer: string // 正确答案
  selection: string // 用户选择
  isCorrect: number // 改为number类型
}

export interface test {
  testId: string
  speechId: string
  state: string
}

export interface MyData {
  userId: string
  answerNumber: number // 改为小写开头
  allnumber: number // 改为小写开头
  correctPercentage: string // 改为小写开头
}
export interface UserAnswerData {
  questionId: string
  description: string
  optionA: string
  optionB: string
  optionC: string
  optionD: string
  answer: string
  anumber: number
  bnumber: number
  cnumber: number
  dnumber: number
  answerNumber: number
  correctPercentage: string
}

// 获取题目作答数据
export const getUserData = (testId: string) => {
  return api.get<UserAnswerData[]>('/question/getUserData', { params: { testId } })
}

// 获取测试列表
export const getTestList = (speechId: string) => {
  return api.get<test[]>('/question/getTestList', { params: { speechId } })
}

// 获取测试的所有问题
export const getQuestionsByTestId = (testId: string) => {
  return api.get<Question[]>('/question/getQuestionsByTestId', { params: { testId } })
}

// 提交答案
export const submitAnswer = (submitAnswer: UserSubmit) => {
  return api.post<string>('/question/submitAnswer', submitAnswer)
}

// 获取我的提交记录
export const getMySubmit = (userId: string, speechId: string) => {
  return api.get<ReturnSubmit[]>('/question/getMySubmit', { params: { userId, speechId } })
}

// 获取我的数据
export const getMyData = (userId: string, speechId: string) => {
  return api.get<MyData>('/question/getMyData', { params: { userId, speechId } })
}
// 获取未使用的题目列表
export const getUnusedQuestions = (speechId: string) => {
  return api.get<Question[]>('/question/launchQuestion', { params: { speechId } })
}

// 发布测试
export const launchTest = (speechId: string, questionIds: string[]) => {
  return api.post<string>('/question/publishQuestion', questionIds, {
    params: { speechId },
  })
}

// 改变测试状态（激活/停用测试）
export const changeTestStatus = (testId: string) => {
  return api.get<string>('/question/changeTestStatus', { params: { testId } })
}
