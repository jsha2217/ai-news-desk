package com.ainewsdesk.service;

import com.ainewsdesk.dto.ArticleDetailDto;
import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.CreateArticleRequest;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.entity.Article.SourceType;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.mapper.ArticleMapper;
import com.ainewsdesk.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 관리 서비스
 * <p>기사 조회, 검색, 생성, 수정, 삭제 비즈니스 로직 처리</p>
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    public ArticleService(ArticleRepository articleRepository, ArticleMapper articleMapper) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
    }

    /**
     * 전체 기사 조회 - 최신순 정렬
     */
    public Page<ArticleDto> getAllArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findByOrderByCrawledAtDesc(pageable);
        return articles.map(articleMapper::toDto);
    }

    /**
     * 출처 타입별 기사 조회
     */
    public Page<ArticleDto> getArticlesBySourceType(SourceType sourceType, Pageable pageable) {
        Page<Article> articles = articleRepository.findBySourceTypeOrderByCrawledAtDesc(sourceType, pageable);
        return articles.map(articleMapper::toDto);
    }

    /**
     * 카테고리별 기사 조회
     */
    public Page<ArticleDto> getArticlesByCategory(String category, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCategoryOrderByCrawledAtDesc(category, pageable);
        return articles.map(articleMapper::toDto);
    }

    /**
     * 제목 키워드 검색
     */
    public Page<ArticleDto> searchArticles(String keyword, Pageable pageable) {
        Page<Article> articles = articleRepository.findByTitleContainingOrderByCrawledAtDesc(keyword, pageable);
        return articles.map(articleMapper::toDto);
    }

    /**
     * 기사 상세 조회 - 본문 포함
     */
    public ArticleDetailDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Article not found for ID: {}. Throwing ResourceNotFoundException.", id);
                    return new ResourceNotFoundException("Article with ID " + id + " does not exist in the database. Please verify the article ID and try again.");
                });

        return articleMapper.toDetailDto(article);
    }

    /**
     * 시간 범위별 기사 조회
     */
    public Page<ArticleDto> getArticlesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCrawledAtBetweenOrderByCrawledAtDesc(start, end, pageable);
        return articles.map(articleMapper::toDto);
    }

    /**
     * 기사 생성 - URL 중복 체크
     */
    @Transactional
    public ArticleDto saveArticle(CreateArticleRequest request) {
        // URL 중복 체크
        if (articleRepository.existsByUrl(request.getUrl())) {
            logger.warn("Attempted to save article with duplicate URL: {}", request.getUrl());
            throw new ConflictException("Article with URL '" + request.getUrl() + "' already exists in the database. Please provide a unique URL.");
        }

        // Article 엔티티 생성
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setContent(request.getContent());
        article.setUrl(request.getUrl());
        article.setSourceName(request.getSourceName());
        article.setSourceType(request.getSourceType());
        article.setPriority(request.getPriority() != null ? request.getPriority() : 3);
        article.setCategory(request.getCategory());
        article.setThumbnailUrl(request.getThumbnailUrl());
        article.setPublishedAt(request.getPublishedAt());

        // 저장
        Article savedArticle = articleRepository.save(article);
        logger.info("Article saved successfully. ID: {}, Title: {}", savedArticle.getId(), savedArticle.getTitle());

        return articleMapper.toDto(savedArticle);
    }

    /**
     * 기사 수정 - 전체 필드 업데이트
     */
    @Transactional
    public ArticleDto updateArticle(Long id, CreateArticleRequest request) {
        // 기사 조회
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Article not found for update. ID: {}", id);
                    return new ResourceNotFoundException("Article with ID " + id + " not found for update. Please verify the article ID exists before attempting to update.");
                });

        // 필드 업데이트
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setContent(request.getContent());
        article.setUrl(request.getUrl());
        article.setSourceName(request.getSourceName());
        article.setSourceType(request.getSourceType());
        article.setPriority(request.getPriority() != null ? request.getPriority() : 3);
        article.setCategory(request.getCategory());
        article.setThumbnailUrl(request.getThumbnailUrl());
        article.setPublishedAt(request.getPublishedAt());

        // 저장 (JPA dirty checking에 의해 자동 업데이트)
        Article updatedArticle = articleRepository.save(article);
        logger.info("Article updated successfully. ID: {}, Title: {}", updatedArticle.getId(), updatedArticle.getTitle());

        return articleMapper.toDto(updatedArticle);
    }

    /**
     * 기사 삭제
     */
    @Transactional
    public void deleteArticle(Long id) {
        // 기사 존재 여부 확인
        if (!articleRepository.existsById(id)) {
            logger.warn("Article not found for deletion. ID: {}", id);
            throw new ResourceNotFoundException("Article with ID " + id + " not found for deletion. The article may have already been deleted or the ID may be invalid.");
        }

        // 삭제
        articleRepository.deleteById(id);
        logger.info("Article deleted successfully. ID: {}", id);
    }

    /**
     * 출처 타입별 기사 개수 조회
     */
    public long countArticlesBySourceType(SourceType sourceType) {
        return articleRepository.countBySourceType(sourceType);
    }

    /**
     * 카테고리별 기사 개수 조회
     */
    public long countArticlesByCategory(String category) {
        return articleRepository.countByCategory(category);
    }

    /**
     * 오늘 수집된 기사 개수 조회
     */
    public long getTodayArticleCount() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        return articleRepository.countByCrawledAtAfter(startOfDay);
    }

    /**
     * 전체 기사 개수 조회
     */
    public long getTotalArticleCount() {
        return articleRepository.count();
    }
}
