import { createContext, useContext, useState, useEffect } from 'react'
import { authService } from '../services/authService'

const AuthContext = createContext(null)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    // 로컬 스토리지에서 토큰 확인
    const token = localStorage.getItem('token')
    if (token) {
      // 토큰이 있으면 사용자 정보 가져오기
      authService.getCurrentUser()
        .then(userData => setUser(userData))
        .catch(() => {
          localStorage.removeItem('token')
        })
        .finally(() => setLoading(false))
    } else {
      setLoading(false)
    }
  }, [])

  const login = async (email, password) => {
    const response = await authService.login(email, password)
    localStorage.setItem('token', response.token)
    setUser({
      id: response.userId,
      email: response.email,
      username: response.username
    })
    return response
  }

  const register = async (email, password, username) => {
    const response = await authService.register(email, password, username)
    // 회원가입 후 자동 로그인을 위해 사용자 정보 설정
    if (response.token) {
      localStorage.setItem('token', response.token)
      setUser({
        id: response.userId,
        email: response.email,
        username: response.username
      })
    }
    return response
  }

  const logout = () => {
    localStorage.removeItem('token')
    setUser(null)
    window.location.href = '/login'
  }

  const value = {
    user,
    login,
    register,
    logout,
    loading,
    isAuthenticated: !!user
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
