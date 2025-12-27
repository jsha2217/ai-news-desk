import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { ChevronLeft, ChevronRight, Bookmark as BookmarkIcon, BookmarkCheck, Clock, TrendingUp, Activity } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { bookmarkService } from '../services/bookmarkService'
import { formatAbsoluteDate } from '../utils/dateFormatter'
import { parseHighlights } from '../utils/textParser'
import LoadingSpinner from '../components/LoadingSpinner'
import ArticleCard from '../components/ArticleCard'
import EmptyState from '../components/EmptyState'

const BookmarksPage = () => {
  const [bookmarks, setBookmarks] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()
  const { isAuthenticated, loading: authLoading } = useAuth()
  const bookmarksPerPage = 10

  useEffect(() => {
    if (!authLoading) {
      if (!isAuthenticated) {
        navigate('/login')
      } else {
        loadBookmarks(currentPage)
      }
    }
  }, [currentPage, isAuthenticated, authLoading, navigate])

  const loadBookmarks = async (page) => {
    try {
      setLoading(true)
      const response = await bookmarkService.getBookmarks(page, bookmarksPerPage)
      console.log('Bookmarks response:', response) // 디버깅 로그
      setBookmarks(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)
    } catch (error) {
      console.error('Failed to load bookmarks:', error)
      toast.error('북마크를 불러오는데 실패했습니다')
    } finally {
      setLoading(false)
    }
  }

  const handleRemoveBookmark = async (bookmark) => {
    try {
      await bookmarkService.removeBookmark(
        bookmark.bookmarkType,
        bookmark.articleId,
        bookmark.aiSummaryId
      )
      toast.success('북마크가 제거되었습니다')
      // Reload current page
      loadBookmarks(currentPage)
    } catch (error) {
      console.error('Failed to remove bookmark:', error)
      toast.error('북마크 제거에 실패했습니다')
    }
  }

  const handleClick = (bookmark) => {
    if (bookmark.bookmarkType === 'ARTICLE' && bookmark.articleId) {
      navigate(`/articles/${bookmark.articleId}`)
    } else if (bookmark.bookmarkType === 'AI_SUMMARY' && bookmark.aiSummaryId) {
      navigate(`/summaries/${bookmark.aiSummaryId}`)
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

  if (authLoading || loading) {
    return <LoadingSpinner />
  }

  if (bookmarks.length === 0) {
    return <EmptyState Icon={BookmarkIcon} title="저장된 북마크가 없습니다" />
  }

  return (
    <div className="w-full px-4 sm:px-6 lg:px-10 py-8 pb-24 lg:pb-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl sm:text-4xl font-bold text-gray-100 mb-2">내 북마크</h1>
          <p className="text-gray-400">총 {totalElements}개의 북마크가 저장되었습니다</p>
        </div>

        {/* Bookmarks List */}
        <div className="space-y-4">
          {bookmarks.map((bookmark) => {
            // 기사 북마크인 경우
            if (bookmark.bookmarkType === 'ARTICLE' && bookmark.article) {
              return (
                <ArticleCard
                  key={bookmark.id}
                  article={bookmark.article}
                  isBookmarked={true}
                  onBookmarkClick={() => handleRemoveBookmark(bookmark)}
                  onArticleClick={() => handleClick(bookmark)}
                />
              )
            }
            // AI 요약 북마크인 경우
            else if (bookmark.bookmarkType === 'AI_SUMMARY' && bookmark.aiSummary) {
              return (
                <div
                  key={bookmark.id}
                  onClick={() => handleClick(bookmark)}
                  className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6 sm:p-8 space-y-4 hover:border-purple-400/40 transition-colors cursor-pointer"
                >
                  {/* Header */}
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex items-center gap-3 flex-1 min-w-0">
                      <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center flex-shrink-0">
                        <Activity className="w-6 h-6 text-gray-900" />
                      </div>
                      <h2 className="text-xl sm:text-2xl font-bold text-gray-100 hover:text-purple-400 transition-colors truncate">{bookmark.aiSummary.title}</h2>
                    </div>
                    <div className="flex items-center gap-3 flex-shrink-0">
                      <div className="flex items-center gap-2 text-sm text-gray-400">
                        <Clock className="w-4 h-4" />
                        <span className="hidden sm:inline">{formatAbsoluteDate(bookmark.aiSummary.generatedAt)}</span>
                      </div>
                      <button
                        onClick={(e) => {
                          e.stopPropagation()
                          handleRemoveBookmark(bookmark)
                        }}
                        className="flex items-center gap-2 text-purple-400 hover:text-purple-300 transition-colors"
                        title="북마크 제거"
                      >
                        <BookmarkCheck className="w-5 h-5 fill-current" />
                      </button>
                    </div>
                  </div>

                  {/* Key Highlights */}
                  {bookmark.aiSummary.keyHighlights && (
                    <div className="bg-purple-400/10 border border-purple-400/30 rounded-lg p-4">
                      <div className="flex items-center gap-2 mb-3">
                        <TrendingUp className="w-5 h-5 text-purple-400" />
                        <h3 className="text-lg font-semibold text-purple-300">주요 하이라이트</h3>
                      </div>
                      <div className="space-y-2">
                        {parseHighlights(bookmark.aiSummary.keyHighlights).map((highlight, index) => (
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
                      <span>관련 기사 {bookmark.aiSummary.relatedArticlesCount}개</span>
                    </div>
                  </div>
                </div>
              )
            }
            return null
          })}
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <>
            <div className="mt-8 flex items-center justify-center gap-2 flex-wrap">
              {/* Previous Button */}
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="p-2 rounded-lg bg-[#1a1f3a] border border-cyan-400/30 text-gray-300 hover:text-cyan-400 hover:border-cyan-400 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
              >
                <ChevronLeft className="w-5 h-5" />
              </button>

              {/* First Page */}
              {getPageNumbers()[0] > 0 && (
                <>
                  <button
                    onClick={() => handlePageChange(0)}
                    className="px-4 py-2 rounded-lg bg-[#1a1f3a] border border-cyan-400/30 text-gray-300 hover:text-cyan-400 hover:border-cyan-400 transition-all"
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
                      ? 'bg-cyan-400 border-cyan-400 text-gray-900 font-bold'
                      : 'bg-[#1a1f3a] border-cyan-400/30 text-gray-300 hover:text-cyan-400 hover:border-cyan-400'
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
                    className="px-4 py-2 rounded-lg bg-[#1a1f3a] border border-cyan-400/30 text-gray-300 hover:text-cyan-400 hover:border-cyan-400 transition-all"
                  >
                    {totalPages}
                  </button>
                </>
              )}

              {/* Next Button */}
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage === totalPages - 1}
                className="p-2 rounded-lg bg-[#1a1f3a] border border-cyan-400/30 text-gray-300 hover:text-cyan-400 hover:border-cyan-400 disabled:opacity-30 disabled:cursor-not-allowed transition-all"
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
      </div>
    </div>
  )
}

export default BookmarksPage
