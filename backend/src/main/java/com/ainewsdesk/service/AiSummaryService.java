package com.ainewsdesk.service;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.entity.AiSummary.SummaryStatus;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.repository.AiSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 요약 관리 서비스
 * <p>AI 요약 조회, 생성, 발행, 삭제 비즈니스 로직 처리</p>
 */
@Service
@Transactional(readOnly = true)
public class AiSummaryService {

    private static final Logger logger = LoggerFactory.getLogger(AiSummaryService.class);

    private final AiSummaryRepository aiSummaryRepository;

    public AiSummaryService(AiSummaryRepository aiSummaryRepository) {
        this.aiSummaryRepository = aiSummaryRepository;
    }

    /**
     * 전체 AI 요약 조회 - 최신순 정렬
     */
    public Page<AiSummary> getAllSummaries(Pageable pageable) {
        return aiSummaryRepository.findByOrderBySummaryPeriodStartDesc(pageable);
    }

    /**
     * 발행된 AI 요약 조회 - PUBLISHED 상태만
     */
    public Page<AiSummary> getPublishedSummaries(Pageable pageable) {
        return aiSummaryRepository.findByStatusOrderBySummaryPeriodStartDesc(SummaryStatus.PUBLISHED, pageable);
    }

    /**
     * 시간 범위별 AI 요약 조회
     */
    public List<AiSummary> getSummariesByDateRange(LocalDateTime start, LocalDateTime end) {
        return aiSummaryRepository.findBySummaryPeriodStartBetweenOrderBySummaryPeriodStartDesc(start, end);
    }

    /**
     * 최신 발행 AI 요약 조회
     */
    public Optional<AiSummary> getLatestSummary() {
        return aiSummaryRepository.findLatestPublished();
    }

    /**
     * AI 요약 상세 조회
     */
    public AiSummary getSummaryById(Long id) {
        return aiSummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI 요약을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * AI 요약 생성 - 초기 상태 DRAFT
     */
    @Transactional
    public AiSummary createSummary(AiSummary summary) {
        // 상태가 설정되지 않은 경우 DRAFT로 초기화
        if (summary.getStatus() == null) {
            summary.setStatus(SummaryStatus.DRAFT);
        }

        return aiSummaryRepository.save(summary);
    }

    /**
     * AI 요약 발행 - PUBLISHED 상태로 변경
     */
    @Transactional
    public AiSummary publishSummary(Long id) {
        // AI 요약 조회
        AiSummary summary = aiSummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI 요약을 찾을 수 없습니다. ID: " + id));

        // 상태를 PUBLISHED로 변경
        summary.setStatus(SummaryStatus.PUBLISHED);

        // 저장 (JPA dirty checking에 의해 자동 업데이트)
        return aiSummaryRepository.save(summary);
    }

    /**
     * AI 요약 삭제
     */
    @Transactional
    public void deleteSummary(Long id) {
        // AI 요약 존재 여부 확인
        if (!aiSummaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("AI 요약을 찾을 수 없습니다. ID: " + id);
        }

        // 삭제
        aiSummaryRepository.deleteById(id);
    }
}
