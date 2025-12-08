package com.ainewsdesk.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * HTTP 상태 코드: 404 Not Found
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 메시지로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인으로 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 예외 원인
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
