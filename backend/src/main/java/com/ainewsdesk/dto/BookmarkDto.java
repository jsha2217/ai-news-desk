package com.ainewsdesk.dto;

import java.time.LocalDateTime;

/**
 * 북마크 정보 응답 데이터 객체
 */
public class BookmarkDto {

    /**
     * 북마크 ID
     */
    private Long id;

    /**
     * 기사 ID
     */
    private Long articleId;

    /**
     * 관련 기사 정보
     */
    private ArticleDto article;

    /**
     * 북마크 생성 시간
     */
    private LocalDateTime createdAt;

    public BookmarkDto() {
    }

    public BookmarkDto(Long id, Long articleId, ArticleDto article, LocalDateTime createdAt) {
        this.id = id;
        this.articleId = articleId;
        this.article = article;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public ArticleDto getArticle() {
        return article;
    }

    public void setArticle(ArticleDto article) {
        this.article = article;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
