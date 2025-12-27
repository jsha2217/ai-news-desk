package com.ainewsdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * 사용자 ID
     */
    private Long id;

    /**
     * 사용자 이메일
     */
    private String email;

    /**
     * 사용자명
     */
    private String username;

    /**
     * 이메일 인증 여부
     */
    private Boolean verified;

    /**
     * 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 수정 시간
     */
    private LocalDateTime updatedAt;
}
