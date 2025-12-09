package com.ainewsdesk.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 생성 요청 데이터 객체
 */
public class CreateArticleRequest {

    /**
     * 기사 제목
     */
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
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
    @NotBlank(message = "URL은 필수 입력 항목입니다.")
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String url;

    /**
     * 출처명
     */
    @NotBlank(message = "출처명은 필수 입력 항목입니다.")
    private String sourceName;

    /**
     * 출처 타입 (OFFICIAL, PROFESSIONAL, GENERAL)
     */
    @NotBlank(message = "출처 타입은 필수 입력 항목입니다.")
    private String sourceType;

    /**
     * 우선순위 (1-5, 기본값: 3)
     */
    private Integer priority = 3;

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

    public CreateArticleRequest() {
    }

    public CreateArticleRequest(String title, String description, String content, String url,
                               String sourceName, String sourceType, Integer priority, String category,
                               String thumbnailUrl, LocalDateTime publishedAt) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.sourceName = sourceName;
        this.sourceType = sourceType;
        this.priority = priority;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.publishedAt = publishedAt;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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
}
