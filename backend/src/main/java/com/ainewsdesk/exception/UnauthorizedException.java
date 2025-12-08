package com.ainewsdesk.exception;

/**
 * 인증되지 않았을 때 발생하는 예외
 * HTTP 상태 코드: 401 Unauthorized
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * 메시지로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인으로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
