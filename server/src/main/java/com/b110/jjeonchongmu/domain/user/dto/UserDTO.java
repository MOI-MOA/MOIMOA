package com.b110.jjeonchongmu.domain.user.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String email;
    private String name;
    private String birth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}