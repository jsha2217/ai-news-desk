package com.ainewsdesk.controller;

import com.ainewsdesk.dto.ArticleDetailDto;
import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.CreateArticleRequest;
import com.ainewsdesk.entity.Article.SourceType;
import com.ainewsdesk.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 뉴스 기사 REST API 컨트롤러
 *
 * <p>뉴스 기사 조회, 생성, 수정, 삭제 등의 API 엔드포인트를 제공합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * ArticleController 생성자
     *
     * @param articleService 기사 서비스
     */
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 모든 기사 조회 API
     *
     * <p>모든 뉴스 기사를 페이징하여 조회합니다. 크롤링 시간 기준 최신순으로 정렬됩니다.</p>
     *
     * @param pageable 페이징 정보 (기본값: 페이지 크기 20, 크롤링 시간 내림차순 정렬)
     * @return 기사 목록 페이지 (ArticleDto)
     */
    @GetMapping
    public ResponseEntity<Page<ArticleDto>> getAllArticles(
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleDto> articles = articleService.getAllArticles(pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * 기사 검색 API
     *
     * <p>제목에 키워드가 포함된 기사를 검색합니다. 크롤링 시간 기준 최신순으로 정렬됩니다.</p>
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보 (기본값: 페이지 크기 20, 크롤링 시간 내림차순 정렬)
     * @return 검색된 기사 목록 페이지 (ArticleDto)
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ArticleDto>> searchArticles(
            @RequestParam String keyword,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleDto> articles = articleService.searchArticles(keyword, pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * 출처 타입별 기사 조회 API
     *
     * <p>특정 출처 타입(OFFICIAL, PROFESSIONAL, GENERAL)의 기사를 조회합니다.
     * 크롤링 시간 기준 최신순으로 정렬됩니다.</p>
     *
     * @param sourceType 출처 타입 (OFFICIAL, PROFESSIONAL, GENERAL)
     * @param pageable 페이징 정보 (기본값: 페이지 크기 20, 크롤링 시간 내림차순 정렬)
     * @return 기사 목록 페이지 (ArticleDto)
     */
    @GetMapping("/source/{sourceType}")
    public ResponseEntity<Page<ArticleDto>> getArticlesBySourceType(
            @PathVariable SourceType sourceType,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesBySourceType(sourceType, pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * 카테고리별 기사 조회 API
     *
     * <p>특정 카테고리의 기사를 조회합니다. 크롤링 시간 기준 최신순으로 정렬됩니다.</p>
     *
     * @param category 카테고리명
     * @param pageable 페이징 정보 (기본값: 페이지 크기 20, 크롤링 시간 내림차순 정렬)
     * @return 기사 목록 페이지 (ArticleDto)
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<ArticleDto>> getArticlesByCategory(
            @PathVariable String category,
            @PageableDefault(size = 20, sort = "crawledAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesByCategory(category, pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * 기사 상세 조회 API
     *
     * <p>기사 ID로 특정 기사의 상세 정보를 조회합니다. 본문 내용을 포함합니다.</p>
     *
     * @param id 기사 ID
     * @return 기사 상세 정보 (ArticleDetailDto)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailDto> getArticleById(@PathVariable Long id) {
        ArticleDetailDto article = articleService.getArticleById(id);
        return ResponseEntity.ok(article);
    }

    /**
     * 기사 생성 API (크롤러용)
     *
     * <p>새로운 뉴스 기사를 생성합니다. URL 중복을 체크하고 SourceType을 검증합니다.
     * 주로 크롤러가 사용하는 엔드포인트입니다.</p>
     *
     * @param request 기사 생성 요청 데이터
     * @return 생성된 기사 정보 (ArticleDto)와 HTTP 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody CreateArticleRequest request) {
        ArticleDto article = articleService.saveArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(article);
    }

    /**
     * 기사 수정 API
     *
     * <p>기존 기사의 정보를 수정합니다. 모든 필드를 업데이트합니다.</p>
     *
     * @param id 기사 ID
     * @param request 기사 수정 요청 데이터
     * @return 수정된 기사 정보 (ArticleDto)
     */
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDto> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody CreateArticleRequest request) {
        ArticleDto article = articleService.updateArticle(id, request);
        return ResponseEntity.ok(article);
    }

    /**
     * 기사 삭제 API
     *
     * <p>기사 ID로 특정 기사를 삭제합니다.</p>
     *
     * @param id 기사 ID
     * @return HTTP 204 No Content 상태 코드
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }
}
