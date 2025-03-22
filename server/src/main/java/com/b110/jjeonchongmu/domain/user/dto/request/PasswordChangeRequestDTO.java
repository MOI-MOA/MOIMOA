package com.b110.jjeonchongmu.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordChangeRequestDTO {

    @NotBlank(message = "현재 비밀번호는 필수 입력값입니다")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다")
    private String newPassword;
} 