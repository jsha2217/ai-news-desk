package com.ainewsdesk.controller;

import com.ainewsdesk.dto.ArticleDetailDto;
import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.CreateArticleRequest;
import com.ainewsdesk.entity.Article.SourceType;
import com.ainewsdesk.exception.BadRequestException;
import com.ainewsdesk.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.*;

/**
 * 뉴스 기사 REST API 컨트롤러
 * <p>뉴스 기사 조회, 생성, 수정, 삭제 API 엔드포인트 제공</p>
 */
@RestController
@RequestMapping("/articles")
@Tag(name = "Articles", description = "뉴스 기사 관리 API")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 전체 기사 목록 조회 - 페이징, 최신순 정렬
     *
     * @param pageable 페이징 정보
     * @return Page<ArticleDto> 기사 목록
     */
    @GetMapping
    @Operation(summary = "모든 기사 조회", description = "전체 기사 목록 페이징 조회, 최신순 정렬")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<ArticleDto>> getAllArticles(
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.debug("Fetching all articles. Page: {}, Size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<ArticleDto> articles = articleService.getAllArticles(pageable);
        logger.debug("Retrieved {} articles from page {}", articles.getContent().size(), pageable.getPageNumber());
        return ResponseEntity.ok(articles);
    }

    /**
     * 키워드 검색 - 제목에 키워드 포함된 기사 검색
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return Page<ArticleDto> 검색 결과
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDto>> searchArticles(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.warn("Article search validation failed: keyword is empty");
            throw new BadRequestException("Search keyword cannot be empty");
        }
        if (keyword.length() > 255) {
            logger.warn("Article search validation failed: keyword length exceeds limit. Length: {}", keyword.length());
            throw new BadRequestException("Search keyword cannot exceed 255 characters");
        }

        logger.debug("Searching articles with keyword: {}", keyword);
        Page<ArticleDto> articles = articleService.searchArticles(keyword, pageable);
        logger.debug("Found {} articles matching keyword: {}", articles.getTotalElements(), keyword);
        return ResponseEntity.ok(articles);
    }

    /**
     * 출처 타입별 조회 - OFFICIAL, PROFESSIONAL, GENERAL
     *
     * @param sourceType 출처 타입
     * @param pageable 페이징 정보
     * @return Page<ArticleDto> 기사 목록
     */
    @GetMapping("/source/{sourceType}")
    public ResponseEntity<Page<ArticleDto>> getArticlesBySourceType(
            @PathVariable SourceType sourceType,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        logger.debug("Fetching articles by source type: {}", sourceType);
        Page<ArticleDto> articles = articleService.getArticlesBySourceType(sourceType, pageable);
        logger.debug("Retrieved {} articles of source type: {}", articles.getTotalElements(), sourceType);
        return ResponseEntity.ok(articles);
    }

    /**
     * 카테고리별 조회
     *
     * @param category 카테고리명
     * @param pageable 페이징 정보
     * @return Page<ArticleDto> 기사 목록
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ArticleDto>> getArticlesByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        if (category == null || category.trim().isEmpty()) {
            logger.warn("Article category fetch validation failed: category is empty");
            throw new BadRequestException("Category cannot be empty");
        }
        if (category.length() > 50) {
            logger.warn("Article category fetch validation failed: category length exceeds limit. Length: {}", category.length());
            throw new BadRequestException("Category cannot exceed 50 characters");
        }

        logger.debug("Fetching articles by category: {}", category);
        Page<ArticleDto> articles = articleService.getArticlesByCategory(category, pageable);
        logger.debug("Retrieved {} articles in category: {}", articles.getTotalElements(), category);
        return ResponseEntity.ok(articles);
    }

    /**
     * 기사 상세 조회 - 본문 포함
     *
     * @param id 기사 ID
     * @return ArticleDetailDto 기사 상세 정보
     */
    @GetMapping("/{id}")
    @Operation(summary = "기사 상세 조회", description = "기사 ID로 상세 정보 조회, 본문 포함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "기사 없음")
    })
    public ResponseEntity<ArticleDetailDto> getArticleById(@PathVariable Long id) {
        logger.debug("Fetching article details. ID: {}", id);
        ArticleDetailDto article = articleService.getArticleById(id);
        logger.debug("Retrieved article details. ID: {}, Title: {}", id, article.getTitle());
        return ResponseEntity.ok(article);
    }

    /**
     * 기사 생성 (크롤러용) - URL 중복 체크, SourceType 검증
     *
     * @param request 기사 생성 요청
     * @return ArticleDto 생성된 기사
     */
    @PostMapping
    @Operation(summary = "기사 생성", description = "새 기사 생성, URL 중복 체크")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "URL 중복")
    })
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        logger.info("Creating new article. Title: {}, Source: {}", request.getTitle(), request.getSourceName());
        ArticleDto article = articleService.saveArticle(request);
        logger.info("Article created successfully. ID: {}, Title: {}", article.getId(), article.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(article);
    }

    /**
     * 기사 수정 - 전체 필드 업데이트
     *
     * @param id 기사 ID
     * @param request 기사 수정 요청
     * @return ArticleDto 수정된 기사
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody CreateArticleRequest request) {
        logger.info("Updating article. ID: {}, Title: {}", id, request.getTitle());
        ArticleDto article = articleService.updateArticle(id, request);
        logger.info("Article updated successfully. ID: {}, Title: {}", article.getId(), article.getTitle());
        return ResponseEntity.ok(article);
    }

    /**
     * 기사 삭제
     *
     * @param id 기사 ID
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        logger.info("Deleting article. ID: {}", id);
        articleService.deleteArticle(id);
        logger.info("Article deleted successfully. ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 오늘 수집된 기사 수 조회
     *
     * @return 오늘 기사 수
     */
    @GetMapping("/count/today")
    public ResponseEntity<Long> getTodayArticleCount() {
        logger.debug("Fetching today article count");
        long count = articleService.getTodayArticleCount();
        logger.debug("Today article count: {}", count);
        return ResponseEntity.ok(count);
    }

    /**
     * 전체 기사 수 조회
     *
     * @return 전체 기사 수
     */
    @GetMapping("/count/total")
    public ResponseEntity<Long> getTotalArticleCount() {
        logger.debug("Fetching total article count");
        long count = articleService.getTotalArticleCount();
        logger.debug("Total article count: {}", count);
        return ResponseEntity.ok(count);
    }
}
