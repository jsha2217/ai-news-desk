package com.ainewsdesk.security;

import com.ainewsdesk.entity.User;
import com.ainewsdesk.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 *
 * <p>모든 HTTP 요청에 대해 JWT 토큰을 검증하고 Spring Security 컨텍스트에 인증 정보를 설정합니다.
 * OncePerRequestFilter를 확장하여 요청당 한 번만 실행되도록 보장합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * JwtAuthenticationFilter 생성자
     *
     * @param jwtTokenProvider JWT 토큰 제공자
     * @param userRepository 사용자 리포지토리
     */
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    /**
     * 필터의 주요 로직을 구현하는 메서드
     *
     * <p>HTTP 요청에서 JWT 토큰을 추출하고 검증한 후, 유효한 경우 Spring Security 컨텍스트에
     * 인증 정보를 설정합니다. 예외가 발생하더라도 필터 체인을 계속 진행합니다.</p>
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException 입출력 예외
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
            // 예외 발생 시 로그 출력 (실제 운영 환경에서는 로거 사용 권장)
            System.err.println("Could not set user authentication in security context: " + ex.getMessage());
        }

        // 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청에서 JWT 토큰을 추출하는 메서드
     *
     * <p>Authorization 헤더에서 "Bearer " 접두사를 제거하고 순수 JWT 토큰을 반환합니다.</p>
     *
     * @param request HTTP 요청
     * @return JWT 토큰 문자열, 없으면 null
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
