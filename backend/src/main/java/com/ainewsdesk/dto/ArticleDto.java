package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 정보 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDto {

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
}
