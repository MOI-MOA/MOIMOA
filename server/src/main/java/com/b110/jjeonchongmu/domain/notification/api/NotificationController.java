package com.b110.jjeonchongmu.domain.notification.api;

import com.b110.jjeonchongmu.domain.notification.dto.NotificationCreateDto;
import com.b110.jjeonchongmu.domain.notification.dto.NotificationResponseDto;
import com.b110.jjeonchongmu.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 알림 관련 API 컨트롤러
 * 
 * 1. 알림 일정 생성 - POST /api/v1/notification
 *    - Request: notificationContent, notificationType, data_id, data_type, notificationCreatedAt
 * 
 * 2. 미확인 알림 조회 - GET /api/v1/notification/unread
 *    - Response: notificationContent, notificationType, dataId, dataType, notificationCreatedAt
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<String> createNotification(@RequestBody NotificationCreateDto dto) {
        notificationService.createNotification(dto);
        return ResponseEntity.status(201).body("알림생성성공");
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications() {
        return ResponseEntity.ok(notificationService.getUnreadNotifications());
    }
}
