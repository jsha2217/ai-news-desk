package com.ainewsdesk.repository;

import com.ainewsdesk.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 특정 사용자의 북마크를 생성일 내림차순으로 페이징 조회
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 북마크 페이지
     */
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * 특정 사용자의 특정 기사 북마크 조회
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     * @return 북마크 Optional 객체
     */
    Optional<Bookmark> findByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 특정 사용자가 특정 기사를 북마크했는지 확인
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     * @return 북마크 존재 여부
     */
    boolean existsByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 특정 사용자의 특정 기사 북마크 삭제
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     */
    void deleteByUserIdAndArticleId(Long userId, Long articleId);

    /**
     * 특정 사용자의 북마크 개수 조회
     *
     * @param userId 사용자 ID
     * @return 북마크 개수
     */
    long countByUserId(Long userId);
}
