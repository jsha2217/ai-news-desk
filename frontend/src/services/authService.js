import api from './api'

export const authService = {
  // 로그인
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },

  // 회원가입
  register: async (email, password, username) => {
    const response = await api.post('/auth/register', { email, password, username })
    return response.data
  },

  // 현재 사용자 정보 가져오기
  getCurrentUser: async () => {
    const response = await api.get('/auth/me')
    return response.data
  },

  // 비밀번호 변경
  changePassword: async (currentPassword, newPassword) => {
    const response = await api.put('/auth/password', { currentPassword, newPassword })
    return response.data
  },

  // 회원 탈퇴
  deleteAccount: async (password) => {
    const response = await api.delete('/auth/account', { data: { password } })
    return response.data
  },
}
