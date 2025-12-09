package com.ainewsdesk.controller;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.service.AiSummaryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * AI 요약 REST API 컨트롤러
 *
 * <p>AI가 생성한 뉴스 요약의 조회, 생성, 발행, 삭제 등의 API 엔드포인트를 제공합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@RestController
@RequestMapping("/api/summaries")
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;

    /**
     * AiSummaryController 생성자
     *
     * @param aiSummaryService AI 요약 서비스
     */
    public AiSummaryController(AiSummaryService aiSummaryService) {
        this.aiSummaryService = aiSummaryService;
    }

    /**
     * 모든 AI 요약 조회 API
     *
     * <p>모든 AI 요약을 페이징하여 조회합니다. 요약 기간 시작일 기준 최신순으로 정렬됩니다.
     * DRAFT와 PUBLISHED 상태의 요약을 모두 포함합니다.</p>
     *
     * @param pageable 페이징 정보 (기본값: 페이지 크기 10, 요약 기간 시작일 내림차순 정렬)
     * @return AI 요약 목록 페이지
     */
    @GetMapping
    public ResponseEntity<Page<AiSummary>> getAllSummaries(
            @PageableDefault(size = 10, sort = "summaryPeriodStart", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AiSummary> summaries = aiSummaryService.getAllSummaries(pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 발행된 AI 요약 조회 API
     *
     * <p>PUBLISHED 상태의 AI 요약만 페이징하여 조회합니다.
     * 요약 기간 시작일 기준 최신순으로 정렬됩니다.</p>
     *
     * @param pageable 페이징 정보 (기본값: 페이지 크기 10, 요약 기간 시작일 내림차순 정렬)
     * @return 발행된 AI 요약 목록 페이지
     */
    @GetMapping("/published")
    public ResponseEntity<Page<AiSummary>> getPublishedSummaries(
            @PageableDefault(size = 10, sort = "summaryPeriodStart", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AiSummary> summaries = aiSummaryService.getPublishedSummaries(pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 최신 AI 요약 조회 API
     *
     * <p>가장 최근에 발행된 AI 요약을 조회합니다. PUBLISHED 상태의 요약만 대상입니다.</p>
     *
     * @return 최신 발행된 AI 요약
     */
    @GetMapping("/latest")
    public ResponseEntity<AiSummary> getLatestSummary() {
        Optional<AiSummary> summary = aiSummaryService.getLatestSummary();
        return summary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * AI 요약 상세 조회 API
     *
     * <p>AI 요약 ID로 특정 요약의 상세 정보를 조회합니다.</p>
     *
     * @param id AI 요약 ID
     * @return AI 요약 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<AiSummary> getSummaryById(@PathVariable Long id) {
        AiSummary summary = aiSummaryService.getSummaryById(id);
        return ResponseEntity.ok(summary);
    }

    /**
     * AI 요약 생성 API (크롤러용)
     *
     * <p>새로운 AI 요약을 생성합니다. 초기 상태는 DRAFT로 설정됩니다.
     * 주로 AI 요약 생성 시스템이나 크롤러가 사용하는 엔드포인트입니다.</p>
     *
     * @param summary AI 요약 데이터
     * @return 생성된 AI 요약과 HTTP 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<AiSummary> createSummary(@Valid @RequestBody AiSummary summary) {
        AiSummary createdSummary = aiSummaryService.createSummary(summary);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSummary);
    }

    /**
     * AI 요약 발행 API
     *
     * <p>AI 요약의 상태를 PUBLISHED로 변경하여 발행합니다.
     * 발행된 요약은 사용자에게 공개됩니다.</p>
     *
     * @param id AI 요약 ID
     * @return 발행된 AI 요약
     */
    @PutMapping("/{id}/publish")
    public ResponseEntity<AiSummary> publishSummary(@PathVariable Long id) {
        AiSummary publishedSummary = aiSummaryService.publishSummary(id);
        return ResponseEntity.ok(publishedSummary);
    }

    /**
     * AI 요약 삭제 API
     *
     * <p>AI 요약 ID로 특정 요약을 삭제합니다.</p>
     *
     * @param id AI 요약 ID
     * @return HTTP 204 No Content 상태 코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSummary(@PathVariable Long id) {
        aiSummaryService.deleteSummary(id);
        return ResponseEntity.noContent().build();
    }
}
