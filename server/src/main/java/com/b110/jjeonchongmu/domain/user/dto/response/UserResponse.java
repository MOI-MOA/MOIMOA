package com.b110.jjeonchongmu.domain.user.dto.response;

import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
public class UserResponse {
    private Long userId;
    private String email;
    private String name;
    private Boolean scheduleNotificationEnabled;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .scheduleNotificationEnabled(user.getScheduleNotificationEnabled())
                .build();
    }
} 