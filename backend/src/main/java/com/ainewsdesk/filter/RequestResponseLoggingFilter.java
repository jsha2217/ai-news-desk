package com.ainewsdesk.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * HTTP 요청/응답 로깅 필터
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Generate or extract request ID
        String requestId = getRequestId(request);

        // Log incoming request
        logRequest(request, requestId);

        long startTime = System.currentTimeMillis();

        try {
            // Continue with the request
            filterChain.doFilter(request, response);
        } finally {
            // Log outgoing response
            long duration = System.currentTimeMillis() - startTime;
            logResponse(response, requestId, duration);
        }
    }

    /**
     * Request ID 조회 또는 생성
     */
    private String getRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    /**
     * HTTP Request 로그 기록
     */
    private void logRequest(HttpServletRequest request, String requestId) {
        logger.info(
                "Incoming Request [ID: {}] {} {} | User-Agent: {}",
                requestId,
                request.getMethod(),
                request.getRequestURI(),
                request.getHeader("User-Agent")
        );

        // Log query parameters if present
        String queryString = request.getQueryString();
        if (queryString != null && !queryString.isEmpty()) {
            logger.debug("Request [ID: {}] Query Parameters: {}", requestId, queryString);
        }

        // Log content type if present
        String contentType = request.getContentType();
        if (contentType != null) {
            logger.debug("Request [ID: {}] Content-Type: {}", requestId, contentType);
        }
    }

    /**
     * HTTP Response 로그 기록
     */
    private void logResponse(HttpServletResponse response, String requestId, long duration) {
        logger.info(
                "Outgoing Response [ID: {}] Status: {} | Duration: {}ms",
                requestId,
                response.getStatus(),
                duration
        );

        // Log response content type if present
        String contentType = response.getContentType();
        if (contentType != null) {
            logger.debug("Response [ID: {}] Content-Type: {}", requestId, contentType);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Skip logging for health check endpoints to reduce noise
        String path = request.getRequestURI();
        return path.startsWith("/health") ||
               path.startsWith("/actuator") ||
               path.startsWith("/favicon.ico") ||
               path.startsWith("/static/");
    }
}
