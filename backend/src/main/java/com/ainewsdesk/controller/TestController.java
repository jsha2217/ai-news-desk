package com.ainewsdesk.controller;

import com.ainewsdesk.scheduler.AiSummaryScheduler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 테스트용 컨트롤러
 */
@RestController
@RequestMapping("/test")
public class TestController {

    private final AiSummaryScheduler aiSummaryScheduler;

    public TestController(AiSummaryScheduler aiSummaryScheduler) {
        this.aiSummaryScheduler = aiSummaryScheduler;
    }

    /**
     * AI 요약 스케줄러 수동 실행
     */
    @PostMapping("/generate-summary")
    public ResponseEntity<String> generateSummary() {
        aiSummaryScheduler.generateDailySummary();
        return ResponseEntity.ok("AI 요약 생성 작업이 시작되었습니다.");
    }
}
