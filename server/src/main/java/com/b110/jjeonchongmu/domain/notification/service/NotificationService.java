package com.b110.jjeonchongmu.domain.notification.service;

import com.b110.jjeonchongmu.domain.notification.dto.NotificationCreateDto;
import com.b110.jjeonchongmu.domain.notification.dto.NotificationResponseDto;
import com.b110.jjeonchongmu.domain.notification.entity.Notification;
import com.b110.jjeonchongmu.domain.notification.repo.NotificationRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepo notificationRepo;

    @Transactional
    public void createNotification(NotificationCreateDto dto) {
        User user = getCurrentUser(); // 현재 사용자 정보 가져오기
        
        Notification notification = Notification.builder()
//                .content(dto.getNotificationContent())
                .notificationType(dto.getNotificationType())
                .dataId(dto.getData_id())
                .dataType(dto.getData_type())
//                .createdAt(dto.getNotificationCreatedAt())
                .user(user)
                .build();
                
        notificationRepo.save(notification);
    }

    public List<NotificationResponseDto> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        return notificationRepo.findUnreadNotifications(userId);
    }

    private User getCurrentUser() {
        // SecurityContext에서 현재 사용자 정보를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private Long getCurrentUserId() {
        // SecurityContext에서 현재 사용자 ID를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
