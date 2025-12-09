package com.ainewsdesk.dto;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 상세 정보 응답 데이터 객체
 */
public class ArticleDetailDto {

    /**
     * 기사 ID
     */
    private Long id;

    /**
     * 기사 제목
     */
    private String title;

    /**
     * 기사 요약
     */
    private String description;

    /**
     * 기사 본문
     */
    private String content;

    /**
     * 기사 URL
     */
    private String url;

    /**
     * 출처명
     */
    private String sourceName;

    /**
     * 출처 타입 (OFFICIAL, PROFESSIONAL, GENERAL)
     */
    private String sourceType;

    /**
     * 카테고리
     */
    private String category;

    /**
     * 썸네일 이미지 URL
     */
    private String thumbnailUrl;

    /**
     * 기사 발행 시간
     */
    private LocalDateTime publishedAt;

    /**
     * 크롤링 시간
     */
    private LocalDateTime crawledAt;

    public ArticleDetailDto() {
    }

    public ArticleDetailDto(Long id, String title, String description, String content, String url,
                           String sourceName, String sourceType, String category, String thumbnailUrl,
                           LocalDateTime publishedAt, LocalDateTime crawledAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
        this.crawledAt = crawledAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCrawledAt() {
        return crawledAt;
    }

    public void setCrawledAt(LocalDateTime crawledAt) {
        this.crawledAt = crawledAt;
    }
}
