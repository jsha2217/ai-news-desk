package com.ainewsdesk.security;

import com.ainewsdesk.entity.User;
import com.ainewsdesk.exception.UnauthorizedException;
import com.ainewsdesk.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 인증 헬퍼
 * <p>인증 객체에서 사용자 정보 추출</p>
 */
@Component
public class AuthenticationHelper {

    private final UserRepository userRepository;

    public AuthenticationHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 현재 사용자 ID 추출
     */
    public Long getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("인증 정보가 없습니다.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }
}
