import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Rocket, Bookmark, FileText, LogIn, LogOut, UserPlus, User } from 'lucide-react'

const Layout = ({ children }) => {
  const { isAuthenticated, user, logout } = useAuth()
  const location = useLocation()
  const currentPath = location.pathname

  return (
    <div className="min-h-screen" style={{ background: 'linear-gradient(135deg, #0a0e27 0%, #1a1f3a 100%)' }}>
      {/* Header */}
      <header className="fixed top-0 left-0 right-0 h-16 bg-[#1a1f3a]/80 backdrop-blur-md border-b border-cyan-400/10 z-50 shadow-[0_4px_20px_rgba(0,0,0,0.5)]">
        <div className="max-w-[1400px] mx-auto px-4 sm:px-6 lg:px-10 h-full flex items-center justify-between">
          {/* Logo */}
          <Link to="/" className={`flex items-center gap-3 text-2xl font-bold bg-gradient-to-r ${currentPath === '/' ? 'from-cyan-300 to-cyan-500' : 'from-cyan-400 to-cyan-600 hover:from-cyan-300 hover:to-cyan-500'} bg-clip-text text-transparent transition-all`}>
            <Rocket className={`w-7 h-7 transition-all ${currentPath === '/' ? 'text-cyan-300 scale-110' : 'text-cyan-400 hover:text-cyan-300 hover:scale-110'}`} />
            <span>AI News Desk</span>
          </Link>

          {/* Navigation & Icons */}
          <div className="flex items-center gap-2 sm:gap-5">
            {/* Navigation Links - Hidden on mobile */}
            <nav className="hidden lg:flex items-center gap-1">
              <Link to="/articles" className="px-4 py-2 text-gray-300 hover:text-cyan-400 rounded-md text-sm font-medium transition-colors">
                기사
              </Link>
              <Link to="/summaries" className="flex items-center gap-1.5 px-4 py-2 text-gray-300 hover:text-cyan-400 rounded-md text-sm font-medium transition-colors">
                <FileText className="w-4 h-4" />
                AI 요약
              </Link>
              {isAuthenticated && (
                <Link to="/bookmarks" className="flex items-center gap-1.5 px-4 py-2 text-gray-300 hover:text-cyan-400 rounded-md text-sm font-medium transition-colors">
                  <Bookmark className="w-4 h-4" />
                  북마크
                </Link>
              )}
            </nav>

            {isAuthenticated ? (
              <div className="flex items-center gap-3">
                <Link to="/profile" className="flex items-center gap-2 px-3 py-1.5 bg-cyan-400/10 border border-cyan-400/30 rounded-lg hover:bg-cyan-400/20 transition-colors">
                  <User className="w-4 h-4 text-cyan-400" />
                  <span className="text-sm text-gray-300">{user?.username || user?.email}</span>
                </Link>
                <button
                  onClick={logout}
                  className="flex items-center gap-1.5 px-3 py-2 text-gray-300 hover:text-red-400 rounded-md text-sm font-medium transition-colors"
                >
                  <LogOut className="w-4 h-4" />
                  <span className="hidden sm:inline">로그아웃</span>
                </button>
              </div>
            ) : (
              <>
                <Link to="/login" className="flex items-center gap-1.5 px-3 py-2 text-gray-300 hover:text-cyan-400 rounded-md text-sm font-medium transition-colors">
                  <LogIn className="w-4 h-4" />
                  <span className="hidden sm:inline">로그인</span>
                </Link>
                <Link to="/register" className="flex items-center gap-1.5 px-3 sm:px-5 py-2 bg-gradient-to-br from-cyan-400 to-cyan-600 text-gray-900 font-bold rounded-lg text-sm transition-all duration-300 hover:shadow-[0_0_20px_rgba(0,212,255,0.5)]">
                  <UserPlus className="w-4 h-4" />
                  <span className="hidden sm:inline">회원가입</span>
                </Link>
              </>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="pt-16 pb-16">
        {children}
      </main>

      {/* Bottom Navigation - Visible on all screens */}
      <nav className="fixed bottom-0 left-0 right-0 h-16 bg-[#1a1f3a]/95 backdrop-blur-md border-t border-cyan-400/10 z-50">
        <div className="h-full flex items-center justify-around px-4">
          <Link to="/articles" className={`flex flex-col items-center gap-1 transition-colors ${currentPath === '/articles' ? 'text-cyan-400' : 'text-gray-400 hover:text-cyan-400'}`}>
            <FileText className={`w-5 h-5 transition-transform ${currentPath === '/articles' ? 'scale-110' : ''}`} />
            <span className="text-xs font-semibold">기사</span>
          </Link>
          <Link to="/summaries" className={`flex flex-col items-center gap-1 transition-colors ${currentPath === '/summaries' ? 'text-cyan-400' : 'text-gray-400 hover:text-cyan-400'}`}>
            <Rocket className={`w-5 h-5 transition-transform ${currentPath === '/summaries' ? 'scale-110' : ''}`} />
            <span className="text-xs font-semibold">AI요약</span>
          </Link>
          {isAuthenticated && (
            <Link to="/bookmarks" className={`flex flex-col items-center gap-1 transition-colors ${currentPath === '/bookmarks' ? 'text-cyan-400' : 'text-gray-400 hover:text-cyan-400'}`}>
              <Bookmark className={`w-5 h-5 transition-transform ${currentPath === '/bookmarks' ? 'scale-110' : ''}`} />
              <span className="text-xs font-semibold">북마크</span>
            </Link>
          )}
          <Link to={isAuthenticated ? "/profile" : "/login"} className={`flex flex-col items-center gap-1 transition-colors ${currentPath === '/profile' || currentPath === '/login' ? 'text-cyan-400' : 'text-gray-400 hover:text-cyan-400'}`}>
            <User className={`w-5 h-5 transition-transform ${currentPath === '/profile' || currentPath === '/login' ? 'scale-110' : ''}`} />
            <span className="text-xs font-semibold">프로필</span>
          </Link>
        </div>
      </nav>
    </div>
  )
}

export default Layout
