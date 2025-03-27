package com.b110.jjeonchongmu.domain.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignupRequestDTO {
    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "이름은 필수 입력값입니다")
    @Size(min = 2, max = 20, message = "이름은 2자 이상 20자 이하로 입력해주세요")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{4,20}$",
            message = "비밀번호는 4~20자리수여야 합니다. 영문 대소문자, 숫자, 특수문자를 1개 이상 포함해야 합니다.")
    private String password;

    @NotNull(message = "생년월일은 필수 입력값입니다")
    @Past(message = "생년월일은 과거 날짜여야 합니다")
    private LocalDate birth;

    @NotNull(message = "개인계좌 비밀번호는 필수 입력값입니다")
    @Min(value = 0000, message = "개인계좌 비밀번호는 4자리 이상이어야 합니다")
    @Max(value = 999999, message = "개인계좌 비밀번호는 6자리 이하여야 합니다")
    private Integer personalAccountPW;
}