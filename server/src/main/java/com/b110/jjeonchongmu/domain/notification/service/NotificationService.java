package com.b110.jjeonchongmu.domain.notification.service;

import com.b110.jjeonchongmu.domain.notification.dto.*;
import com.b110.jjeonchongmu.domain.notification.entity.Notification;
import com.b110.jjeonchongmu.domain.notification.repo.NotificationRepo;
import com.b110.jjeonchongmu.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
    private final NotificationRepo notificationRepo;
    private final JwtUtil jwtUtil;

    /**
     * 알림 생성
     */
    @Transactional
    public void createNotification(NotificationCreateRequest request) {
        Long userId = jwtUtil.getCurrentMemberId();
        
        Notification notification = Notification.builder()
                .notificationContent(request.getNotificationContent())
                .notificationType(request.getNotificationType())
                .dataId(request.getDataId())
                .dataType(request.getDataType())
                .notificationCreatedAt(request.getNotificationCreatedAt())
                .userId(userId)
                .isRead(false)
                .build();

        notificationRepo.save(notification);
    }

    /**
     * 미확인 알림 조회
     */
    public List<NotificationDTO> getUnreadNotifications() {
        Long userId = jwtUtil.getCurrentMemberId();
        List<Notification> notifications = notificationRepo.findByUserIdAndIsReadFalse(userId);

        return notifications.stream()
                .map(notification -> NotificationDTO.builder()
                        .notificationContent(notification.getNotificationContent())
                        .notificationType(notification.getNotificationType())
                        .dataId(notification.getDataId())
                        .dataType(notification.getDataType())
                        .notificationCreatedAt(notification.getNotificationCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
