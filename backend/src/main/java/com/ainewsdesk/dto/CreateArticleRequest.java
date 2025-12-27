package com.ainewsdesk.dto;

import com.ainewsdesk.entity.Article;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 생성 요청 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    @NotNull(message = "출처 타입은 필수 입력 항목입니다.")
    private Article.SourceType sourceType;

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
}
