import api from './api'

export const articleService = {
  // 전체 기사 조회 (페이징)
  getAll: async (page = 0, size = 20) => {
    const response = await api.get('/articles', {
      params: { page, size }
    })
    return response.data
  },

  // 기사 상세 조회
  getById: async (id) => {
    const response = await api.get(`/articles/${id}`)
    return response.data
  },

  // 출처별 기사 조회
  getBySource: async (sourceType, page = 0, size = 20) => {
    const response = await api.get(`/articles/source/${sourceType}`, {
      params: { page, size }
    })
    return response.data
  },

  // 카테고리별 기사 조회
  getByCategory: async (category, page = 0, size = 20) => {
    const response = await api.get(`/articles/category/${category}`, {
      params: { page, size }
    })
    return response.data
  },

  // 기사 검색
  search: async (query, page = 0, size = 20) => {
    const response = await api.get('/articles/search', {
      params: { query, page, size }
    })
    return response.data
  },
}
