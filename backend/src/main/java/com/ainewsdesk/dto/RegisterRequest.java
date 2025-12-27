package com.ainewsdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 데이터 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 6, max = 100, message = "비밀번호는 6자 이상 100자 이하여야 합니다.")
    private String password;

    @NotBlank(message = "사용자명은 필수 입력 항목입니다.")
    @Size(min = 2, max = 100, message = "사용자명은 2자 이상 100자 이하여야 합니다.")
    private String username;
}
