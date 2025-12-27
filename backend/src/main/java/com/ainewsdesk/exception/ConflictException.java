package com.ainewsdesk.exception;

/**
 * 리소스 중복 등 충돌이 발생했을 때 발생하는 예외
 * HTTP 상태 코드: 409 Conflict
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
