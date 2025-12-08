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
     * 모든 AI 요약을 기간 시작일 기준 내림차순으로 페이징 조회
     *
     * @param pageable 페이징 정보
     * @return AI 요약 페이지
     */
    Page<AiSummary> findByOrderBySummaryPeriodStartDesc(Pageable pageable);

    /**
     * 특정 상태의 AI 요약을 기간 시작일 기준 내림차순으로 페이징 조회
     *
     * @param status 요약 상태 (DRAFT, PUBLISHED)
     * @param pageable 페이징 정보
     * @return AI 요약 페이지
     */
    Page<AiSummary> findByStatusOrderBySummaryPeriodStartDesc(SummaryStatus status, Pageable pageable);

    /**
     * 특정 기간 범위 내의 AI 요약을 기간 시작일 기준 내림차순으로 조회
     *
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @return AI 요약 리스트
     */
    List<AiSummary> findBySummaryPeriodStartBetweenOrderBySummaryPeriodStartDesc(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    /**
     * 최신 발행된 AI 요약 조회 (네이티브 쿼리)
     *
     * @return 최신 발행된 AI 요약 Optional 객체
     */
    @Query(value = "SELECT * FROM ai_summaries WHERE status = 'PUBLISHED' ORDER BY summary_period_start DESC LIMIT 1", nativeQuery = true)
    Optional<AiSummary> findLatestPublished();
}
