import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { ArrowLeft, Calendar, ExternalLink, Play, Eye, ThumbsUp, MessageSquare, Tag } from 'lucide-react'
import { articleService } from '../services/articleService'
import { formatAbsoluteDate } from '../utils/dateFormatter'
import { extractYouTubeVideoId } from '../utils/textParser'
import LoadingSpinner from '../components/LoadingSpinner'

const ArticleDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [article, setArticle] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchArticle()
  }, [id])

  const fetchArticle = async () => {
    try {
      setLoading(true)
      const response = await articleService.getById(id)
      setArticle(response)
    } catch (error) {
      console.error('Failed to fetch article:', error)
      toast.error('기사를 불러오는데 실패했습니다')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <LoadingSpinner />
  }

  if (!article) {
    return (
      <div className="h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-gray-400">기사를 찾을 수 없습니다</p>
          <button
            onClick={() => navigate('/')}
            className="mt-4 inline-block text-cyan-400 hover:text-cyan-300"
          >
            ← 홈으로 돌아가기
          </button>
        </div>
      </div>
    )
  }

  const videoId = extractYouTubeVideoId(article.url)

  return (
    <div
      className="h-screen w-full overflow-y-auto pb-16 lg:pb-0"
      style={{ height: 'calc(100vh - 4rem)' }}
    >
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-10 py-8 space-y-8">
        {/* Back Button */}
        <button
          onClick={() => navigate('/')}
          className="flex items-center gap-2 text-cyan-400 hover:text-cyan-300 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          <span>뒤로 가기</span>
        </button>

        {/* Article Header */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-6">
          {/* Badges and Meta Info */}
          <div className="flex items-center gap-3 flex-wrap">
            <span className="badge-official">OFFICIAL</span>
            <div className="flex items-center gap-2 text-sm text-gray-400">
              <Calendar className="w-4 h-4" />
              <span>발행일: {formatAbsoluteDate(article.publishedAt || article.crawledAt)}</span>
            </div>
          </div>

          {/* Title */}
          <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-gray-100 leading-tight">
            {article.title}
          </h1>

          {/* Source */}
          <div className="flex items-center gap-2 text-lg">
            <span className="text-gray-400">출처:</span>
            <span className="text-cyan-400 font-semibold">{article.sourceName}</span>
          </div>

          {/* Tags */}
          <div className="flex items-center gap-2 flex-wrap">
            <Tag className="w-4 h-4 text-gray-400" />
            <span className="tag">#YouTube</span>
            <span className="tag">#{article.category}</span>
          </div>
        </div>

        {/* YouTube Video Embed */}
        {videoId && (
          <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-4">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-100 flex items-center gap-3">
              <Play className="w-6 h-6 text-cyan-400" />
              영상 보기
            </h2>
            <div className="relative w-full" style={{ paddingBottom: '56.25%' }}>
              <iframe
                className="absolute top-0 left-0 w-full h-full rounded-lg"
                src={`https://www.youtube.com/embed/${videoId}`}
                title={article.title}
                frameBorder="0"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                allowFullScreen
              ></iframe>
            </div>
          </div>
        )}

        {/* Video URL */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-4">
          <h2 className="text-xl sm:text-2xl font-bold text-gray-100 flex items-center gap-3">
            <ExternalLink className="w-6 h-6 text-cyan-400" />
            영상 링크
          </h2>
          <a
            href={article.url}
            target="_blank"
            rel="noopener noreferrer"
            className="block text-cyan-400 hover:text-cyan-300 break-all transition-colors"
          >
            {article.url}
          </a>
        </div>

        {/* Description */}
        {article.description && (
          <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-4">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-100">영상 설명</h2>
            <p className="text-base sm:text-lg text-gray-300 leading-relaxed whitespace-pre-wrap">
              {article.description}
            </p>
          </div>
        )}

        {/* Thumbnail (if no video embed) */}
        {!videoId && article.thumbnailUrl && (
          <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-4">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-100">썸네일</h2>
            <img
              src={article.thumbnailUrl}
              alt={article.title}
              className="w-full rounded-lg"
            />
          </div>
        )}

        {/* Article Metadata */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8">
          <h2 className="text-xl sm:text-2xl font-bold text-gray-100 mb-4">기사 정보</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm sm:text-base">
            <div className="flex items-start gap-2">
              <span className="text-gray-400 min-w-24">카테고리:</span>
              <span className="text-gray-200">{article.category}</span>
            </div>
            <div className="flex items-start gap-2">
              <span className="text-gray-400 min-w-24">소스 타입:</span>
              <span className="text-gray-200">{article.sourceType}</span>
            </div>
            <div className="flex items-start gap-2">
              <span className="text-gray-400 min-w-24">수집 일시:</span>
              <span className="text-gray-200">{formatAbsoluteDate(article.crawledAt)}</span>
            </div>
            {article.publishedAt && (
              <div className="flex items-start gap-2">
                <span className="text-gray-400 min-w-24">발행 일시:</span>
                <span className="text-gray-200">{formatAbsoluteDate(article.publishedAt)}</span>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default ArticleDetailPage
