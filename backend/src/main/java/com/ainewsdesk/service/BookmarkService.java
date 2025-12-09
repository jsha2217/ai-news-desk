package com.ainewsdesk.service;

import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.BookmarkDto;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.entity.Bookmark;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.repository.ArticleRepository;
import com.ainewsdesk.repository.BookmarkRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 북마크 관리 서비스
 *
 * <p>사용자의 북마크 조회, 추가, 삭제 등의 비즈니스 로직을 처리합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final ArticleRepository articleRepository;

    /**
     * BookmarkService 생성자
     *
     * @param bookmarkRepository 북마크 리포지토리
     * @param articleRepository 기사 리포지토리
     */
    public BookmarkService(BookmarkRepository bookmarkRepository,
                          ArticleRepository articleRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.articleRepository = articleRepository;
    }

    /**
     * 사용자의 모든 북마크 조회 (최신순)
     *
     * <p>특정 사용자의 모든 북마크를 생성 시간 기준 내림차순으로 페이징 조회합니다.
     * 각 북마크에는 관련 기사 정보가 포함됩니다.</p>
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 북마크 목록 페이지 (BookmarkDto)
     */
    public Page<BookmarkDto> getUserBookmarks(Long userId, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        // Bookmark -> BookmarkDto 변환 (기사 정보 포함)
        List<BookmarkDto> bookmarkDtos = bookmarks.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(bookmarkDtos, pageable, bookmarks.getTotalElements());
    }

    /**
     * 북마크 추가
     *
     * <p>사용자가 특정 기사를 북마크에 추가합니다.
     * 이미 북마크된 기사인 경우 예외가 발생합니다.</p>
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     * @return 생성된 북마크 정보 (BookmarkDto)
     * @throws ConflictException 이미 북마크된 기사인 경우
     * @throws ResourceNotFoundException 기사를 찾을 수 없는 경우
     */
    @Transactional
    public BookmarkDto addBookmark(Long userId, Long articleId) {
        // 이미 북마크된 기사인지 확인
        if (bookmarkRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new ConflictException("이미 북마크에 추가된 기사입니다. Article ID: " + articleId);
        }

        // Article 존재 확인
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("기사를 찾을 수 없습니다. ID: " + articleId));

        // Bookmark 엔티티 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setArticleId(articleId);

        // 저장
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);

        return convertToDto(savedBookmark);
    }

    /**
     * 북마크 삭제
     *
     * <p>사용자의 특정 기사 북마크를 삭제합니다.</p>
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     * @throws ResourceNotFoundException 북마크를 찾을 수 없는 경우
     */
    @Transactional
    public void removeBookmark(Long userId, Long articleId) {
        // 북마크 존재 확인
        Bookmark bookmark = bookmarkRepository.findByUserIdAndArticleId(userId, articleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "북마크를 찾을 수 없습니다. User ID: " + userId + ", Article ID: " + articleId));

        // 삭제
        bookmarkRepository.deleteByUserIdAndArticleId(userId, articleId);
    }

    /**
     * 북마크 여부 확인
     *
     * <p>사용자가 특정 기사를 북마크했는지 확인합니다.</p>
     *
     * @param userId 사용자 ID
     * @param articleId 기사 ID
     * @return 북마크 여부 (true: 북마크됨, false: 북마크 안됨)
     */
    public boolean isBookmarked(Long userId, Long articleId) {
        return bookmarkRepository.existsByUserIdAndArticleId(userId, articleId);
    }

    /**
     * 사용자의 총 북마크 개수 조회
     *
     * <p>특정 사용자의 전체 북마크 개수를 반환합니다.</p>
     *
     * @param userId 사용자 ID
     * @return 북마크 개수
     */
    public long getBookmarkCount(Long userId) {
        return bookmarkRepository.countByUserId(userId);
    }

    /**
     * Bookmark 엔티티를 BookmarkDto로 변환
     *
     * <p>북마크 엔티티를 DTO로 변환하며, 관련 기사 정보를 포함합니다.</p>
     *
     * @param bookmark 북마크 엔티티
     * @return BookmarkDto
     */
    private BookmarkDto convertToDto(Bookmark bookmark) {
        BookmarkDto dto = new BookmarkDto();
        dto.setId(bookmark.getId());
        dto.setArticleId(bookmark.getArticleId());
        dto.setCreatedAt(bookmark.getCreatedAt());

        // 기사 정보 조회 및 설정
        try {
            Article article = articleRepository.findById(bookmark.getArticleId()).orElse(null);
            if (article != null) {
                ArticleDto articleDto = convertArticleToDto(article);
                dto.setArticle(articleDto);
            }
        } catch (Exception e) {
            // 기사 조회 실패 시 null로 유지
            System.err.println("Failed to load article for bookmark: " + e.getMessage());
        }

        return dto;
    }

    /**
     * Article 엔티티를 ArticleDto로 변환
     *
     * <p>기사 엔티티를 기사 목록용 DTO로 변환합니다.</p>
     *
     * @param article 기사 엔티티
     * @return ArticleDto
     */
    private ArticleDto convertArticleToDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setUrl(article.getUrl());
        dto.setSourceName(article.getSourceName());
        dto.setSourceType(article.getSourceType() != null ? article.getSourceType().name() : null);
        dto.setCategory(article.getCategory());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCrawledAt(article.getCrawledAt());
        return dto;
    }
}
