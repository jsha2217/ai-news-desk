package com.ainewsdesk.dto;

import com.ainewsdesk.entity.Bookmark;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 북마크 생성 요청 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {

    /**
     * 북마크 타입
     */
    @NotNull(message = "북마크 타입은 필수 입력 항목입니다.")
    private Bookmark.BookmarkType bookmarkType;

    /**
     * 기사 ID (bookmarkType이 ARTICLE인 경우 필수)
     */
    private Long articleId;

    /**
     * AI 요약 ID (bookmarkType이 AI_SUMMARY인 경우 필수)
     */
    private Long aiSummaryId;
}
