/**
 * 하이라이트 텍스트 파싱 (줄바꿈 기준으로 분리)
 * SummariesPage, SummaryDetailPage에서 사용
 */
export const parseHighlights = (highlights) => {
  if (!highlights) return []
  return highlights.split('\n').filter(line => line.trim())
}

/**
 * YouTube 비디오 ID 추출
 * ArticleDetailPage에서 사용
 */
export const extractYouTubeVideoId = (url) => {
  if (!url) return null
  const match = url.match(/[?&]v=([^&]+)/)
  return match ? match[1] : null
}
