import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import HomePage from './pages/HomePage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ArticlesPage from './pages/ArticlesPage'
import ArticleDetailPage from './pages/ArticleDetailPage'
import BookmarksPage from './pages/BookmarksPage'
import SummariesPage from './pages/SummariesPage'
import SummaryDetailPage from './pages/SummaryDetailPage'
import ProfilePage from './pages/ProfilePage'
import Layout from './components/Layout'

function App() {
  return (
    <AuthProvider>
      <Router>
        <Layout>
          <Routes>
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/articles" element={<ArticlesPage />} />
            <Route path="/articles/:id" element={<ArticleDetailPage />} />
            <Route path="/bookmarks" element={<BookmarksPage />} />
            <Route path="/summaries" element={<SummariesPage />} />
            <Route path="/summaries/:id" element={<SummaryDetailPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Routes>
        </Layout>
      </Router>
    </AuthProvider>
  )
}

export default App
