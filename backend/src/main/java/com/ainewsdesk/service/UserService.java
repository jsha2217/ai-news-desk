package com.ainewsdesk.service;

import com.ainewsdesk.dto.RegisterRequest;
import com.ainewsdesk.dto.UserDto;
import com.ainewsdesk.entity.User;
import com.ainewsdesk.exception.ConflictException;
import com.ainewsdesk.exception.ResourceNotFoundException;
import com.ainewsdesk.exception.UnauthorizedException;
import com.ainewsdesk.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관리 서비스
 * 회원가입, 로그인, 사용자 조회 등의 비즈니스 로직을 처리합니다.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 생성자 주입을 통한 의존성 주입
     *
     * @param userRepository 사용자 저장소
     * @param passwordEncoder 비밀번호 암호화 인코더
     */
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자 회원가입
     * 이메일 중복을 확인하고 비밀번호를 암호화하여 새로운 사용자를 등록합니다.
     *
     * @param request 회원가입 요청 데이터
     * @return 생성된 사용자 정보
     * @throws ConflictException 이메일이 이미 존재하는 경우
     */
    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("이미 사용 중인 이메일입니다: " + request.getEmail());
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

        // UserDto로 변환하여 반환
        return convertToDto(savedUser);
    }

    /**
     * 사용자 로그인
     * 이메일과 비밀번호를 검증하고 일치하는 사용자를 반환합니다.
     *
     * @param email 사용자 이메일
     * @param password 비밀번호 (평문)
     * @return 인증된 사용자 엔티티
     * @throws ResourceNotFoundException 사용자를 찾을 수 없는 경우
     * @throws UnauthorizedException 비밀번호가 일치하지 않는 경우
     */
    public User loginUser(String email, String password) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new UnauthorizedException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    /**
     * ID로 사용자 조회
     *
     * @param id 사용자 ID
     * @return 사용자 정보
     * @throws ResourceNotFoundException 사용자를 찾을 수 없는 경우
     */
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다. ID: " + id));

        return convertToDto(user);
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 사용자 이메일
     * @return 사용자 엔티티
     * @throws ResourceNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));
    }

    /**
     * User 엔티티를 UserDto로 변환
     *
     * @param user 사용자 엔티티
     * @return 사용자 DTO
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setVerified(user.isVerified());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
