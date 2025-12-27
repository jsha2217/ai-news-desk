import api from './api'

export const bookmarkService = {
  // 북마크 목록 조회
  getBookmarks: async (page = 0, size = 20) => {
    const response = await api.get('/bookmarks', {
      params: { page, size }
    })
    return response.data
  },

  // 북마크 추가 (기사 또는 AI 요약)
  addBookmark: async (bookmarkType, articleId, aiSummaryId) => {
    const response = await api.post('/bookmarks', {
      bookmarkType,
      articleId,
      aiSummaryId
    })
    return response.data
  },

  // 북마크 삭제 (기사 또는 AI 요약)
  removeBookmark: async (bookmarkType, articleId, aiSummaryId) => {
    const params = { bookmarkType }
    if (articleId) params.articleId = articleId
    if (aiSummaryId) params.aiSummaryId = aiSummaryId

    const response = await api.delete('/bookmarks', { params })
    return response.data
  },

  // 북마크 여부 확인 (기사 또는 AI 요약)
  checkBookmark: async (bookmarkType, articleId, aiSummaryId) => {
    const params = { bookmarkType }
    if (articleId) params.articleId = articleId
    if (aiSummaryId) params.aiSummaryId = aiSummaryId

    const response = await api.get('/bookmarks/check', { params })
    return response.data
  },

  // 배치 북마크 여부 확인
  checkBookmarksBatch: async (bookmarkType, itemIds) => {
    if (!itemIds || itemIds.length === 0) {
      return {}
    }

    const response = await api.get('/bookmarks/check/batch', {
      params: {
        bookmarkType,
        itemIds: itemIds.join(',')
      }
    })
    return response.data
  },
}
