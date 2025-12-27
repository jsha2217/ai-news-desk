package com.ainewsdesk.security;

import com.ainewsdesk.entity.User;
import com.ainewsdesk.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * JWT 인증 필터
 * <p>HTTP 요청 JWT 토큰 검증 및 Security 컨텍스트 인증 정보 설정</p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * JWT 토큰 추출/검증/인증 정보 설정
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // HTTP 헤더에서 JWT 토큰 추출
            String jwt = getJwtFromRequest(request);

            // 토큰이 존재하고 유효한 경우
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                // 토큰에서 사용자 ID 추출
                Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

                // 사용자 정보 조회
                if (userId != null) {
                    Optional<User> userOptional = userRepository.findById(userId);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();

                        // UserDetails 객체 생성 (Spring Security용)
                        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                                user.getEmail(),
                                user.getPasswordHash(),
                                new ArrayList<>() // 권한 목록 (현재는 빈 리스트)
                        );

                        // UsernamePasswordAuthenticationToken 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        // 요청 세부 정보 설정
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // SecurityContextHolder에 인증 정보 설정
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context: {}", ex.getMessage());
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰 추출 - Bearer 접두사 제거
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        // Authorization 헤더 추출
        String bearerToken = request.getHeader("Authorization");

        // "Bearer "로 시작하는 경우 접두사 제거
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후의 토큰 반환
        }

        return null;
    }
}
