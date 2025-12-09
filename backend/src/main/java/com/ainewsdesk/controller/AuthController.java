package com.ainewsdesk.controller;

import com.ainewsdesk.dto.LoginRequest;
import com.ainewsdesk.dto.LoginResponse;
import com.ainewsdesk.dto.RegisterRequest;
import com.ainewsdesk.dto.UserDto;
import com.ainewsdesk.entity.User;
import com.ainewsdesk.security.JwtTokenProvider;
import com.ainewsdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 사용자 관리 REST API 컨트롤러
 *
 * <p>회원가입, 로그인, 사용자 정보 조회 등 인증 관련 API 엔드포인트를 제공합니다.</p>
 *
 * @author AI News Desk
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final long jwtExpiration;

    /**
     * AuthController 생성자
     *
     * @param userService 사용자 서비스
     * @param jwtTokenProvider JWT 토큰 제공자
     * @param jwtExpiration JWT 토큰 만료 시간 (밀리초)
     */
    public AuthController(UserService userService,
                         JwtTokenProvider jwtTokenProvider,
                         @Value("${spring.jwt.expiration}") long jwtExpiration) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * 회원가입 API
     *
     * <p>새로운 사용자를 등록합니다. 이메일 중복 검사와 비밀번호 암호화를 수행합니다.</p>
     *
     * @param registerRequest 회원가입 요청 데이터 (이메일, 비밀번호, 사용자명)
     * @return 생성된 사용자 정보 (UserDto)와 HTTP 201 Created 상태 코드
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto userDto = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    /**
     * 로그인 API
     *
     * <p>이메일과 비밀번호로 인증을 수행하고 JWT 토큰을 발급합니다.</p>
     *
     * @param loginRequest 로그인 요청 데이터 (이메일, 비밀번호)
     * @return JWT 토큰 및 사용자 정보를 포함한 LoginResponse와 HTTP 200 OK 상태 코드
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // 사용자 인증 (이메일과 비밀번호 검증)
        User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(user);

        // 로그인 응답 객체 생성
        LoginResponse loginResponse = new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                jwtExpiration
        );

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 현재 인증된 사용자 정보 조회 API
     *
     * <p>JWT 토큰을 통해 인증된 사용자의 정보를 반환합니다.
     * Authorization 헤더에 유효한 JWT 토큰이 필요합니다.</p>
     *
     * @param authentication Spring Security 인증 객체 (JWT 필터에서 자동 주입)
     * @return 현재 인증된 사용자 정보 (UserDto)와 HTTP 200 OK 상태 코드
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        // Authentication 객체에서 이메일 추출
        // JwtAuthenticationFilter에서 UserDetails의 username으로 이메일을 설정했음
        String email = authentication.getName();

        // 이메일로 사용자 엔티티 조회
        User user = userService.getUserByEmail(email);

        // UserDto로 변환
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setUsername(user.getUsername());
        userDto.setVerified(user.isVerified());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());

        return ResponseEntity.ok(userDto);
    }
}
