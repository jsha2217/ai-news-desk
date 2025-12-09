package com.ainewsdesk.service;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.entity.AiSummary.SummaryStatus;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.repository.AiSummaryRepository;
import com.ainewsdesk.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 요약 관리 서비스
 *
 * <p>AI가 생성한 뉴스 요약의 조회, 생성, 발행, 삭제 등의 비즈니스 로직을 처리합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class AiSummaryService {

    private final AiSummaryRepository aiSummaryRepository;
    private final ArticleRepository articleRepository;

    /**
     * AiSummaryService 생성자
     *
     * @param aiSummaryRepository AI 요약 리포지토리
     * @param articleRepository 기사 리포지토리
     */
    public AiSummaryService(AiSummaryRepository aiSummaryRepository,
                           ArticleRepository articleRepository) {
        this.aiSummaryRepository = aiSummaryRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * 모든 AI 요약 조회 (최신순)
     *
     * <p>모든 AI 요약을 요약 기간 시작일 기준 내림차순으로 페이징 조회합니다.
     * DRAFT와 PUBLISHED 상태의 요약을 모두 포함합니다.</p>
     *
     * @param pageable 페이징 정보
     * @return AI 요약 페이지
     */
    public Page<AiSummary> getAllSummaries(Pageable pageable) {
        return aiSummaryRepository.findByOrderBySummaryPeriodStartDesc(pageable);
    }

    /**
     * 발행된 AI 요약만 조회
     *
     * <p>PUBLISHED 상태의 AI 요약만 요약 기간 시작일 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param pageable 페이징 정보
     * @return 발행된 AI 요약 페이지
     */
    public Page<AiSummary> getPublishedSummaries(Pageable pageable) {
        return aiSummaryRepository.findByStatusOrderBySummaryPeriodStartDesc(SummaryStatus.PUBLISHED, pageable);
    }

    /**
     * 시간 범위로 AI 요약 조회
     *
     * <p>특정 시간 범위 내에 생성된 AI 요약을 요약 기간 시작일 기준 내림차순으로 조회합니다.</p>
     *
     * @param start 시작 시간
     * @param end 종료 시간
     * @return AI 요약 리스트
     */
    public List<AiSummary> getSummariesByDateRange(LocalDateTime start, LocalDateTime end) {
        return aiSummaryRepository.findBySummaryPeriodStartBetweenOrderBySummaryPeriodStartDesc(start, end);
    }

    /**
     * 가장 최근 발행된 AI 요약 조회
     *
     * <p>PUBLISHED 상태의 가장 최근 AI 요약을 반환합니다.</p>
     *
     * @return 최신 발행된 AI 요약 Optional 객체
     */
    public Optional<AiSummary> getLatestSummary() {
        return aiSummaryRepository.findLatestPublished();
    }

    /**
     * AI 요약 상세 조회
     *
     * <p>AI 요약 ID로 특정 요약의 상세 정보를 조회합니다.</p>
     *
     * @param id AI 요약 ID
     * @return AI 요약 엔티티
     * @throws ResourceNotFoundException AI 요약을 찾을 수 없는 경우
     */
    public AiSummary getSummaryById(Long id) {
        return aiSummaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI 요약을 찾을 수 없습니다. ID: " + id));
    }

    /**
     * AI 요약 생성
     *
     * <p>새로운 AI 요약을 저장합니다. 초기 상태는 DRAFT로 설정됩니다.</p>
     *
     * @param summary AI 요약 엔티티
     * @return 저장된 AI 요약
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
     * AI 요약 발행
     *
     * <p>AI 요약의 상태를 PUBLISHED로 변경하여 발행합니다.</p>
     *
     * @param id AI 요약 ID
     * @return 발행된 AI 요약
     * @throws ResourceNotFoundException AI 요약을 찾을 수 없는 경우
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
     *
     * <p>AI 요약 ID로 특정 요약을 삭제합니다.</p>
     *
     * @param id AI 요약 ID
     * @throws ResourceNotFoundException AI 요약을 찾을 수 없는 경우
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
