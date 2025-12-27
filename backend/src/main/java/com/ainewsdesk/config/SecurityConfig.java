package com.ainewsdesk.config;

import com.ainewsdesk.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 설정 클래스
 * <p>JWT 기반 인증, CORS, 인증 규칙 설정</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 비밀번호 인코더 - BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain - CORS, CSRF, 세션, 인증 규칙, JWT 필터 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                // CORS 설정 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF 비활성화 (JWT 사용으로 불필요)
                .csrf(csrf -> csrf.disable())

                // 세션 관리 정책: STATELESS (JWT 기반 인증)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 인증 규칙 설정
                .authorizeHttpRequests(authz -> authz
                        // Swagger UI 관련 경로는 모두 허용
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()

                        // /auth/** 경로는 모두 허용 (로그인, 회원가입)
                        .requestMatchers("/auth/**").permitAll()

                        // /test/** 경로는 모두 허용 (테스트용)
                        .requestMatchers("/test/**").permitAll()

                        // /articles/** 경로는 GET 요청만 허용 (기사 조회)
                        .requestMatchers("GET", "/articles/**").permitAll()

                        // /ai-summaries/** 경로는 GET 요청만 허용 (AI 요약 조회)
                        .requestMatchers("GET", "/ai-summaries/**").permitAll()

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 등록
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS 설정 - 프론트엔드 API 호출 허용
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처 설정
        // TODO: 운영 환경에서는 실제 도메인으로 변경 필요
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",      // 로컬 개발 환경
                "https://yourdomain.com"      // 운영 환경 (나중에 수정)
        ));

        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));

        // Authorization 헤더 노출 허용
        configuration.addExposedHeader("Authorization");

        // 자격증명(쿠키, 인증 헤더 등) 허용
        configuration.setAllowCredentials(true);

        // 최대 캐시 시간 설정 (초 단위)
        configuration.setMaxAge(3600L);

        // 모든 경로에 대해 CORS 설정 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
