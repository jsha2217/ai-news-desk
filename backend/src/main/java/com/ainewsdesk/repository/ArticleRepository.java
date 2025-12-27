package com.ainewsdesk.repository;

import com.ainewsdesk.entity.Article;
import com.ainewsdesk.entity.Article.SourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * 전체 기사 조회 - 크롤링 시간 내림차순
     */
    Page<Article> findByOrderByCrawledAtDesc(Pageable pageable);

    /**
     * 출처 유형별 기사 조회 - 크롤링 시간 내림차순
     */
    Page<Article> findBySourceTypeOrderByCrawledAtDesc(SourceType sourceType, Pageable pageable);

    /**
     * 카테고리별 기사 조회 - 크롤링 시간 내림차순
     */
    Page<Article> findByCategoryOrderByCrawledAtDesc(String category, Pageable pageable);

    /**
     * 키워드로 기사 검색 - 크롤링 시간 내림차순
     */
    Page<Article> findByTitleContainingOrderByCrawledAtDesc(String keyword, Pageable pageable);

    /**
     * URL로 기사 조회
     */
    Optional<Article> findByUrl(String url);

    /**
     * 기간별 기사 조회 - 크롤링 시간 내림차순
     */
    Page<Article> findByCrawledAtBetweenOrderByCrawledAtDesc(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    );

    /**
     * 출처 유형별 기사 개수 조회
     */
    long countBySourceType(SourceType sourceType);

    /**
     * 카테고리별 기사 개수 조회
     */
    long countByCategory(String category);

    /**
     * URL 존재 여부 확인
     */
    boolean existsByUrl(String url);

    /**
     * 특정 시간 이후 기사 개수 조회
     */
    long countByCrawledAtAfter(LocalDateTime startDateTime);
}
