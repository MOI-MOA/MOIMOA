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
public class NotificationCreateDto {
    private String notificationContent;
    private int notificationType;
    private int data_id;
    private int data_type;
    private LocalDateTime notificationCreatedAt;
} 