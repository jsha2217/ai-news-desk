package com.ainewsdesk.exception;

/**
 * 인증되지 않았을 때 발생하는 예외
 * HTTP 상태 코드: 401 Unauthorized
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
