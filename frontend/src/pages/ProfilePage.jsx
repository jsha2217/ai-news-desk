import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Lock, Trash2, User as UserIcon } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { authService } from '../services/authService'

const ProfilePage = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [deletePassword, setDeletePassword] = useState('')
  const [passwordMessage, setPasswordMessage] = useState({ type: '', text: '' })
  const [deleteMessage, setDeleteMessage] = useState({ type: '', text: '' })
  const [loading, setLoading] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const handleChangePassword = async (e) => {
    e.preventDefault()
    setPasswordMessage({ type: '', text: '' })

    // 유효성 검사
    if (!currentPassword || !newPassword || !confirmPassword) {
      setPasswordMessage({ type: 'error', text: '모든 필드를 입력해주세요.' })
      return
    }

    if (newPassword !== confirmPassword) {
      setPasswordMessage({ type: 'error', text: '새 비밀번호가 일치하지 않습니다.' })
      return
    }

    if (newPassword.length < 6) {
      setPasswordMessage({ type: 'error', text: '비밀번호는 최소 6자 이상이어야 합니다.' })
      return
    }

    try {
      setLoading(true)
      await authService.changePassword(currentPassword, newPassword)
      setPasswordMessage({ type: 'success', text: '비밀번호가 성공적으로 변경되었습니다.' })
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
    } catch (error) {
      setPasswordMessage({
        type: 'error',
        text: error.response?.data?.message || '비밀번호 변경에 실패했습니다.'
      })
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteAccount = async (e) => {
    e.preventDefault()
    setDeleteMessage({ type: '', text: '' })

    if (!deletePassword) {
      setDeleteMessage({ type: 'error', text: '비밀번호를 입력해주세요.' })
      return
    }

    try {
      setLoading(true)
      await authService.deleteAccount(deletePassword)
      setDeleteMessage({ type: 'success', text: '계정이 삭제되었습니다. 로그아웃됩니다.' })
      setTimeout(() => {
        logout()
        navigate('/')
      }, 1500)
    } catch (error) {
      setDeleteMessage({
        type: 'error',
        text: error.response?.data?.message || '계정 삭제에 실패했습니다.'
      })
      setLoading(false)
    }
  }

  return (
    <div
      className="h-screen w-full overflow-y-auto pb-16 lg:pb-0"
      style={{ height: 'calc(100vh - 4rem)' }}
    >
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-10 py-8 space-y-6">
        {/* 사용자 정보 */}
        <div className="card bg-gradient-to-br from-cyan-400/10 to-purple-400/10 border-2 border-cyan-400/30 p-6">
          <div className="flex items-center gap-4 mb-4">
            <div className="w-16 h-16 rounded-full bg-gradient-to-br from-cyan-400 to-purple-600 flex items-center justify-center">
              <UserIcon className="w-8 h-8 text-gray-900" />
            </div>
            <div>
              <h1 className="text-2xl font-bold text-gray-100">{user?.username || '사용자'}</h1>
              <p className="text-gray-400">{user?.email}</p>
            </div>
          </div>
          <div className="text-sm text-gray-400">
            가입일: {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ko-KR') : '-'}
          </div>
        </div>

        {/* 비밀번호 변경 */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6">
          <div className="flex items-center gap-3 mb-6">
            <Lock className="w-6 h-6 text-purple-400" />
            <h2 className="text-xl font-bold text-gray-100">비밀번호 변경</h2>
          </div>
          {passwordMessage.text && (
            <div
              className={`p-4 rounded-lg mb-4 ${
                passwordMessage.type === 'success'
                  ? 'bg-green-400/10 border border-green-400/30 text-green-400'
                  : 'bg-red-400/10 border border-red-400/30 text-red-400'
              }`}
            >
              {passwordMessage.text}
            </div>
          )}
          <form onSubmit={handleChangePassword} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                현재 비밀번호
              </label>
              <input
                type="password"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                className="w-full px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:border-purple-400 transition-colors"
                placeholder="현재 비밀번호를 입력하세요"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                새 비밀번호
              </label>
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="w-full px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:border-purple-400 transition-colors"
                placeholder="새 비밀번호를 입력하세요 (최소 6자)"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                새 비밀번호 확인
              </label>
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:border-purple-400 transition-colors"
                placeholder="새 비밀번호를 다시 입력하세요"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full py-3 bg-gradient-to-r from-purple-400 to-purple-600 text-gray-900 font-semibold rounded-lg hover:from-purple-500 hover:to-purple-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading ? '변경 중...' : '비밀번호 변경'}
            </button>
          </form>
        </div>

        {/* 회원탈퇴 */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-red-400/20 p-6">
          <div className="flex items-center gap-3 mb-6">
            <Trash2 className="w-6 h-6 text-red-400" />
            <h2 className="text-xl font-bold text-gray-100">회원 탈퇴</h2>
          </div>
          {!showDeleteConfirm ? (
            <div>
              <p className="text-gray-400 mb-4">
                계정을 삭제하면 모든 데이터가 영구적으로 삭제되며 복구할 수 없습니다.
              </p>
              <button
                onClick={() => {
                  setShowDeleteConfirm(true)
                  setDeleteMessage({ type: '', text: '' })
                }}
                className="w-full py-3 bg-red-400/10 border border-red-400/30 text-red-400 font-semibold rounded-lg hover:bg-red-400/20 transition-all"
              >
                회원 탈퇴하기
              </button>
            </div>
          ) : (
            <form onSubmit={handleDeleteAccount} className="space-y-4">
              <p className="text-red-400 font-medium mb-4">
                정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.
              </p>
              {deleteMessage.text && (
                <div
                  className={`p-4 rounded-lg ${
                    deleteMessage.type === 'success'
                      ? 'bg-green-400/10 border border-green-400/30 text-green-400'
                      : 'bg-red-400/10 border border-red-400/30 text-red-400'
                  }`}
                >
                  {deleteMessage.text}
                </div>
              )}
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  비밀번호 확인
                </label>
                <input
                  type="password"
                  value={deletePassword}
                  onChange={(e) => setDeletePassword(e.target.value)}
                  className="w-full px-4 py-3 bg-gray-800/50 border border-gray-700 rounded-lg text-gray-100 focus:outline-none focus:border-red-400 transition-colors"
                  placeholder="비밀번호를 입력하세요"
                />
              </div>
              <div className="flex gap-3">
                <button
                  type="button"
                  onClick={() => {
                    setShowDeleteConfirm(false)
                    setDeletePassword('')
                    setDeleteMessage({ type: '', text: '' })
                  }}
                  className="flex-1 py-3 bg-gray-700 text-gray-300 font-semibold rounded-lg hover:bg-gray-600 transition-all"
                >
                  취소
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="flex-1 py-3 bg-red-400 text-gray-900 font-semibold rounded-lg hover:bg-red-500 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {loading ? '삭제 중...' : '계정 삭제'}
                </button>
              </div>
            </form>
          )}
        </div>
      </div>
    </div>
  )
}

export default ProfilePage
