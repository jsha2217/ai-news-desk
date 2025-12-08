package com.ainewsdesk.exception;

/**
 * 리소스 중복 등 충돌이 발생했을 때 발생하는 예외
 * HTTP 상태 코드: 409 Conflict
 */
public class ConflictException extends RuntimeException {

    /**
     * 메시지로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인으로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
