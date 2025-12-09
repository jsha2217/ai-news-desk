package com.ainewsdesk.dto;

/**
 * 로그인 응답 데이터 객체
 */
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

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, Long userId, String email, String username, Long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
