package com.ainewsdesk.service;

import com.ainewsdesk.dto.RegisterRequest;
import com.ainewsdesk.dto.UserDto;
import com.ainewsdesk.entity.User;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.exception.UnauthorizedException;
import com.ainewsdesk.mapper.UserMapper;
import com.ainewsdesk.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관리 서비스
 * <p>회원가입, 로그인, 사용자 조회 비즈니스 로직 처리</p>
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    /**
     * 회원가입 - 이메일 중복 체크, 비밀번호 암호화
     */
    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Attempted registration with duplicate email: {}", request.getEmail());
            throw new ConflictException("이메일 '" + request.getEmail() + "'은(는) 이미 등록된 이메일입니다. 다른 이메일을 사용하거나 로그인을 시도해주세요.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(encodedPassword);
        user.setUsername(request.getUsername());
        user.setVerified(false);

        // 사용자 저장
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

        // UserDto로 변환하여 반환
        return userMapper.toDto(savedUser);
    }

    /**
     * 로그인 - 이메일/비밀번호 검증
     */
    public User loginUser(String email, String password) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Login attempted with non-existent email: {}", email);
                    return new UnauthorizedException("Login failed: Email '" + email + "' not found. Please verify your email address or create a new account.");
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Login attempted with incorrect password for email: {}", email);
            throw new UnauthorizedException("Login failed: Invalid password for email '" + email + "'. Please check your password and try again.");
        }

        logger.info("User logged in successfully. Email: {}", email);
        return user;
    }

    /**
     * ID로 사용자 조회
     */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + id + " not found. The user may not exist or may have been deleted."));

        return userMapper.toDto(user);
    }

    /**
     * 이메일로 사용자 조회
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email '" + email + "' not found. Please verify the email address."));
    }

    /**
     * 비밀번호 변경 - 현재 비밀번호 검증 후 변경
     */
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Password change attempted for non-existent user. ID: {}", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found. Cannot change password for non-existent user.");
                });

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            logger.warn("Password change attempted with incorrect current password. User ID: {}", userId);
            throw new UnauthorizedException("Password change failed: Current password is incorrect. Please verify your current password and try again.");
        }

        // 새 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedPassword);
        userRepository.save(user);
        logger.info("Password changed successfully. User ID: {}", userId);
    }

    /**
     * 회원 탈퇴 - 비밀번호 확인 후 삭제
     */
    @Transactional
    public void deleteAccount(Long userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Account deletion attempted for non-existent user. ID: {}", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found. Cannot delete non-existent account.");
                });

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            logger.warn("Account deletion attempted with incorrect password. User ID: {}", userId);
            throw new UnauthorizedException("회원 탈퇴 실패: 비밀번호가 일치하지 않습니다. 비밀번호를 확인하고 다시 시도해주세요.");
        }

        // 사용자 삭제
        userRepository.delete(user);
        logger.info("Account deleted successfully. User ID: {}, Email: {}", userId, user.getEmail());
    }

}
