import api from './config'

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

export interface Spit {
  speechId: string
  userId: string
  content: string
  time?: Date
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

// 吐槽演讲
export const spikeSpeech = (spit: Spit) => {
  return api.post<string>('/speech/SpikeSpeech', spit)
}

// 获取演讲的所有吐槽
export const getSpitsBySpeechId = (speechId: string) => {
  return api.get<Spit[]>('/speech/getSpitsBySpeechId', { params: { speechId } })
}
