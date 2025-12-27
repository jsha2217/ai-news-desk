import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Clock, TrendingUp, Activity, Sparkles, ChevronLeft, ChevronRight, Bookmark, BookmarkCheck } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { summaryService } from '../services/summaryService'
import { bookmarkService } from '../services/bookmarkService'
import { formatAbsoluteDate } from '../utils/dateFormatter'
import { parseHighlights } from '../utils/textParser'
import LoadingSpinner from '../components/LoadingSpinner'
import EmptyState from '../components/EmptyState'

const SummariesPage = () => {
  const [summaries, setSummaries] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [bookmarkedSummaries, setBookmarkedSummaries] = useState({})
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const summariesPerPage = 10

  useEffect(() => {
    loadSummaries(currentPage)
  }, [currentPage, isAuthenticated])

  const loadSummaries = async (page) => {
    try {
      setLoading(true)
      const response = await summaryService.getAll(page, summariesPerPage)
      setSummaries(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)

      // Check bookmark status using batch API if authenticated
      if (isAuthenticated && response.content.length > 0) {
        try {
          const summaryIds = response.content.map(summary => summary.id)
          const bookmarkStatuses = await bookmarkService.checkBookmarksBatch('AI_SUMMARY', summaryIds)
          setBookmarkedSummaries(bookmarkStatuses)
        } catch (error) {
          console.error('Failed to fetch bookmark statuses:', error)
          setBookmarkedSummaries({})
        }
      }
    } catch (error) {
      console.error('Failed to fetch AI summaries:', error)
      toast.error('AI 요약을 불러오는데 실패했습니다')
    } finally {
      setLoading(false)
    }
  }

  const handleBookmarkClick = async (summaryId) => {
    if (!isAuthenticated) {
      navigate('/login')
      return
    }

    try {
      const isCurrentlyBookmarked = bookmarkedSummaries[summaryId]

      if (isCurrentlyBookmarked) {
        await bookmarkService.removeBookmark('AI_SUMMARY', null, summaryId)
        setBookmarkedSummaries(prev => ({ ...prev, [summaryId]: false }))
        toast.success('북마크가 제거되었습니다')
      } else {
        await bookmarkService.addBookmark('AI_SUMMARY', null, summaryId)
        setBookmarkedSummaries(prev => ({ ...prev, [summaryId]: true }))
        toast.success('북마크에 추가되었습니다')
      }
    } catch (error) {
      console.error('Failed to toggle bookmark:', error)
      toast.error('북마크 처리 중 오류가 발생했습니다')
    }
  }

  const handlePageChange = (page) => {
    setCurrentPage(page)
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }

  const getPageNumbers = () => {
    const pages = []
    const maxVisible = 5
    let startPage = Math.max(0, currentPage - 2)
    let endPage = Math.min(totalPages - 1, startPage + maxVisible - 1)

    if (endPage - startPage < maxVisible - 1) {
      startPage = Math.max(0, endPage - maxVisible + 1)
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i)
    }

    return pages
  }

  if (loading) {
    return <LoadingSpinner />
  }

  return (
    <div className="w-full px-4 sm:px-6 lg:px-10 py-8 pb-24 lg:pb-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-2">
            <Sparkles className="w-8 h-8 text-purple-400" />
            <h1 className="text-3xl sm:text-4xl font-bold text-gray-100">AI 요약</h1>
          </div>
          <p className="text-gray-400">총 {totalElements}개의 AI 요약이 생성되었습니다</p>
        </div>

        {/* Summaries List */}
        {summaries.length > 0 ? (
          <>
            <div className="space-y-6">
              {summaries.map((summary) => (
                <div
                  key={summary.id}
                  onClick={() => navigate(`/summaries/${summary.id}`)}
                  className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6 sm:p-8 space-y-4 hover:border-purple-400/40 transition-colors cursor-pointer"
                >
                  {/* Header */}
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                      <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center flex-shrink-0">
                        <Activity className="w-6 h-6 text-gray-900" />
                      </div>
                      <h2 className="text-xl sm:text-2xl font-bold text-gray-100 hover:text-purple-400 transition-colors truncate">{summary.title}</h2>
                    </div>
                    <div className="flex items-center gap-3 flex-shrink-0">
                      <div className="flex items-center gap-2 text-sm text-gray-400">
                        <Clock className="w-4 h-4" />
                        <span className="hidden sm:inline">{formatAbsoluteDate(summary.createdAt)}</span>
                      </div>
                      {isAuthenticated && (
                        <button
                          onClick={(e) => {
                            e.stopPropagation()
                            handleBookmarkClick(summary.id)
                          }}
                          className="flex items-center gap-2 text-purple-400 hover:text-purple-300 transition-colors"
                          title={bookmarkedSummaries[summary.id] ? '북마크 제거' : '북마크 추가'}
                        >
                          {bookmarkedSummaries[summary.id] ? (
                            <BookmarkCheck className="w-5 h-5 fill-current" />
                          ) : (
                            <Bookmark className="w-5 h-5" />
                          )}
                        </button>
                      )}
                    </div>
                  </div>

                  {/* Key Highlights */}
                  {summary.keyHighlights && (
                    <div className="bg-purple-400/10 border border-purple-400/30 rounded-lg p-4">
                      <div className="flex items-center gap-2 mb-3">
                        <TrendingUp className="w-5 h-5 text-purple-400" />
                        <h3 className="text-lg font-semibold text-purple-300">주요 하이라이트</h3>
                      </div>
                      <div className="space-y-2">
                        {parseHighlights(summary.keyHighlights).map((highlight, index) => (
                          <p key={index} className="text-gray-300 leading-relaxed">
                            {highlight}
                          </p>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Footer */}
                  <div className="flex items-center gap-4 pt-4 border-t border-purple-400/20">
                    <div className="flex items-center gap-2 text-sm text-gray-400">
                      <TrendingUp className="w-4 h-4" />
                      <span>관련 기사 {summary.relatedArticlesCount}개</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <>
                <div className="mt-8 flex items-center justify-center gap-2 flex-wrap">
                  {/* Previous Button */}
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 0}
                    className="p-2 rounded-lg bg-[#1a1f3a] border border-purple-400/30 text-gray-300 hover:text-purple-400 hover:border-purple-400 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
                  >
                    <ChevronLeft className="w-5 h-5" />
                  </button>

                  {/* First Page */}
                  {getPageNumbers()[0] > 0 && (
                    <>
                      <button
                        onClick={() => handlePageChange(0)}
                        className="px-4 py-2 rounded-lg bg-[#1a1f3a] border border-purple-400/30 text-gray-300 hover:text-purple-400 hover:border-purple-400 transition-all"
                      >
                        1
                      </button>
                      {getPageNumbers()[0] > 1 && (
                        <span className="text-gray-400">...</span>
                      )}
                    </>
                  )}

                  {/* Page Numbers */}
                  {getPageNumbers().map((page) => (
                    <button
                      key={page}
                      onClick={() => handlePageChange(page)}
                      className={`px-4 py-2 rounded-lg border transition-all ${
                        page === currentPage
                          ? 'bg-purple-400 border-purple-400 text-gray-900 font-bold'
                          : 'bg-[#1a1f3a] border-purple-400/30 text-gray-300 hover:text-purple-400 hover:border-purple-400'
                      }`}
                    >
                      {page + 1}
                    </button>
                  ))}

                  {/* Last Page */}
                  {getPageNumbers()[getPageNumbers().length - 1] < totalPages - 1 && (
                    <>
                      {getPageNumbers()[getPageNumbers().length - 1] < totalPages - 2 && (
                        <span className="text-gray-400">...</span>
                      )}
                      <button
                        onClick={() => handlePageChange(totalPages - 1)}
                        className="px-4 py-2 rounded-lg bg-[#1a1f3a] border border-purple-400/30 text-gray-300 hover:text-purple-400 hover:border-purple-400 transition-all"
                      >
                        {totalPages}
                      </button>
                    </>
                  )}

                  {/* Next Button */}
                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages - 1}
                    className="p-2 rounded-lg bg-[#1a1f3a] border border-purple-400/30 text-gray-300 hover:text-purple-400 hover:border-purple-400 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
                  >
                    <ChevronRight className="w-5 h-5" />
                  </button>
                </div>

                {/* Page Info */}
                <div className="mt-4 text-center text-sm text-gray-400">
                  {currentPage + 1} / {totalPages} 페이지
                </div>
              </>
            )}
          </>
        ) : (
          <EmptyState
            Icon={Activity}
            title="아직 AI 요약이 없습니다"
            description="5분마다 새로운 요약이 생성됩니다"
          />
        )}
      </div>
    </div>
  )
}

export default SummariesPage
