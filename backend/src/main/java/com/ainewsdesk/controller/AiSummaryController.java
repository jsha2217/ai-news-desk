package com.ainewsdesk.controller;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.service.AiSummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * <p>AI 요약 조회, 생성, 발행, 삭제 API 엔드포인트 제공</p>
 */
@RestController
@RequestMapping("/ai-summaries")
@Tag(name = "AI Summaries", description = "AI 요약 관리 API")
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;

    public AiSummaryController(AiSummaryService aiSummaryService) {
        this.aiSummaryService = aiSummaryService;
    }

    /**
     * 전체 AI 요약 목록 조회 - 페이징, 최신순 정렬
     *
     * @param pageable 페이징 정보
     * @return Page<AiSummary> AI 요약 목록
     */
    @GetMapping
    @Operation(summary = "모든 AI 요약 조회", description = "전체 AI 요약 목록 페이징 조회, 최신순 정렬")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ResponseEntity<Page<AiSummary>> getAllSummaries(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AiSummary> summaries = aiSummaryService.getAllSummaries(pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 발행된 AI 요약 목록 조회 - PUBLISHED 상태만
     *
     * @param pageable 페이징 정보
     * @return Page<AiSummary> 발행된 AI 요약 목록
     */
    @GetMapping("/published")
    @Operation(summary = "발행된 AI 요약 조회", description = "PUBLISHED 상태 AI 요약 목록 페이징 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public ResponseEntity<Page<AiSummary>> getPublishedSummaries(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<AiSummary> summaries = aiSummaryService.getPublishedSummaries(pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * 최신 발행 AI 요약 조회 - PUBLISHED 상태 최신 1건
     *
     * @return AiSummary 최신 발행 AI 요약
     */
    @GetMapping("/latest")
    @Operation(summary = "최신 AI 요약 조회", description = "최신 발행(PUBLISHED) AI 요약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "발행된 요약이 없음")
    })
    public ResponseEntity<AiSummary> getLatestSummary() {
        Optional<AiSummary> summary = aiSummaryService.getLatestSummary();
        return summary.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * AI 요약 상세 조회
     *
     * @param id AI 요약 ID
     * @return AiSummary AI 요약 상세
     */
    @GetMapping("/{id}")
    @Operation(summary = "AI 요약 상세 조회", description = "AI 요약 ID로 상세 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "AI 요약을 찾을 수 없음")
    })
    public ResponseEntity<AiSummary> getSummaryById(@PathVariable Long id) {
        AiSummary summary = aiSummaryService.getSummaryById(id);
        return ResponseEntity.ok(summary);
    }

    /**
     * AI 요약 생성 (크롤러용) - 초기 상태 DRAFT
     *
     * @param summary AI 요약 데이터
     * @return AiSummary 생성된 AI 요약
     */
    @PostMapping
    @Operation(summary = "AI 요약 생성", description = "새 AI 요약 생성, 초기 상태 DRAFT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "AI 요약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<AiSummary> createSummary(@Valid @RequestBody AiSummary summary) {
        AiSummary createdSummary = aiSummaryService.createSummary(summary);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSummary);
    }

    /**
     * AI 요약 발행 - PUBLISHED 상태로 변경
     *
     * @param id AI 요약 ID
     * @return AiSummary 발행된 AI 요약
     */
    @PutMapping("/{id}/publish")
    @Operation(summary = "AI 요약 발행", description = "AI 요약 상태를 PUBLISHED로 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AI 요약 발행 성공"),
            @ApiResponse(responseCode = "404", description = "AI 요약을 찾을 수 없음")
    })
    public ResponseEntity<AiSummary> publishSummary(@PathVariable Long id) {
        AiSummary publishedSummary = aiSummaryService.publishSummary(id);
        return ResponseEntity.ok(publishedSummary);
    }

    /**
     * AI 요약 삭제
     *
     * @param id AI 요약 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "AI 요약 삭제", description = "AI 요약 ID로 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "AI 요약 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "AI 요약을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteSummary(@PathVariable Long id) {
        aiSummaryService.deleteSummary(id);
        return ResponseEntity.noContent().build();
    }
}
