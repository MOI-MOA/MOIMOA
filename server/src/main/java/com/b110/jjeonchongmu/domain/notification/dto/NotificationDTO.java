package com.b110.jjeonchongmu.domain.notification.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long notificationId;
    private String notificationContent;
    private String notificationType;
    private Integer dataId;
    private Integer dataType;
    private Boolean isRead;
    private LocalDateTime notificationCreatedAt;
    private String userId;
}