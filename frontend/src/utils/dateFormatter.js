/**
 * 상대 시간 포맷 (예: "5분 전", "2시간 전", "3일 전")
 * HomePage, ArticlesPage, BookmarksPage에서 사용
 */
export const formatRelativeDate = (dateString) => {
  const date = new Date(dateString)
  const now = new Date()
  const diff = Math.floor((now - date) / 1000 / 60) // 분 단위

  if (diff < 60) return `${diff}분 전`
  if (diff < 1440) return `${Math.floor(diff / 60)}시간 전`
  return `${Math.floor(diff / 1440)}일 전`
}

/**
 * 절대 시간 포맷 (예: "2025년 12월 25일 오전 10:00")
 * ArticleDetailPage, SummariesPage, SummaryDetailPage에서 사용
 */
export const formatAbsoluteDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}
