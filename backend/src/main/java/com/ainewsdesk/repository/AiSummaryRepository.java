package com.ainewsdesk.repository;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.entity.AiSummary.SummaryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AiSummaryRepository extends JpaRepository<AiSummary, Long> {

    /**
     * AI 요약 전체 조회 - 기간 시작일 내림차순
     */
    Page<AiSummary> findByOrderBySummaryPeriodStartDesc(Pageable pageable);

    /**
     * 상태별 AI 요약 조회 - 기간 시작일 내림차순
     */
    Page<AiSummary> findByStatusOrderBySummaryPeriodStartDesc(SummaryStatus status, Pageable pageable);

    /**
     * 기간 범위 내 AI 요약 조회
     */
    List<AiSummary> findBySummaryPeriodStartBetweenOrderBySummaryPeriodStartDesc(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 최신 발행 AI 요약 조회
     */
    @Query(value = "SELECT * FROM ai_summaries WHERE status = 'PUBLISHED' ORDER BY summary_period_start DESC LIMIT 1", nativeQuery = true)
    Optional<AiSummary> findLatestPublished();
}
