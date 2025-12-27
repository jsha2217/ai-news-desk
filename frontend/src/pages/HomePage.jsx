import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Clock, TrendingUp, Bookmark, Activity } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { getTodayArticleCount, getTotalArticleCount, getBookmarkCount } from '../services/statisticsService'
import { bookmarkService } from '../services/bookmarkService'
import { summaryService } from '../services/summaryService'
import { articleService } from '../services/articleService'
import { formatRelativeDate } from '../utils/dateFormatter'
import LoadingSpinner from '../components/LoadingSpinner'
import ArticleCard from '../components/ArticleCard'

const HomePage = () => {
  const [articles, setArticles] = useState([])
  const [loading, setLoading] = useState(true)
  const [timeRemaining, setTimeRemaining] = useState({ hours: 0, minutes: 0, seconds: 0 })
  const [bookmarkedArticles, setBookmarkedArticles] = useState({})
  const [stats, setStats] = useState({
    today: 0,
    bookmarks: 0,
    total: 0
  })
  const [latestSummary, setLatestSummary] = useState(null)
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()

  // 다음 요약 시간(09:00~23:00 정각)까지 남은 시간 계산
  const calculateTimeRemaining = () => {
    const now = new Date()
    const currentHour = now.getHours()
    const currentMinute = now.getMinutes()
    const currentSecond = now.getSeconds()

    let targetHour
    let targetDate = new Date(now)

    if (currentHour < 9) {
      // 09시 이전이면 오늘 09시가 목표
      targetHour = 9
    } else if (currentHour < 23) {
      // 09시~22시 사이면 다음 시간 정각이 목표
      targetHour = currentHour + 1
    } else {
      // 23시 이후면 내일 09시가 목표
      targetDate.setDate(targetDate.getDate() + 1)
      targetHour = 9
    }

    const target = new Date(targetDate)
    target.setHours(targetHour, 0, 0, 0)

    const diffSeconds = Math.floor((target - now) / 1000)

    const hours = Math.floor(diffSeconds / 3600)
    const minutes = Math.floor((diffSeconds % 3600) / 60)
    const seconds = diffSeconds % 60

    return { hours, minutes, seconds }
  }

  // 타이머 카운트다운
  useEffect(() => {
    // 초기 시간 설정
    setTimeRemaining(calculateTimeRemaining())

    // 매초 업데이트
    const timer = setInterval(() => {
      setTimeRemaining(calculateTimeRemaining())
    }, 1000)

    return () => clearInterval(timer)
  }, [])

  useEffect(() => {
    fetchArticles()
    fetchStatistics()
    fetchLatestSummary()
  }, [isAuthenticated])

  const fetchArticles = async () => {
    try {
      setLoading(true)
      const response = await articleService.getAll(0, 10)
      setArticles(response.content)

      // Check bookmark status using batch API if authenticated
      if (isAuthenticated && response.content.length > 0) {
        try {
          const articleIds = response.content.map(article => article.id)
          const bookmarkStatuses = await bookmarkService.checkBookmarksBatch('ARTICLE', articleIds)
          setBookmarkedArticles(bookmarkStatuses)
        } catch (error) {
          console.error('Failed to fetch bookmark statuses:', error)
          setBookmarkedArticles({})
        }
      }
    } catch (error) {
      console.error('Failed to fetch articles:', error)
      toast.error('기사를 불러오는데 실패했습니다')
    } finally {
      setLoading(false)
    }
  }

  const fetchStatistics = async () => {
    try {
      const [todayCount, totalCount] = await Promise.all([
        getTodayArticleCount(),
        getTotalArticleCount()
      ])

      let bookmarkCount = 0
      if (isAuthenticated) {
        try {
          const bookmarkData = await getBookmarkCount()
          // bookmarkData가 숫자인 경우와 객체인 경우를 모두 처리
          bookmarkCount = typeof bookmarkData === 'number' ? bookmarkData : (bookmarkData?.count || bookmarkData?.bookmarks || 0)
        } catch (error) {
          console.error('Failed to fetch bookmark count:', error)
          bookmarkCount = 0
        }
      }

      setStats({
        today: todayCount,
        bookmarks: bookmarkCount,
        total: totalCount
      })
    } catch (error) {
      console.error('Failed to fetch statistics:', error)
      toast.error('통계 정보를 불러오는데 실패했습니다')
    }
  }

  const fetchLatestSummary = async () => {
    try {
      const response = await summaryService.getLatestSummary()
      if (response.content && response.content.length > 0) {
        setLatestSummary(response.content[0])
      }
    } catch (error) {
      console.error('Failed to fetch latest summary:', error)
      // 더미 데이터 사용
      setLatestSummary({
        id: 1,
        title: 'GPT-5와 차세대 AI 모델의 시대가 열린다',
        content: 'OpenAI는 차세대 언어 모델 GPT-5의 개발 로드맵을 공개하며 2026년 초 출시를 목표로 하고 있다고 밝혔습니다. GPT-5는 현재의 GPT-4 대비 추론 능력이 대폭 향상되고, 멀티모달 처리 성능이 개선될 것으로 예상됩니다. DeepMind의 AlphaFold 3가 단백질 구조 예측 정확도 95%를 달성하며 생명과학 분야에 새로운 이정표를 세웠습니다.',
        generatedAt: '2025-12-25T10:00:00'
      })
    }
  }

  const handleBookmarkClick = async (articleId) => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    try {
      const isCurrentlyBookmarked = bookmarkedArticles[articleId]

      if (isCurrentlyBookmarked) {
        await bookmarkService.removeBookmark('ARTICLE', articleId, null)
        setBookmarkedArticles(prev => ({ ...prev, [articleId]: false }))
        setStats(prev => ({ ...prev, bookmarks: prev.bookmarks - 1 }))
        toast.success('북마크가 제거되었습니다')
      } else {
        await bookmarkService.addBookmark('ARTICLE', articleId, null)
        setBookmarkedArticles(prev => ({ ...prev, [articleId]: true }))
        setStats(prev => ({ ...prev, bookmarks: prev.bookmarks + 1 }))
        toast.success('북마크에 추가되었습니다')
      }
    } catch (error) {
      console.error('Failed to toggle bookmark:', error)
      toast.error('북마크 처리 중 오류가 발생했습니다')
    }
  }

  const handleArticleClick = (articleId) => {
    navigate(`/articles/${articleId}`)
  }

  const getSummaryStatus = () => {
    if (!latestSummary || !latestSummary.generatedAt) {
      return '대기중'
    }

    const now = new Date()
    const currentHour = now.getHours()
    const generatedDate = new Date(latestSummary.generatedAt)
    const generatedHour = generatedDate.getHours()

    // 직전 시간에 생성된 요약이 있으면 완료
    if (generatedHour === currentHour) {
      return '완료'
    }

    return '대기중'
  }

  if (loading) {
    return <LoadingSpinner />
  }

  if (articles.length === 0) {
    return (
      <div className="h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-xl text-gray-400">아직 기사가 없습니다</p>
          <Link to="/articles" className="mt-4 inline-block text-cyan-400 hover:text-cyan-300">
            기사 페이지로 이동 →
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div
      className="h-screen w-full overflow-y-auto pb-16 lg:pb-0"
      style={{ height: 'calc(100vh - 4rem)' }}
    >
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-10 py-8 space-y-8">
        {/* Timer Component - Centered */}
        <div className="flex justify-center">
          <div className="card bg-gradient-to-br from-cyan-400/10 to-purple-400/10 border-2 border-cyan-400/30 p-6 sm:p-8 text-center">
            <h2 className="text-lg sm:text-xl text-gray-300 mb-4">다음 AI 요약까지</h2>
            <div className="flex items-center justify-center gap-3 sm:gap-6">
              <div className="text-3xl sm:text-5xl font-bold text-cyan-400 font-mono">
                {String(timeRemaining.hours).padStart(2, '0')}
              </div>
              <div className="text-3xl sm:text-5xl font-bold text-cyan-400">:</div>
              <div className="text-3xl sm:text-5xl font-bold text-cyan-400 font-mono">
                {String(timeRemaining.minutes).padStart(2, '0')}
              </div>
              <div className="text-3xl sm:text-5xl font-bold text-cyan-400">:</div>
              <div className="text-3xl sm:text-5xl font-bold text-cyan-400 font-mono">
                {String(timeRemaining.seconds).padStart(2, '0')}
              </div>
            </div>
          </div>
        </div>

        {/* AI Summary Section */}
        {latestSummary && (
          <div
            onClick={() => navigate(`/summaries/${latestSummary.id}`)}
            className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6 sm:p-8 cursor-pointer hover:border-purple-400/40 transition-colors"
          >
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center">
                <Activity className="w-6 h-6 text-gray-900" />
              </div>
              <h2 className="text-xl sm:text-2xl font-bold text-gray-100 hover:text-purple-400 transition-colors">{latestSummary.title}</h2>
            </div>
            <p className="text-base sm:text-lg text-gray-300 leading-relaxed mb-4 line-clamp-3">
              {latestSummary.content}
            </p>
            <div className="flex items-center gap-2 text-sm text-gray-400">
              <Clock className="w-4 h-4" />
              <span>업데이트: {formatRelativeDate(latestSummary.generatedAt)}</span>
            </div>
          </div>
        )}

        {/* Feature Cards Section */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <div className="card bg-gradient-to-br from-cyan-400/10 to-cyan-600/10 border border-cyan-400/30 p-4 sm:p-6 text-center hover:border-cyan-400/50 transition-colors">
            <TrendingUp className="w-8 h-8 sm:w-10 sm:h-10 text-cyan-400 mx-auto mb-3" />
            <div className="text-2xl sm:text-3xl font-bold text-cyan-400 mb-1">{stats.today}</div>
            <div className="text-xs sm:text-sm text-gray-400">오늘 수집</div>
          </div>

          <div className="card bg-gradient-to-br from-purple-400/10 to-purple-600/10 border border-purple-400/30 p-4 sm:p-6 text-center hover:border-purple-400/50 transition-colors">
            <Activity className="w-8 h-8 sm:w-10 sm:h-10 text-purple-400 mx-auto mb-3" />
            <div className={`text-2xl sm:text-3xl font-bold mb-1 ${getSummaryStatus() === '완료' ? 'text-green-400' : 'text-purple-400'}`}>
              {getSummaryStatus()}
            </div>
            <div className="text-xs sm:text-sm text-gray-400">AI 요약</div>
          </div>

          <div className="card bg-gradient-to-br from-pink-400/10 to-pink-600/10 border border-pink-400/30 p-4 sm:p-6 text-center hover:border-pink-400/50 transition-colors">
            <Bookmark className="w-8 h-8 sm:w-10 sm:h-10 text-pink-400 mx-auto mb-3" />
            <div className="text-2xl sm:text-3xl font-bold text-pink-400 mb-1">
              {isAuthenticated ? stats.bookmarks : '로그인 필요'}
            </div>
            <div className="text-xs sm:text-sm text-gray-400">내 저장</div>
          </div>

          <div className="card bg-gradient-to-br from-green-400/10 to-green-600/10 border border-green-400/30 p-4 sm:p-6 text-center hover:border-green-400/50 transition-colors">
            <TrendingUp className="w-8 h-8 sm:w-10 sm:h-10 text-green-400 mx-auto mb-3" />
            <div className="text-2xl sm:text-3xl font-bold text-green-400 mb-1">{stats.total}</div>
            <div className="text-xs sm:text-sm text-gray-400">누적 기사</div>
          </div>
        </div>

        {/* Article Cards - Scrollable with reversed order (newest first) */}
        <div className="space-y-6">
          <h2 className="text-2xl font-bold text-gray-100 flex items-center gap-3">
            <TrendingUp className="w-6 h-6 text-cyan-400" />
            최신 기사
          </h2>

          {articles.map((article) => (
            <ArticleCard
              key={article.id}
              article={article}
              isBookmarked={bookmarkedArticles[article.id]}
              onBookmarkClick={handleBookmarkClick}
              onArticleClick={handleArticleClick}
            />
          ))}
        </div>
      </div>
    </div>
  )
}

export default HomePage
