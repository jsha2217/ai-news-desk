package com.ainewsdesk.mapper;

import com.ainewsdesk.dto.UserDto;
import com.ainewsdesk.entity.User;
import org.springframework.stereotype.Component;

/**
 * User 엔티티 ↔ DTO 변환
 */
@Component
public class UserMapper {

    /**
     * User → UserDto 변환
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

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
