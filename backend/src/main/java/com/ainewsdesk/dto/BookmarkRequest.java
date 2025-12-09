package com.ainewsdesk.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 북마크 생성 요청 데이터 객체
 */
public class BookmarkRequest {

    /**
     * 기사 ID
     */
    @NotNull(message = "기사 ID는 필수 입력 항목입니다.")
    private Long articleId;

    public BookmarkRequest() {
    }

    public BookmarkRequest(Long articleId) {
        this.articleId = articleId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
