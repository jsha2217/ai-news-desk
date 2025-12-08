package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 북마크 정보 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
