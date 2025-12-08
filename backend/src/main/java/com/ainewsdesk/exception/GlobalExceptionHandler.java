package com.ainewsdesk.exception;

import com.ainewsdesk.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션에서 발생하는 예외를 처리하고 적절한 HTTP 응답을 반환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * ErrorResponse 생성 헬퍼 메서드
     */
    private ErrorResponse createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(status.value());
        response.setError(status.getReasonPhrase());
        response.setMessage(message);
        response.setPath(path);
        return response;
    }

    /**
     * ResourceNotFoundException 처리
     * HTTP 상태 코드: 404 Not Found
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * BadRequestException 처리
     * HTTP 상태 코드: 400 Bad Request
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * UnauthorizedException 처리
     * HTTP 상태 코드: 401 Unauthorized
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * ConflictException 처리
     * HTTP 상태 코드: 409 Conflict
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * MethodArgumentNotValidException 처리
     * Bean Validation 실패 시 발생
     * HTTP 상태 코드: 400 Bad Request
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        // 필드별 에러 메시지 수집
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * 기타 모든 예외 처리
     * HTTP 상태 코드: 500 Internal Server Error
     *
     * @param ex 예외 객체
     * @param request HTTP 요청 객체
     * @return 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse errorResponse = createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 내부 오류가 발생했습니다.",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
