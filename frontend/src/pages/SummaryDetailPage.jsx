import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import toast from 'react-hot-toast'
import { Clock, TrendingUp, Activity, ArrowLeft, Sparkles } from 'lucide-react'
import { summaryService } from '../services/summaryService'
import { formatAbsoluteDate } from '../utils/dateFormatter'
import { parseHighlights } from '../utils/textParser'
import LoadingSpinner from '../components/LoadingSpinner'

const SummaryDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()
  const [summary, setSummary] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchSummary()
  }, [id])

  const fetchSummary = async () => {
    try {
      setLoading(true)
      const response = await summaryService.getSummaryById(id)
      setSummary(response)
    } catch (error) {
      console.error('Failed to fetch AI summary:', error)
      toast.error('AI 요약을 불러오는데 실패했습니다')
      // 더미 데이터 사용
      const dummyData = {
        1: {
          id: 1,
          title: 'GPT-5와 차세대 AI 모델의 시대가 열린다',
          content: `## OpenAI GPT-5 개발 본격화

OpenAI는 차세대 언어 모델 GPT-5의 개발 로드맵을 공개하며 2026년 초 출시를 목표로 하고 있다고 밝혔습니다. GPT-5는 현재의 GPT-4 대비 추론 능력이 대폭 향상되고, 멀티모달 처리 성능이 개선될 것으로 예상됩니다.

## AlphaFold 3의 혁신적인 성과

DeepMind의 AlphaFold 3가 단백질 구조 예측 정확도 95%를 달성하며 생명과학 분야에 새로운 이정표를 세웠습니다. 이는 신약 개발과 질병 치료 연구에 혁신적인 변화를 가져올 것으로 기대됩니다.

## Meta Llama 4 베타 테스트 시작

Meta는 오픈소스 대규모 언어 모델 Llama 4의 베타 테스트를 시작했습니다. Llama 4는 이전 버전 대비 30% 향상된 성능을 보이며, 특히 코드 생성과 수학적 추론 능력이 크게 개선되었습니다.

## 삼성전자, AI 반도체 시장 공략

삼성전자가 AI 반도체 개발에 100억 달러를 투자한다고 발표했습니다. 차세대 HBM(고대역폭 메모리)과 NPU(신경망 처리 장치) 개발에 집중하며, 2026년까지 시장 점유율 1위를 목표로 하고 있습니다.`,
          keyHighlights: '• GPT-5 개발 로드맵 공개 (2026년 초 출시 목표)\n• AlphaFold 3, 단백질 구조 예측 정확도 95% 달성\n• Meta Llama 4 베타 테스트 시작\n• 삼성전자, AI 반도체에 100억 달러 투자',
          relatedArticlesCount: 15,
          generatedAt: '2025-12-25T10:00:00',
          summaryPeriodStart: '2025-12-24T22:00:00',
          summaryPeriodEnd: '2025-12-25T10:00:00'
        },
        2: {
          id: 2,
          title: '기업용 AI 도구의 급격한 발전',
          content: `## Microsoft Azure AI Studio 대규모 업데이트

Microsoft는 Azure AI Studio의 대규모 업데이트를 발표하며 기업들의 AI 도입을 가속화하고 있습니다. 새로운 버전은 GPT-4 Turbo를 기본 지원하며, 커스텀 모델 파인튜닝 기능이 대폭 개선되었습니다.

## Claude 3.5 Sonnet 코딩 능력 향상

Anthropic의 Claude 3.5 Sonnet이 코딩 능력에서 40% 향상된 성능을 보였습니다. 특히 복잡한 알고리즘 구현과 디버깅 작업에서 뛰어난 성과를 보이며, 개발자들 사이에서 큰 호응을 얻고 있습니다.

## Tesla Optimus 로봇 공장 테스트

Tesla의 인간형 로봇 Optimus가 실제 공장 환경에서 테스트를 시작했습니다. 단순 반복 작업부터 시작하여 점진적으로 복잡한 조립 작업까지 수행할 예정이며, 2026년 상용화를 목표로 하고 있습니다.

## Baidu Ernie 4.0의 성능 향상

중국 Baidu의 AI 모델 Ernie 4.0이 중국어 처리에서 GPT-4를 능가하는 성능을 보였다고 발표했습니다. 특히 중국 문화와 역사에 대한 이해도가 높으며, 로컬라이제이션 측면에서 강점을 보이고 있습니다.`,
          keyHighlights: '• Microsoft Azure AI Studio 업데이트\n• Claude 3.5 Sonnet 코딩 능력 40% 향상\n• Tesla Optimus 로봇 공장 테스트 시작\n• Baidu Ernie 4.0, GPT-4 능가 주장',
          relatedArticlesCount: 12,
          generatedAt: '2025-12-24T22:00:00',
          summaryPeriodStart: '2025-12-24T10:00:00',
          summaryPeriodEnd: '2025-12-24T22:00:00'
        }
      }
      setSummary(dummyData[id])
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <LoadingSpinner />
  }

  if (!summary) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <Activity className="w-16 h-16 text-gray-600 mx-auto mb-4" />
          <p className="text-xl text-gray-400">요약을 찾을 수 없습니다</p>
          <button
            onClick={() => navigate('/summaries')}
            className="mt-4 px-6 py-3 bg-purple-400/20 border border-purple-400/30 rounded-lg text-purple-300 hover:bg-purple-400/30 transition-colors"
          >
            목록으로 돌아가기
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="w-full px-4 sm:px-6 lg:px-10 py-8 pb-24 lg:pb-8">
      <div className="max-w-4xl mx-auto">
        {/* Back Button */}
        <button
          onClick={() => navigate('/summaries')}
          className="flex items-center gap-2 text-purple-400 hover:text-purple-300 mb-6 transition-colors"
        >
          <ArrowLeft className="w-5 h-5" />
          <span>목록으로 돌아가기</span>
        </button>

        {/* Header */}
        <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6 sm:p-8 mb-6">
          <div className="flex items-start gap-3 mb-4">
            <div className="w-12 h-12 rounded-lg bg-gradient-to-br from-purple-400 to-purple-600 flex items-center justify-center">
              <Sparkles className="w-7 h-7 text-gray-900" />
            </div>
            <div className="flex-1">
              <h1 className="text-2xl sm:text-3xl lg:text-4xl font-bold text-gray-100 leading-tight">
                {summary.title}
              </h1>
            </div>
          </div>

          {/* Metadata */}
          <div className="flex flex-wrap items-center gap-4 text-sm text-gray-400 border-t border-purple-400/20 pt-4 mt-4">
            <div className="flex items-center gap-2">
              <Clock className="w-4 h-4" />
              <span>생성 시간: {formatAbsoluteDate(summary.generatedAt)}</span>
            </div>
            <div className="flex items-center gap-2">
              <TrendingUp className="w-4 h-4" />
              <span>관련 기사 {summary.relatedArticlesCount}개</span>
            </div>
            {summary.summaryPeriodStart && summary.summaryPeriodEnd && (
              <div className="flex items-center gap-2">
                <Activity className="w-4 h-4" />
                <span>
                  {formatAbsoluteDate(summary.summaryPeriodStart)} ~ {formatAbsoluteDate(summary.summaryPeriodEnd)}
                </span>
              </div>
            )}
          </div>
        </div>

        {/* Key Highlights */}
        {summary.keyHighlights && (
          <div className="card bg-purple-400/10 border-2 border-purple-400/30 p-6 sm:p-8 mb-6">
            <div className="flex items-center gap-2 mb-4">
              <TrendingUp className="w-6 h-6 text-purple-400" />
              <h2 className="text-xl sm:text-2xl font-bold text-purple-300">주요 하이라이트</h2>
            </div>
            <div className="space-y-3">
              {parseHighlights(summary.keyHighlights).map((highlight, index) => (
                <p key={index} className="text-base sm:text-lg text-gray-300 leading-relaxed">
                  {highlight}
                </p>
              ))}
            </div>
          </div>
        )}

        {/* Content */}
        {summary.content && (
          <div className="card bg-[#1a1f3a]/90 backdrop-blur-xl border-2 border-purple-400/20 p-6 sm:p-8">
            <div className="flex items-center gap-2 mb-6">
              <Activity className="w-6 h-6 text-purple-400" />
              <h2 className="text-xl sm:text-2xl font-bold text-gray-100">상세 내용</h2>
            </div>
            <div className="prose prose-invert max-w-none">
              {summary.content.split('\n\n').map((paragraph, index) => {
                if (paragraph.startsWith('## ')) {
                  return (
                    <h3 key={index} className="text-xl sm:text-2xl font-bold text-purple-300 mt-8 mb-4">
                      {paragraph.replace('## ', '')}
                    </h3>
                  )
                }
                return (
                  <p key={index} className="text-base sm:text-lg text-gray-300 leading-relaxed mb-4">
                    {paragraph}
                  </p>
                )
              })}
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default SummaryDetailPage
