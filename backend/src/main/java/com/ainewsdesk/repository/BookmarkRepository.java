package com.ainewsdesk.repository;

import com.ainewsdesk.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 사용자별 북마크 조회 - 생성일 내림차순
     */
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 사용자-기사 북마크 조회
     */
    Optional<Bookmark> findByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 사용자-기사 북마크 존재 여부 확인
     */
    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 사용자-기사 북마크 삭제
     */
    void deleteByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 사용자-AI요약 북마크 조회
     */
    Optional<Bookmark> findByUserIdAndAiSummaryId(Long userId, Long aiSummaryId);

    /**
     * 사용자-AI요약 북마크 존재 여부 확인
     */
    boolean existsByUserIdAndAiSummaryId(Long userId, Long aiSummaryId);

    /**
     * 사용자-AI요약 북마크 삭제
     */
    void deleteByUserIdAndAiSummaryId(Long userId, Long aiSummaryId);

    /**
     * 사용자별 북마크 개수 조회
     */
    long countByUserId(Long userId);

    /**
     * 사용자-기사 목록 북마크 조회 (배치)
     */
    List<Bookmark> findByUserIdAndArticleIdIn(Long userId, List<Long> articleIds);

    /**
     * 사용자-AI요약 목록 북마크 조회 (배치)
     */
    List<Bookmark> findByUserIdAndAiSummaryIdIn(Long userId, List<Long> aiSummaryIds);
}
