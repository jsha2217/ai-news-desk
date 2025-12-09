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
     * 모든 기사를 크롤링 시간 내림차순으로 페이징 조회
     *
     * @param pageable 페이징 정보
     * @return 기사 페이지
     */
    Page<Article> findByOrderByCrawledAtDesc(Pageable pageable);

    /**
     * 특정 출처 유형의 기사를 크롤링 시간 내림차순으로 페이징 조회
     *
     * @param sourceType 출처 유형 (OFFICIAL, PROFESSIONAL, GENERAL)
     * @param pageable 페이징 정보
     * @return 기사 페이지
     */
    Page<Article> findBySourceTypeOrderByCrawledAtDesc(SourceType sourceType, Pageable pageable);

    /**
     * 특정 카테고리의 기사를 크롤링 시간 내림차순으로 페이징 조회
     *
     * @param category 카테고리명
     * @param pageable 페이징 정보
     * @return 기사 페이지
     */
    Page<Article> findByCategoryOrderByCrawledAtDesc(String category, Pageable pageable);

    /**
     * 제목에 특정 키워드가 포함된 기사를 크롤링 시간 내림차순으로 페이징 조회
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 기사 페이지
     */
    Page<Article> findByTitleContainingOrderByCrawledAtDesc(String keyword, Pageable pageable);

    /**
     * URL로 기사 조회 (중복 체크용)
     *
     * @param url 기사 URL
     * @return 기사 Optional 객체
     */
    Optional<Article> findByUrl(String url);

    /**
     * 특정 기간 내에 크롤링된 기사를 크롤링 시간 내림차순으로 페이징 조회
     *
     * @param startDateTime 시작 시간
     * @param endDateTime 종료 시간
     * @param pageable 페이징 정보
     * @return 기사 페이지
     */
    Page<Article> findByCrawledAtBetweenOrderByCrawledAtDesc(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable
    );

    /**
     * 특정 출처 유형의 기사 개수 조회
     *
     * @param sourceType 출처 유형 (OFFICIAL, PROFESSIONAL, GENERAL)
     * @return 기사 개수
     */
    long countBySourceType(SourceType sourceType);

    /**
     * 특정 카테고리의 기사 개수 조회
     *
     * @param category 카테고리명
     * @return 기사 개수
     */
    long countByCategory(String category);

    /**
     * URL 존재 여부 확인 (중복 체크용)
     *
     * @param url 기사 URL
     * @return 존재 여부
     */
    boolean existsByUrl(String url);
}
