package com.ainewsdesk.service;

import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.BookmarkDto;
import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.entity.Bookmark;
import com.ainewsdesk.exception.BadRequestException;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.mapper.ArticleMapper;
import com.ainewsdesk.repository.AiSummaryRepository;
import com.ainewsdesk.repository.ArticleRepository;
import com.ainewsdesk.repository.BookmarkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 북마크 관리 서비스
 * <p>북마크 조회, 추가, 삭제 비즈니스 로직 처리</p>
 */
@Service
@Transactional(readOnly = true)
public class BookmarkService {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkService.class);

    private final BookmarkRepository bookmarkRepository;
    private final ArticleRepository articleRepository;
    private final AiSummaryRepository aiSummaryRepository;
    private final ArticleMapper articleMapper;

    public BookmarkService(BookmarkRepository bookmarkRepository,
                          ArticleRepository articleRepository,
                          AiSummaryRepository aiSummaryRepository,
                          ArticleMapper articleMapper) {
        this.bookmarkRepository = bookmarkRepository;
        this.articleRepository = articleRepository;
        this.aiSummaryRepository = aiSummaryRepository;
        this.articleMapper = articleMapper;
    }

    /**
     * 사용자 북마크 조회 - 최신순 정렬
     */
    public Page<BookmarkDto> getUserBookmarks(Long userId, Pageable pageable) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        List<Bookmark> content = bookmarks.getContent();

        // 배치: 모든 기사 ID와 AI 요약 ID 수집
        List<Long> articleIds = content.stream()
                .filter(b -> b.getBookmarkType() == Bookmark.BookmarkType.ARTICLE && b.getArticleId() != null)
                .map(Bookmark::getArticleId)
                .collect(Collectors.toList());

        List<Long> summaryIds = content.stream()
                .filter(b -> b.getBookmarkType() == Bookmark.BookmarkType.AI_SUMMARY && b.getAiSummaryId() != null)
                .map(Bookmark::getAiSummaryId)
                .collect(Collectors.toList());

        // 배치 조회: 한 번에 모든 기사 로드
        final Map<Long, Article> articlesMap;
        if (!articleIds.isEmpty()) {
            articlesMap = articleRepository.findAllById(articleIds).stream()
                    .collect(Collectors.toMap(Article::getId, article -> article));
        } else {
            articlesMap = new HashMap<>();
        }

        // 배치 조회: 한 번에 모든 AI 요약 로드
        final Map<Long, AiSummary> summariesMap;
        if (!summaryIds.isEmpty()) {
            summariesMap = aiSummaryRepository.findAllById(summaryIds).stream()
                    .collect(Collectors.toMap(AiSummary::getId, summary -> summary));
        } else {
            summariesMap = new HashMap<>();
        }

        // Bookmark -> BookmarkDto 변환 (미리 로드된 데이터 사용)
        List<BookmarkDto> bookmarkDtos = content.stream()
                .map(bookmark -> convertToDto(bookmark, articlesMap, summariesMap))
                .collect(Collectors.toList());

        return new PageImpl<>(bookmarkDtos, pageable, bookmarks.getTotalElements());
    }

    /**
     * 북마크 추가 - 중복 체크
     */
    @Transactional
    public BookmarkDto addBookmark(Long userId, Bookmark.BookmarkType bookmarkType, Long articleId, Long aiSummaryId) {
        // 북마크 타입별 검증 및 처리
        if (bookmarkType == Bookmark.BookmarkType.ARTICLE) {
            if (articleId == null) {
                logger.warn("Bookmark addition failed: article ID is null for user {}", userId);
                throw new BadRequestException("기사 북마크의 경우 기사 ID는 필수입니다.");
            }
            // 이미 북마크된 기사인지 확인
            if (bookmarkRepository.existsByUserIdAndArticleId(userId, articleId)) {
                logger.warn("Bookmark addition failed: article already bookmarked. User ID: {}, Article ID: {}", userId, articleId);
                throw new ConflictException("이미 북마크에 추가된 기사입니다. Article ID: " + articleId);
            }
            // Article 존재 확인
            Article article = articleRepository.findById(articleId)
                    .orElseThrow(() -> {
                        logger.warn("Bookmark addition failed: article not found. ID: {}", articleId);
                        return new ResourceNotFoundException("기사를 찾을 수 없습니다. ID: " + articleId);
                    });
        } else if (bookmarkType == Bookmark.BookmarkType.AI_SUMMARY) {
            if (aiSummaryId == null) {
                logger.warn("Bookmark addition failed: AI summary ID is null for user {}", userId);
                throw new BadRequestException("AI 요약 북마크의 경우 AI 요약 ID는 필수입니다.");
            }
            // 이미 북마크된 AI 요약인지 확인
            if (bookmarkRepository.existsByUserIdAndAiSummaryId(userId, aiSummaryId)) {
                logger.warn("Bookmark addition failed: AI summary already bookmarked. User ID: {}, AI Summary ID: {}", userId, aiSummaryId);
                throw new ConflictException("이미 북마크에 추가된 AI 요약입니다. AI Summary ID: " + aiSummaryId);
            }
            // AiSummary 존재 확인
            AiSummary aiSummary = aiSummaryRepository.findById(aiSummaryId)
                    .orElseThrow(() -> {
                        logger.warn("Bookmark addition failed: AI summary not found. ID: {}", aiSummaryId);
                        return new ResourceNotFoundException("AI 요약을 찾을 수 없습니다. ID: " + aiSummaryId);
                    });
        }

        // Bookmark 엔티티 생성
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setBookmarkType(bookmarkType);
        bookmark.setArticleId(articleId);
        bookmark.setAiSummaryId(aiSummaryId);

        // 저장
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        logger.info("Bookmark added successfully. ID: {}, User ID: {}, Type: {}", savedBookmark.getId(), userId, bookmarkType);

        return convertToDto(savedBookmark);
    }

    /**
     * 북마크 삭제
     */
    @Transactional
    public void removeBookmark(Long userId, Bookmark.BookmarkType bookmarkType, Long articleId, Long aiSummaryId) {
        if (bookmarkType == Bookmark.BookmarkType.ARTICLE && articleId != null) {
            // 북마크 존재 확인
            Bookmark bookmark = bookmarkRepository.findByUserIdAndArticleId(userId, articleId)
                    .orElseThrow(() -> {
                        logger.warn("Bookmark removal failed: bookmark not found. User ID: {}, Article ID: {}", userId, articleId);
                        return new ResourceNotFoundException(
                                "북마크를 찾을 수 없습니다. User ID: " + userId + ", Article ID: " + articleId);
                    });
            // 삭제
            bookmarkRepository.deleteByUserIdAndArticleId(userId, articleId);
            logger.info("Article bookmark removed successfully. User ID: {}, Article ID: {}", userId, articleId);
        } else if (bookmarkType == Bookmark.BookmarkType.AI_SUMMARY && aiSummaryId != null) {
            // 북마크 존재 확인
            Bookmark bookmark = bookmarkRepository.findByUserIdAndAiSummaryId(userId, aiSummaryId)
                    .orElseThrow(() -> {
                        logger.warn("Bookmark removal failed: bookmark not found. User ID: {}, AI Summary ID: {}", userId, aiSummaryId);
                        return new ResourceNotFoundException(
                                "북마크를 찾을 수 없습니다. User ID: " + userId + ", AI Summary ID: " + aiSummaryId);
                    });
            // 삭제
            bookmarkRepository.deleteByUserIdAndAiSummaryId(userId, aiSummaryId);
            logger.info("AI summary bookmark removed successfully. User ID: {}, AI Summary ID: {}", userId, aiSummaryId);
        }
    }

    /**
     * 북마크 여부 확인
     */
    public boolean isBookmarked(Long userId, Bookmark.BookmarkType bookmarkType, Long articleId, Long aiSummaryId) {
        if (bookmarkType == Bookmark.BookmarkType.ARTICLE && articleId != null) {
            return bookmarkRepository.existsByUserIdAndArticleId(userId, articleId);
        } else if (bookmarkType == Bookmark.BookmarkType.AI_SUMMARY && aiSummaryId != null) {
            return bookmarkRepository.existsByUserIdAndAiSummaryId(userId, aiSummaryId);
        }
        return false;
    }

    /**
     * 사용자 북마크 개수 조회
     */
    public long getBookmarkCount(Long userId) {
        return bookmarkRepository.countByUserId(userId);
    }

    /**
     * 배치 북마크 여부 확인
     */
    public Map<Long, Boolean> checkBookmarksBatch(Long userId, Bookmark.BookmarkType bookmarkType, List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, Boolean> result = new HashMap<>();

        if (bookmarkType == Bookmark.BookmarkType.ARTICLE) {
            // 사용자의 모든 기사 북마크 조회
            List<Bookmark> bookmarks = bookmarkRepository.findByUserIdAndArticleIdIn(userId, itemIds);

            // 북마크된 기사 ID 집합 생성
            Map<Long, Boolean> bookmarkedMap = bookmarks.stream()
                    .filter(b -> b.getArticleId() != null)
                    .collect(Collectors.toMap(Bookmark::getArticleId, b -> true));

            // 모든 요청된 ID에 대해 결과 생성
            for (Long itemId : itemIds) {
                result.put(itemId, bookmarkedMap.getOrDefault(itemId, false));
            }
        } else if (bookmarkType == Bookmark.BookmarkType.AI_SUMMARY) {
            // 사용자의 모든 AI 요약 북마크 조회
            List<Bookmark> bookmarks = bookmarkRepository.findByUserIdAndAiSummaryIdIn(userId, itemIds);

            // 북마크된 AI 요약 ID 집합 생성
            Map<Long, Boolean> bookmarkedMap = bookmarks.stream()
                    .filter(b -> b.getAiSummaryId() != null)
                    .collect(Collectors.toMap(Bookmark::getAiSummaryId, b -> true));

            // 모든 요청된 ID에 대해 결과 생성
            for (Long itemId : itemIds) {
                result.put(itemId, bookmarkedMap.getOrDefault(itemId, false));
            }
        }

        logger.debug("Batch bookmark check completed. User ID: {}, Type: {}, Checked {} items",
                userId, bookmarkType, itemIds.size());
        return result;
    }

    /**
     * BookmarkDto 변환 - 배치 조회용
     */
    private BookmarkDto convertToDto(Bookmark bookmark, Map<Long, Article> articlesMap, Map<Long, AiSummary> summariesMap) {
        BookmarkDto dto = new BookmarkDto();
        dto.setId(bookmark.getId());
        dto.setBookmarkType(bookmark.getBookmarkType());
        dto.setArticleId(bookmark.getArticleId());
        dto.setAiSummaryId(bookmark.getAiSummaryId());
        dto.setCreatedAt(bookmark.getCreatedAt());

        // 미리 로드된 데이터에서 조회
        if (bookmark.getBookmarkType() == Bookmark.BookmarkType.ARTICLE && bookmark.getArticleId() != null) {
            Article article = articlesMap.get(bookmark.getArticleId());
            if (article != null) {
                ArticleDto articleDto = articleMapper.toDto(article);
                dto.setArticle(articleDto);
            }
        } else if (bookmark.getBookmarkType() == Bookmark.BookmarkType.AI_SUMMARY && bookmark.getAiSummaryId() != null) {
            AiSummary aiSummary = summariesMap.get(bookmark.getAiSummaryId());
            dto.setAiSummary(aiSummary);
        }

        return dto;
    }

    /**
     * BookmarkDto 변환 - 단일 조회용
     */
    private BookmarkDto convertToDto(Bookmark bookmark) {
        Map<Long, Article> articlesMap = new HashMap<>();
        Map<Long, AiSummary> summariesMap = new HashMap<>();

        // 단일 조회이므로 필요한 데이터만 로드
        if (bookmark.getBookmarkType() == Bookmark.BookmarkType.ARTICLE && bookmark.getArticleId() != null) {
            Article article = articleRepository.findById(bookmark.getArticleId()).orElse(null);
            if (article != null) {
                articlesMap.put(article.getId(), article);
            }
        } else if (bookmark.getBookmarkType() == Bookmark.BookmarkType.AI_SUMMARY && bookmark.getAiSummaryId() != null) {
            AiSummary aiSummary = aiSummaryRepository.findById(bookmark.getAiSummaryId()).orElse(null);
            if (aiSummary != null) {
                summariesMap.put(aiSummary.getId(), aiSummary);
            }
        }

        return convertToDto(bookmark, articlesMap, summariesMap);
    }

}
