package com.ainewsdesk.dto;

/**
 * 북마크 여부 확인 응답 데이터 객체
 */
public class BookmarkCheckResponse {

    /**
     * 북마크 여부
     */
    private boolean bookmarked;

    public BookmarkCheckResponse() {
    }

    public BookmarkCheckResponse(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }
}
