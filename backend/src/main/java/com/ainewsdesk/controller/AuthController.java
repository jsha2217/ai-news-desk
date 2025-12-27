package com.ainewsdesk.controller;

import com.ainewsdesk.dto.ChangePasswordRequest;
import com.ainewsdesk.dto.LoginRequest;
import com.ainewsdesk.dto.LoginResponse;
import com.ainewsdesk.dto.PasswordRequest;
import com.ainewsdesk.dto.RegisterRequest;
import com.ainewsdesk.dto.UserDto;
import com.ainewsdesk.entity.User;
import com.ainewsdesk.mapper.UserMapper;
import com.ainewsdesk.security.JwtTokenProvider;
import com.ainewsdesk.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 및 사용자 관리 REST API 컨트롤러
 * <p>회원가입, 로그인, 사용자 정보 조회 등 인증 관련 API 엔드포인트 제공</p>
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "사용자 인증 및 관리 API")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final long jwtExpiration;

    public AuthController(UserService userService,
                         JwtTokenProvider jwtTokenProvider,
                         UserMapper userMapper,
                         @Value("${spring.jwt.expiration}") long jwtExpiration) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * 회원가입 - 새 사용자 등록 및 JWT 토큰 자동 발급
     *
     * @param registerRequest 회원가입 요청 (이메일, 비밀번호, 사용자명)
     * @return LoginResponse (JWT 토큰 및 사용자 정보)
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새 사용자 등록 및 JWT 토큰 자동 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto userDto = userService.registerUser(registerRequest);
        User user = userService.getUserByEmail(userDto.getEmail());
        String token = jwtTokenProvider.generateToken(user);

        LoginResponse loginResponse = new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                jwtExpiration
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(loginResponse);
    }

    /**
     * 로그인 - 이메일/비밀번호 인증 후 JWT 토큰 발급
     *
     * @param loginRequest 로그인 요청 (이메일, 비밀번호)
     * @return LoginResponse (JWT 토큰 및 사용자 정보)
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일/비밀번호 인증 후 JWT 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtTokenProvider.generateToken(user);

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
     * 현재 사용자 정보 조회 - JWT 토큰으로 인증된 사용자 정보 반환
     *
     * @param authentication Spring Security 인증 객체
     * @return UserDto (사용자 정보)
     */
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 정보 조회", description = "JWT 토큰으로 인증된 사용자 정보 반환")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음")
    })
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }

    /**
     * 비밀번호 변경 - 현재 비밀번호 확인 후 새 비밀번호로 변경
     *
     * @param authentication Spring Security 인증 객체
     * @param request 비밀번호 변경 요청
     * @return HTTP 200 OK
     */
    @PutMapping("/password")
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음")
    })
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        User user = getAuthenticatedUser(authentication);
        userService.changePassword(user.getId(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }

    /**
     * 회원 탈퇴 - 비밀번호 확인 후 계정 삭제
     *
     * @param authentication Spring Security 인증 객체
     * @param request 비밀번호 확인 요청
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/account")
    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 계정 삭제")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증되지 않음")
    })
    public ResponseEntity<Void> deleteAccount(
            Authentication authentication,
            @Valid @RequestBody PasswordRequest request) {
        User user = getAuthenticatedUser(authentication);
        userService.deleteAccount(user.getId(), request.getPassword());
        return ResponseEntity.noContent().build();
    }

    /**
     * 인증된 사용자 조회 헬퍼
     */
    private User getAuthenticatedUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }
}
