package com.ainewsdesk.scheduler;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.service.AiSummaryService;
import com.ainewsdesk.service.ArticleService;
import com.ainewsdesk.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 뉴스 요약 자동 생성 스케줄러
 */
@Component
public class AiSummaryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AiSummaryScheduler.class);

    private final GeminiService geminiService;
    private final AiSummaryService aiSummaryService;

    public AiSummaryScheduler(GeminiService geminiService,
                             AiSummaryService aiSummaryService) {
        this.geminiService = geminiService;
        this.aiSummaryService = aiSummaryService;
    }

    /**
     * AI 뉴스 요약 생성 - 매일 00시, 09~23시
     */
    @Scheduled(cron = "0 0 0,9-23 * * *") // 매일 00:00, 09:00 ~ 23:00 정각
    public void generateDailySummary() {
        try {
            logger.info("===== AI 뉴스 요약 생성 시작 =====");

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

            // Gemini API를 사용하여 최신 AI 뉴스 요약 생성
            logger.info("Gemini API 호출 중... (독립 요약 생성)");

            String prompt = String.format("당신은 AI 뉴스 전문 요약가입니다. 오늘(%s) 기준으로 최신 AI 업계 뉴스, 트렌드, 주요 이슈를 요약해주세요.\n\n다음 내용을 포함해주세요:\n- 최신 AI 모델 출시 소식 (GPT, Claude, Gemini, Llama 등)\n- 주요 AI 기업들의 동향 (OpenAI, Anthropic, Google, Meta 등)\n- AI 기술 발전 및 연구 성과\n- AI 윤리 및 규제 관련 이슈\n- AI 산업 트렌드 및 시장 동향\n\n다음 형식으로 요약을 작성해주세요:\n\n1. 제목 (Title):\n- 오늘의 주요 AI 뉴스를 대표하는 흥미로운 제목 (20자 이내)\n\n2. 주요 하이라이트 (KeyHighlights):\n- 가장 중요한 4-5가지 내용을 불릿 포인트로 요약\n- 각 항목은 한 줄로 간결하게\n\n3. 상세 내용 (Content):\n- **중요: 상세 내용은 반드시 1500자 이내로 작성해주세요**\n- 전체 AI 업계 동향을 종합적으로 상세히 설명\n- 주요 동향, 기술 발전, 업계 영향, 전망 등을 포함\n- 절대 답변을 중간에 끊지 말고 완전히 작성할 것\n\n응답 형식:\nTITLE: [제목]\nHIGHLIGHTS:\n• [하이라이트1]\n• [하이라이트2]\n• [하이라이트3]\nCONTENT:\n[상세 내용 - 1500자 이하]\n\n주의사항: 답변을 절대 중간에 끊지 말고, CONTENT 섹션을 완전히 작성한 후 종료하세요.", LocalDate.now());

            String response = geminiService.generateText(prompt);
            Map<String, String> summaryResult = geminiService.parseSummaryResponse(response);

            logger.info("요약 생성 완료");
            logger.info("제목: {}", summaryResult.get("title"));

            // AiSummary 엔티티 생성 및 저장
            AiSummary aiSummary = new AiSummary();
            aiSummary.setTitle(summaryResult.get("title"));
            aiSummary.setKeyHighlights(summaryResult.get("keyHighlights"));
            aiSummary.setContent(summaryResult.get("content"));
            aiSummary.setSummaryPeriodStart(startOfDay);
            aiSummary.setSummaryPeriodEnd(now);
            aiSummary.setRelatedArticlesCount(0); // 독립 요약이므로 0
            aiSummary.setStatus(AiSummary.SummaryStatus.PUBLISHED); // 바로 발행

            AiSummary savedSummary = aiSummaryService.createSummary(aiSummary);

            logger.info("AI 요약 저장 완료. ID: {}", savedSummary.getId());
            logger.info("===== AI 뉴스 요약 생성 완료 =====");

        } catch (Exception e) {
            logger.error("AI 뉴스 요약 생성 중 오류 발생", e);
        }
    }

    /**
     * AI 뉴스 요약 생성 - 매일 10시, 22시 (프로덕션용)
     */
    // @Scheduled(cron = "0 0 10,22 * * *") // 매일 10:00, 22:00
    public void generateScheduledSummary() {
        generateDailySummary();
    }
}
