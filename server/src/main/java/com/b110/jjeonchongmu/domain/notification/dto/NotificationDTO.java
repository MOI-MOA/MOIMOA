package com.b110.jjeonchongmu.domain.notification.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class NotificationCreateRequest {
    private String notificationContent;
    private int notificationType;
    private Long dataId;
    private int dataType;
    private LocalDateTime notificationCreatedAt;
}

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
class NotificationListResponse {
    private List<NotificationDTO> notifications;
}