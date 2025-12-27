import { Clock, Tag, Bookmark, BookmarkCheck } from 'lucide-react'
import { formatRelativeDate } from '../utils/dateFormatter'

const ArticleCard = ({
  article,
  isBookmarked = false,
  onBookmarkClick,
  onArticleClick,
  highlightKeyword = null,
  highlightText = null
}) => {
  const renderTitle = () => {
    if (highlightKeyword && highlightText) {
      return highlightText(article.title, highlightKeyword)
    }
    return article.title
  }

  return (
    <div
      onClick={() => onArticleClick(article.id)}
      className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-cyan-400/20 p-6 sm:p-8 space-y-4 hover:border-cyan-400/40 transition-colors cursor-pointer"
    >
      {/* Header */}
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-center gap-3 flex-wrap">
          <span className="badge-official">
            OFFICIAL
          </span>
          <div className="flex items-center gap-2 text-sm text-gray-400">
            <Clock className="w-4 h-4" />
            <span>{formatRelativeDate(article.crawledAt)}</span>
          </div>
        </div>
        {onBookmarkClick && (
          <button
            onClick={(e) => {
              e.stopPropagation()
              onBookmarkClick(article.id)
            }}
            className="flex items-center gap-2 text-cyan-400 hover:text-cyan-300 transition-colors"
            title={isBookmarked ? "북마크 제거" : "북마크 추가"}
          >
            {isBookmarked ? (
              <BookmarkCheck className="w-5 h-5 fill-current" />
            ) : (
              <Bookmark className="w-5 h-5" />
            )}
          </button>
        )}
      </div>

      {/* Title */}
      <h3 className="text-xl sm:text-2xl lg:text-3xl font-bold text-gray-100 leading-tight group-hover:text-cyan-400 transition-colors">
        {renderTitle()}
      </h3>

      {/* Description */}
      <p className="text-base sm:text-lg text-gray-300 leading-relaxed line-clamp-2">
        {article.description}
      </p>

      {/* Source */}
      <div className="flex items-center gap-2 text-sm text-gray-400">
        <span>출처:</span>
        <span className="text-cyan-400">{article.sourceName}</span>
      </div>

      {/* Tags */}
      <div className="flex items-center gap-2 flex-wrap">
        <Tag className="w-4 h-4 text-gray-400" />
        <span className="tag">
          #YouTube
        </span>
        <span className="tag">
          #{article.category}
        </span>
      </div>
    </div>
  )
}

export default ArticleCard
