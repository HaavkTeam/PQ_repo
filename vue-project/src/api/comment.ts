import api from './config'

export interface Comment {
  commentId?: string
  questionId: string
  publisher: string
  replyId?: string
  time?: Date
  content: string
}

export interface ReturnComment {
  commentId: string
  questionId: string
  publisher: string
  publisherName: string
  replyId?: string
  replyName?: string
  time: Date
  content: string
}

// 获取题目的所有评论
export const getCommentsByQuestionId = (questionId: string) => {
  return api.get<ReturnComment[]>('/comment/getCommentsByQuestionId', { params: { questionId } })
}

// 添加评论
export const addComment = (comment: Comment) => {
  return api.post<boolean>('/comment/addComment', comment)
}

// 删除评论
export const deleteComment = (commentId: string) => {
  return api.get<boolean>('/comment/deleteComment', { params: { commentId } })
}
