import api from './config'
import type { UserAnswerData } from './question'
export interface Speech {
  speechId: string
  title: string
  description: string
  organizer: string
  speaker: string
  startTime: Date
  endTime: Date
  status: number
}

export interface ReturnSpeech extends Speech {
  organizerName: string
  speakerName: string
}

// 组织者数据接口
export interface OrganizerData {
  userAnswers: UserAnswerData[]
  audienceCount: number
  averageAccuracy: string
  questionNumber: number
}

// 获取组织者演讲数据
export const getSpeechData = (speechId: string) => {
  return api.get<OrganizerData>('/speech/getSpeechData', { params: { speechId } })
}

export interface Spit {
  SpitId?: string
  speechId: string
  content: string
  time?: Date // 改回 Date 类型
}
// 获取所有演讲
export const getAllSpeeches = () => {
  return api.get<ReturnSpeech[]>('/speech/getAllSpeeches')
}

// 创建演讲
export const addSpeech = (speech: Speech) => {
  return api.post<string>('/speech/addSpeech', speech)
}

// 结束演讲
export const endSpeech = (speechId: string) => {
  return api.get<string>('/speech/endSpeech', { params: { speechId } })
}

// 获取指定演讲
export const getSpeechById = (speechId: string) => {
  return api.get<ReturnSpeech>('/speech/getSpeechById', { params: { speechId } })
}

// 加入演讲
export const joinSpeech = (speechId: string, userId: string) => {
  return api.get<string>('/speech/joinSpeech', { params: { speechId, userId } })
}

// 获取用户参与的演讲
export const getMySpeeches = (userId: string) => {
  return api.get<ReturnSpeech[]>('/speech/getMySpeeches', { params: { userId } })
}
// 获取演讲者主讲的演讲
export const getSpeechesBySpeaker = (userId: string) => {
  return api.get<ReturnSpeech[]>('/speech/getSpeechesBySpeaker', { params: { userId } })
}

// 获取组织者组织的演讲
export const getSpeechesByOrganizer = (userId: string) => {
  return api.get<ReturnSpeech[]>('/speech/getSpeechesByOrganizer', { params: { userId } })
}

// 获取演讲的听众列表
export const getAudienceBySpeechId = (speechId: string) => {
  return api.get<string[]>('/speech/getAudienceBySpeechId', { params: { speechId } })
}

export const spikeSpeech = (spit: Spit) => {
  return api.post<string>(
    '/speech/SpikeSpeech',
    {
      SpitId: spit.SpitId || crypto.randomUUID(), // 保持大写 S
      speechId: spit.speechId, // 改为小写 s
      content: spit.content, // 改为小写 c
      time: spit.time || new Date(), // 改为小写 t
    },
    {
      headers: {
        'Content-Type': 'application/json',
      },
    },
  )
}
// 获取演讲的所有吐槽
export const getSpitsBySpeechId = (speechId: string) => {
  return api.get<Spit[]>('/speech/getSpitsBySpeechId', { params: { speechId } })
}
// 获取演讲的听众数量
export const getSpeechAudienceCount = (speechId: string) => {
  return api.get<number>('/speech/getSpeechAudienceCount', { params: { speechId } })
}
