package com.b110.jjeonchongmu.domain.user.dto.response;

import com.b110.jjeonchongmu.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@NoArgsConstructor
public class UserResponseDTO {
    private Long userId;
    private String userEmail;
    private String userKey;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public UserResponseDTO(Long userId, String userEmail, String userKey, LocalDate birth,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userKey = userKey;
        this.birth = birth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .userEmail(user.getEmail())
                .userKey(user.getUserKey())
                .birth(user.getBirth())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 