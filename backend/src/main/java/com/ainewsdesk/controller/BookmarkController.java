package com.ainewsdesk.controller;

import com.ainewsdesk.dto.BookmarkCheckResponse;
import com.ainewsdesk.dto.BookmarkDto;
import com.ainewsdesk.dto.BookmarkRequest;
import com.ainewsdesk.entity.User;
import com.ainewsdesk.service.BookmarkService;
import com.ainewsdesk.service.UserService;
import jakarta.validation.Valid;
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
 *
 * <p>사용자의 북마크 조회, 추가, 삭제, 확인 등의 API 엔드포인트를 제공합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    /**
     * BookmarkController 생성자
     *
     * @param bookmarkService 북마크 서비스
     * @param userService 사용자 서비스
     */
    public BookmarkController(BookmarkService bookmarkService, UserService userService) {
        this.bookmarkService = bookmarkService;
        this.userService = userService;
    }

    /**
     * 현재 사용자의 북마크 조회 API
     *
     * <p>인증된 사용자의 모든 북마크를 페이징하여 조회합니다.
     * 생성 시간 기준 최신순으로 정렬되며, 각 북마크에는 관련 기사 정보가 포함됩니다.</p>
     *
     * @param authentication Spring Security 인증 객체 (JWT 필터에서 자동 주입)
     * @param pageable 페이징 정보 (기본값: 페이지 크기 20, 생성 시간 내림차순 정렬)
     * @return 북마크 목록 페이지 (BookmarkDto)
     */
    @GetMapping
    public ResponseEntity<Page<BookmarkDto>> getUserBookmarks(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = getUserIdFromAuthentication(authentication);

        Page<BookmarkDto> bookmarks = bookmarkService.getUserBookmarks(userId, pageable);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 북마크 추가 API
     *
     * <p>인증된 사용자가 특정 기사를 북마크에 추가합니다.
     * 이미 북마크된 기사인 경우 ConflictException이 발생합니다.</p>
     *
     * @param authentication Spring Security 인증 객체 (JWT 필터에서 자동 주입)
     * @param request 북마크 추가 요청 데이터 (articleId)
     * @return 생성된 북마크 정보 (BookmarkDto)와 HTTP 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<BookmarkDto> addBookmark(
            Authentication authentication,
            @Valid @RequestBody BookmarkRequest request) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = getUserIdFromAuthentication(authentication);

        BookmarkDto bookmark = bookmarkService.addBookmark(userId, request.getArticleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(bookmark);
    }

    /**
     * 북마크 삭제 API
     *
     * <p>인증된 사용자의 특정 기사 북마크를 삭제합니다.</p>
     *
     * @param authentication Spring Security 인증 객체 (JWT 필터에서 자동 주입)
     * @param articleId 기사 ID
     * @return HTTP 204 No Content 상태 코드
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> removeBookmark(
            Authentication authentication,
            @PathVariable Long articleId) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = getUserIdFromAuthentication(authentication);

        bookmarkService.removeBookmark(userId, articleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 북마크 여부 확인 API
     *
     * <p>인증된 사용자가 특정 기사를 북마크했는지 확인합니다.</p>
     *
     * @param authentication Spring Security 인증 객체 (JWT 필터에서 자동 주입)
     * @param articleId 기사 ID
     * @return 북마크 여부를 포함한 응답 객체 { bookmarked: true/false }
     */
    @GetMapping("/check/{articleId}")
    public ResponseEntity<BookmarkCheckResponse> checkBookmark(
            Authentication authentication,
            @PathVariable Long articleId) {
        // 현재 인증된 사용자 정보 가져오기
        Long userId = getUserIdFromAuthentication(authentication);

        boolean isBookmarked = bookmarkService.isBookmarked(userId, articleId);
        BookmarkCheckResponse response = new BookmarkCheckResponse(isBookmarked);
        return ResponseEntity.ok(response);
    }

    /**
     * Authentication 객체에서 사용자 ID 추출
     *
     * <p>JWT 필터에서 설정한 Authentication 객체로부터 이메일을 추출하고,
     * 해당 이메일로 사용자 정보를 조회하여 사용자 ID를 반환합니다.</p>
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자 ID
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // JwtAuthenticationFilter에서 UserDetails의 username으로 이메일을 설정했음
        String email = authentication.getName();

        // 이메일로 사용자 정보 조회
        User user = userService.getUserByEmail(email);

        return user.getId();
    }
}
