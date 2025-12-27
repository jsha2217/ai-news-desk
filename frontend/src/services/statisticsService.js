import api from './api'

export const getTodayArticleCount = async () => {
  const response = await api.get('/articles/count/today')
  return response.data
}

export const getTotalArticleCount = async () => {
  const response = await api.get('/articles/count/total')
  return response.data
}

export const getBookmarkCount = async () => {
  const response = await api.get('/bookmarks/count')
  return response.data
}
