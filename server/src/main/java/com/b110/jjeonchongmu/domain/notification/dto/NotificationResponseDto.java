package com.b110.jjeonchongmu.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponseDto {
    private String notificationContent;
    private int notificationType;
    private int dataId;
    private int dataType;
    private LocalDateTime notificationCreatedAt;
} 