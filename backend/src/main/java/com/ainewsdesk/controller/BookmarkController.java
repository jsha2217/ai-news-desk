package com.ainewsdesk.controller;

import com.ainewsdesk.dto.BookmarkCheckResponse;
import com.ainewsdesk.dto.BookmarkDto;
import com.ainewsdesk.dto.BookmarkRequest;
import com.ainewsdesk.exception.BadRequestException;
import com.ainewsdesk.security.AuthenticationHelper;
import com.ainewsdesk.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 북마크 REST API 컨트롤러
 * <p>북마크 조회, 추가, 삭제, 확인 API 엔드포인트 제공</p>
 */
@RestController
@RequestMapping("/bookmarks")
@Tag(name = "Bookmarks", description = "북마크 관리 API")
@SecurityRequirement(name = "Bearer Authentication")
public class BookmarkController {

    private static final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

    private final BookmarkService bookmarkService;
    private final AuthenticationHelper authenticationHelper;

    public BookmarkController(BookmarkService bookmarkService, AuthenticationHelper authenticationHelper) {
        this.bookmarkService = bookmarkService;
        this.authenticationHelper = authenticationHelper;
    }

    /**
     * 사용자 북마크 목록 조회 - 페이징, 최신순 정렬
     *
     * @param authentication 인증 객체
     * @param pageable 페이징 정보
     * @return Page<BookmarkDto> 북마크 목록
     */
    @GetMapping
    @Operation(summary = "사용자 북마크 조회", description = "사용자의 북마크 목록 페이징 조회, 최신순 정렬")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음")
    })
    public ResponseEntity<Page<BookmarkDto>> getUserBookmarks(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        logger.debug("Fetching bookmarks for user. User ID: {}, Page: {}", userId, pageable.getPageNumber());

        Page<BookmarkDto> bookmarks = bookmarkService.getUserBookmarks(userId, pageable);
        logger.debug("Retrieved {} bookmarks for user {}", bookmarks.getContent().size(), userId);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 북마크 추가 - 기사 또는 AI 요약
     *
     * @param authentication 인증 객체
     * @param request 북마크 추가 요청
     * @return BookmarkDto 생성된 북마크
     */
    @PostMapping
    @Operation(summary = "북마크 추가", description = "기사 또는 AI 요약 북마크 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이미 북마크된 항목")
    })
    public ResponseEntity<BookmarkDto> addBookmark(
            Authentication authentication,
            @Valid @RequestBody BookmarkRequest request) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        logger.info("Adding bookmark for user. User ID: {}, Type: {}", userId, request.getBookmarkType());

        BookmarkDto bookmark = bookmarkService.addBookmark(
                userId,
                request.getBookmarkType(),
                request.getArticleId(),
                request.getAiSummaryId()
        );
        logger.info("Bookmark added successfully. Bookmark ID: {}, User ID: {}", bookmark.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
    }

    /**
     * 북마크 삭제
     *
     * @param authentication 인증 객체
     * @param bookmarkType 북마크 타입
     * @param articleId 기사 ID (ARTICLE 타입)
     * @param aiSummaryId AI 요약 ID (AI_SUMMARY 타입)
     * @return HTTP 204 No Content
     */
    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(
            Authentication authentication,
            @RequestParam com.ainewsdesk.entity.Bookmark.BookmarkType bookmarkType,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Long aiSummaryId) {
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        validateBookmarkRequest(bookmarkType, articleId, aiSummaryId);

        logger.info("Removing bookmark for user. User ID: {}, Type: {}", userId, bookmarkType);
        bookmarkService.removeBookmark(userId, bookmarkType, articleId, aiSummaryId);
        logger.info("Bookmark removed successfully. User ID: {}, Type: {}", userId, bookmarkType);
        return ResponseEntity.noContent().build();
    }

    /**
     * 북마크 여부 확인
     *
     * @param authentication 인증 객체
     * @param bookmarkType 북마크 타입
     * @param articleId 기사 ID (ARTICLE 타입)
     * @param aiSummaryId AI 요약 ID (AI_SUMMARY 타입)
     * @return BookmarkCheckResponse 북마크 여부
     */
    @GetMapping("/check")
    public ResponseEntity<BookmarkCheckResponse> checkBookmark(
            Authentication authentication,
            @RequestParam com.ainewsdesk.entity.Bookmark.BookmarkType bookmarkType,
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Long aiSummaryId) {
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        validateBookmarkRequest(bookmarkType, articleId, aiSummaryId);

        logger.debug("Checking bookmark status for user. User ID: {}, Type: {}", userId, bookmarkType);
        boolean isBookmarked = bookmarkService.isBookmarked(userId, bookmarkType, articleId, aiSummaryId);
        logger.debug("Bookmark status checked. User ID: {}, Type: {}, Bookmarked: {}", userId, bookmarkType, isBookmarked);
        return ResponseEntity.ok(new BookmarkCheckResponse(isBookmarked));
    }

    /**
     * 북마크 개수 조회
     *
     * @param authentication 인증 객체
     * @return 북마크 개수
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getBookmarkCount(Authentication authentication) {
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        logger.debug("Fetching bookmark count for user. User ID: {}", userId);

        long count = bookmarkService.getBookmarkCount(userId);
        logger.debug("User bookmark count: {} for User ID: {}", count, userId);
        return ResponseEntity.ok(count);
    }

    /**
     * 배치 북마크 여부 확인
     *
     * @param authentication 인증 객체
     * @param bookmarkType 북마크 타입
     * @param itemIds 확인할 항목 ID 목록 (쉼표로 구분)
     * @return Map<Long, Boolean> 항목 ID별 북마크 여부
     */
    @GetMapping("/check/batch")
    public ResponseEntity<java.util.Map<Long, Boolean>> checkBookmarksBatch(
            Authentication authentication,
            @RequestParam com.ainewsdesk.entity.Bookmark.BookmarkType bookmarkType,
            @RequestParam java.util.List<Long> itemIds) {
        Long userId = authenticationHelper.getCurrentUserId(authentication);
        logger.debug("Batch checking bookmark status for user. User ID: {}, Type: {}, Items: {}",
                userId, bookmarkType, itemIds.size());

        java.util.Map<Long, Boolean> result = bookmarkService.checkBookmarksBatch(userId, bookmarkType, itemIds);
        logger.debug("Batch bookmark check completed. User ID: {}, Results: {}", userId, result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * 북마크 요청 검증 헬퍼
     */
    private void validateBookmarkRequest(
            com.ainewsdesk.entity.Bookmark.BookmarkType bookmarkType,
            Long articleId,
            Long aiSummaryId) {
        if (bookmarkType == com.ainewsdesk.entity.Bookmark.BookmarkType.ARTICLE && articleId == null) {
            logger.warn("Bookmark validation failed: articleId is required for ARTICLE type");
            throw new BadRequestException("Article ID is required for ARTICLE bookmark type");
        }
        if (bookmarkType == com.ainewsdesk.entity.Bookmark.BookmarkType.AI_SUMMARY && aiSummaryId == null) {
            logger.warn("Bookmark validation failed: aiSummaryId is required for AI_SUMMARY type");
            throw new BadRequestException("AI Summary ID is required for AI_SUMMARY bookmark type");
        }
    }
}
