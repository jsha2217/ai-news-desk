package com.ainewsdesk.exception;

/**
 * 잘못된 요청일 때 발생하는 예외
 * HTTP 상태 코드: 400 Bad Request
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
