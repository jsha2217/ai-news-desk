package com.ainewsdesk.service;

import com.ainewsdesk.dto.ArticleDetailDto;
import com.ainewsdesk.dto.ArticleDto;
import com.ainewsdesk.dto.CreateArticleRequest;
import com.ainewsdesk.entity.Article;
import com.ainewsdesk.entity.Article.SourceType;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 관리 서비스
 *
 * <p>뉴스 기사 조회, 검색, 필터링 등의 비즈니스 로직을 처리합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * ArticleService 생성자
     *
     * @param articleRepository 기사 리포지토리
     */
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 모든 기사 조회 (최신순)
     *
     * <p>모든 뉴스 기사를 크롤링 시간 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param pageable 페이징 정보 (페이지 번호, 크기, 정렬 등)
     * @return 기사 목록 페이지 (ArticleDto)
     */
    public Page<ArticleDto> getAllArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findByOrderByCrawledAtDesc(pageable);
        return articles.map(this::convertToDto);
    }

    /**
     * 출처 타입별 기사 조회
     *
     * <p>특정 출처 타입(OFFICIAL, PROFESSIONAL, GENERAL)의 기사를
     * 크롤링 시간 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param sourceType 출처 타입 (OFFICIAL, PROFESSIONAL, GENERAL)
     * @param pageable 페이징 정보
     * @return 기사 목록 페이지 (ArticleDto)
     */
    public Page<ArticleDto> getArticlesBySourceType(SourceType sourceType, Pageable pageable) {
        Page<Article> articles = articleRepository.findBySourceTypeOrderByCrawledAtDesc(sourceType, pageable);
        return articles.map(this::convertToDto);
    }

    /**
     * 카테고리별 기사 조회
     *
     * <p>특정 카테고리의 기사를 크롤링 시간 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param category 카테고리명
     * @param pageable 페이징 정보
     * @return 기사 목록 페이지 (ArticleDto)
     */
    public Page<ArticleDto> getArticlesByCategory(String category, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCategoryOrderByCrawledAtDesc(category, pageable);
        return articles.map(this::convertToDto);
    }

    /**
     * 제목으로 기사 검색
     *
     * <p>제목에 특정 키워드가 포함된 기사를 크롤링 시간 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 기사 목록 페이지 (ArticleDto)
     */
    public Page<ArticleDto> searchArticles(String keyword, Pageable pageable) {
        Page<Article> articles = articleRepository.findByTitleContainingOrderByCrawledAtDesc(keyword, pageable);
        return articles.map(this::convertToDto);
    }

    /**
     * 기사 상세 조회
     *
     * <p>기사 ID로 특정 기사의 상세 정보를 조회합니다. 본문 내용을 포함합니다.</p>
     *
     * @param id 기사 ID
     * @return 기사 상세 정보 (ArticleDetailDto)
     * @throws ResourceNotFoundException 기사를 찾을 수 없는 경우
     */
    public ArticleDetailDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("기사를 찾을 수 없습니다. ID: " + id));

        return convertToDetailDto(article);
    }

    /**
     * 시간 범위로 기사 조회
     *
     * <p>특정 시간 범위 내에 크롤링된 기사를 크롤링 시간 기준 내림차순으로 페이징 조회합니다.</p>
     *
     * @param start 시작 시간
     * @param end 종료 시간
     * @param pageable 페이징 정보
     * @return 기사 목록 페이지 (ArticleDto)
     */
    public Page<ArticleDto> getArticlesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCrawledAtBetweenOrderByCrawledAtDesc(start, end, pageable);
        return articles.map(this::convertToDto);
    }

    /**
     * Article 엔티티를 ArticleDto로 변환
     *
     * <p>기사 엔티티를 기사 목록용 DTO로 변환합니다. 본문 내용은 제외됩니다.</p>
     *
     * @param article 기사 엔티티
     * @return ArticleDto
     */
    private ArticleDto convertToDto(Article article) {
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

    /**
     * Article 엔티티를 ArticleDetailDto로 변환
     *
     * <p>기사 엔티티를 기사 상세 정보용 DTO로 변환합니다. 본문 내용을 포함합니다.</p>
     *
     * @param article 기사 엔티티
     * @return ArticleDetailDto
     */
    private ArticleDetailDto convertToDetailDto(Article article) {
        ArticleDetailDto dto = new ArticleDetailDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setDescription(article.getDescription());
        dto.setContent(article.getContent());
        dto.setUrl(article.getUrl());
        dto.setSourceName(article.getSourceName());
        dto.setSourceType(article.getSourceType() != null ? article.getSourceType().name() : null);
        dto.setCategory(article.getCategory());
        dto.setThumbnailUrl(article.getThumbnailUrl());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setCrawledAt(article.getCrawledAt());
        return dto;
    }

    // ==================== Part 2: 기사 생성, 수정, 삭제, 통계 메서드 ====================

    /**
     * 기사 생성
     *
     * <p>새로운 뉴스 기사를 생성합니다. URL 중복을 체크하고,
     * SourceType Enum을 변환하여 저장합니다.</p>
     *
     * @param request 기사 생성 요청 데이터
     * @return 생성된 기사 정보 (ArticleDto)
     * @throws ConflictException URL이 이미 존재하는 경우
     */
    @Transactional
    public ArticleDto saveArticle(CreateArticleRequest request) {
        // URL 중복 체크
        if (articleRepository.existsByUrl(request.getUrl())) {
            throw new ConflictException("이미 존재하는 URL입니다: " + request.getUrl());
        }

        // SourceType Enum 변환
        SourceType sourceType;
        try {
            sourceType = SourceType.valueOf(request.getSourceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 출처 타입입니다: " + request.getSourceType() +
                    ". 사용 가능한 값: OFFICIAL, PROFESSIONAL, GENERAL");
        }

        // Article 엔티티 생성
        Article article = new Article();
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setContent(request.getContent());
        article.setUrl(request.getUrl());
        article.setSourceName(request.getSourceName());
        article.setSourceType(sourceType);
        article.setPriority(request.getPriority() != null ? request.getPriority() : 3);
        article.setCategory(request.getCategory());
        article.setThumbnailUrl(request.getThumbnailUrl());
        article.setPublishedAt(request.getPublishedAt());

        // 저장
        Article savedArticle = articleRepository.save(article);

        return convertToDto(savedArticle);
    }

    /**
     * 기사 수정
     *
     * <p>기존 기사의 정보를 수정합니다. 모든 필드를 업데이트합니다.</p>
     *
     * @param id 기사 ID
     * @param request 기사 수정 요청 데이터
     * @return 수정된 기사 정보 (ArticleDto)
     * @throws ResourceNotFoundException 기사를 찾을 수 없는 경우
     */
    @Transactional
    public ArticleDto updateArticle(Long id, CreateArticleRequest request) {
        // 기사 조회
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("기사를 찾을 수 없습니다. ID: " + id));

        // SourceType Enum 변환
        SourceType sourceType;
        try {
            sourceType = SourceType.valueOf(request.getSourceType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 출처 타입입니다: " + request.getSourceType() +
                    ". 사용 가능한 값: OFFICIAL, PROFESSIONAL, GENERAL");
        }

        // 필드 업데이트
        article.setTitle(request.getTitle());
        article.setDescription(request.getDescription());
        article.setContent(request.getContent());
        article.setUrl(request.getUrl());
        article.setSourceName(request.getSourceName());
        article.setSourceType(sourceType);
        article.setPriority(request.getPriority() != null ? request.getPriority() : 3);
        article.setCategory(request.getCategory());
        article.setThumbnailUrl(request.getThumbnailUrl());
        article.setPublishedAt(request.getPublishedAt());

        // 저장 (JPA dirty checking에 의해 자동 업데이트)
        Article updatedArticle = articleRepository.save(article);

        return convertToDto(updatedArticle);
    }

    /**
     * 기사 삭제
     *
     * <p>기사 ID로 특정 기사를 삭제합니다.</p>
     *
     * @param id 기사 ID
     * @throws ResourceNotFoundException 기사를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteArticle(Long id) {
        // 기사 존재 여부 확인
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("기사를 찾을 수 없습니다. ID: " + id);
        }

        // 삭제
        articleRepository.deleteById(id);
    }

    /**
     * 출처 타입별 기사 개수 조회
     *
     * <p>특정 출처 타입(OFFICIAL, PROFESSIONAL, GENERAL)의 기사 개수를 반환합니다.</p>
     *
     * @param sourceType 출처 타입 (OFFICIAL, PROFESSIONAL, GENERAL)
     * @return 기사 개수
     */
    public long countArticlesBySourceType(SourceType sourceType) {
        return articleRepository.countBySourceType(sourceType);
    }

    /**
     * 카테고리별 기사 개수 조회
     *
     * <p>특정 카테고리의 기사 개수를 반환합니다.</p>
     *
     * @param category 카테고리명
     * @return 기사 개수
     */
    public long countArticlesByCategory(String category) {
        return articleRepository.countByCategory(category);
    }
}
