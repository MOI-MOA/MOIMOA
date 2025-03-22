package com.b110.jjeonchongmu.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignupRequestDTO {

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String userEmail;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    private String password;

    @Past(message = "생년월일은 과거 날짜여야 합니다")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
} 