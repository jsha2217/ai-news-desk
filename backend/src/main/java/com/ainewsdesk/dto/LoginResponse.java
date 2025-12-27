package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * JWT 토큰
     */
    private String token;

    /**
     * 토큰 타입 (기본값: "Bearer")
     */
    private String tokenType = "Bearer";

    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 토큰 만료 시간 (밀리초)
     */
    private Long expiresIn;
}
