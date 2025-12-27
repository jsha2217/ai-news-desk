package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 북마크 여부 확인 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkCheckResponse {

    /**
     * 북마크 여부
     */
    private boolean bookmarked;
}
