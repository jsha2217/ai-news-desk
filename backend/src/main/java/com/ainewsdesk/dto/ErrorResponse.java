package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 에러 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 에러 발생 시간
     */
    private LocalDateTime timestamp;

    /**
     * HTTP 상태 코드
     */
    private int status;

    /**
     * 에러 타입
     */
    private String error;

    /**
     * 에러 메시지
     */
    private String message;

    /**
     * 요청 경로
     */
    private String path;
}
