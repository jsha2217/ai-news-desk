package com.ainewsdesk.dto;

import com.ainewsdesk.entity.AiSummary;
import com.ainewsdesk.entity.Bookmark;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 북마크 정보 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {

    /**
     * 북마크 ID
     */
    private Long id;

    /**
     * 북마크 타입 (ARTICLE, AI_SUMMARY)
     */
    private Bookmark.BookmarkType bookmarkType;

    /**
     * 기사 ID
     */
    private Long articleId;

    /**
     * AI 요약 ID
     */
    private Long aiSummaryId;

    /**
     * 관련 기사 정보
     */
    private ArticleDto article;

    /**
     * 관련 AI 요약 정보
     */
    private AiSummary aiSummary;

    /**
     * 북마크 생성 시간
     */
    private LocalDateTime createdAt;
}
