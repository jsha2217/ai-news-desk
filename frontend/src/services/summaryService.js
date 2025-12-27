import api from './api'

export const summaryService = {
  // 모든 AI 요약 조회
  getAll: async (page = 0, size = 20, sort = 'createdAt,desc') => {
    const response = await api.get('/ai-summaries', {
      params: { page, size, sort }
    })
    return response.data
  },

  // 발행된 AI 요약 조회
  getPublishedSummaries: async (page = 0, size = 10) => {
    const response = await api.get('/ai-summaries', {
      params: {
        page,
        size,
        status: 'PUBLISHED',
        sort: 'createdAt,desc'
      }
    })
    return response.data
  },

  // 최신 AI 요약 조회
  getLatestSummary: async () => {
    const response = await api.get('/ai-summaries', {
      params: {
        page: 0,
        size: 1,
        sort: 'createdAt,desc'
      }
    })
    return response.data
  },

  // AI 요약 상세 조회
  getSummaryById: async (id) => {
    const response = await api.get(`/ai-summaries/${id}`)
    return response.data
  },
}
