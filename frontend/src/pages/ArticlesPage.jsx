import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { ChevronLeft, ChevronRight, Search, X } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { bookmarkService } from '../services/bookmarkService'
import { articleService } from '../services/articleService'
import LoadingSpinner from '../components/LoadingSpinner'
import ArticleCard from '../components/ArticleCard'
import EmptyState from '../components/EmptyState'

const ArticlesPage = () => {
  const [articles, setArticles] = useState([])
  const [currentPage, setCurrentPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [totalElements, setTotalElements] = useState(0)
  const [loading, setLoading] = useState(true)
  const [bookmarkedArticles, setBookmarkedArticles] = useState({})
  const [searchKeyword, setSearchKeyword] = useState('')
  const [activeSearchKeyword, setActiveSearchKeyword] = useState('')
  const [isSearching, setIsSearching] = useState(false)
  const navigate = useNavigate()
  const { isAuthenticated } = useAuth()
  const articlesPerPage = 10

  useEffect(() => {
    loadArticles(currentPage)
  }, [currentPage, isAuthenticated, isSearching, activeSearchKeyword])

  const loadArticles = async (page) => {
    try {
      setLoading(true)

      const response = isSearching && activeSearchKeyword.trim()
        ? await articleService.search(activeSearchKeyword.trim(), page, articlesPerPage)
        : await articleService.getAll(page, articlesPerPage)

      setArticles(response.content)
      setTotalPages(response.totalPages)
      setTotalElements(response.totalElements)

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
      console.error('Failed to load articles:', error)
      toast.error('기사를 불러오는데 실패했습니다')
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (e) => {
    e.preventDefault()
    if (searchKeyword.trim()) {
      setActiveSearchKeyword(searchKeyword.trim())
      setIsSearching(true)
      setCurrentPage(0)
    }
  }

  const handleClearSearch = () => {
    setSearchKeyword('')
    setActiveSearchKeyword('')
    setIsSearching(false)
    setCurrentPage(0)
  }

  const highlightText = (text, keyword) => {
    if (!keyword || !isSearching) return text

    const parts = text.split(new RegExp(`(${keyword})`, 'gi'))
    return parts.map((part, index) =>
      part.toLowerCase() === keyword.toLowerCase()
        ? <mark key={index} className="bg-yellow-400/30 text-yellow-200 px-1 rounded">{part}</mark>
        : part
    )
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
        toast.success('북마크가 제거되었습니다')
      } else {
        await bookmarkService.addBookmark('ARTICLE', articleId, null)
        setBookmarkedArticles(prev => ({ ...prev, [articleId]: true }))
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

  if (articles.length === 0) {
    return <EmptyState title="아직 기사가 없습니다" />
  }

  return (
    <div className="w-full px-4 sm:px-6 lg:px-10 py-8 pb-24 lg:pb-8">
      <div className="max-w-6xl mx-auto">
        {/* Header */}
        <div className="mb-8">
          <h1 className="text-3xl sm:text-4xl font-bold text-gray-100 mb-2">최신 기사</h1>
          <p className="text-gray-400">총 {totalElements}개의 AI 뉴스가 수집되었습니다</p>
        </div>

        {/* Search Bar */}
        <div className="mb-6">
          <form onSubmit={handleSearch} className="relative">
            <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              placeholder="기사 제목으로 검색..."
              className="w-full pl-12 pr-24 py-3 bg-[#1a1f3a]/90 border-2 border-cyan-400/20 rounded-lg text-gray-100 placeholder-gray-400 focus:outline-none focus:border-cyan-400/60 transition-colors"
            />
            {searchKeyword && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="absolute right-20 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-cyan-400 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            )}
            <button
              type="submit"
              className="absolute right-2 top-1/2 transform -translate-y-1/2 px-4 py-1.5 bg-cyan-400 text-gray-900 font-semibold rounded-md hover:bg-cyan-300 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              disabled={!searchKeyword.trim()}
            >
              검색
            </button>
          </form>
          {isSearching && (
            <div className="mt-2 text-sm text-cyan-400">
              "{activeSearchKeyword}" 검색 결과: {totalElements}개
            </div>
          )}
        </div>

        {/* Articles List */}
        <div className="space-y-4">
          {articles.map((article) => (
            <ArticleCard
              key={article.id}
              article={article}
              isBookmarked={bookmarkedArticles[article.id]}
              onBookmarkClick={handleBookmarkClick}
              onArticleClick={handleArticleClick}
              highlightKeyword={activeSearchKeyword}
              highlightText={highlightText}
            />
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

export default ArticlesPage
