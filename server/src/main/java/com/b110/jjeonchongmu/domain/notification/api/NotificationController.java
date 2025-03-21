package com.b110.jjeonchongmu.domain.notification.api;

import com.b110.jjeonchongmu.domain.notification.dto.*;
import com.b110.jjeonchongmu.domain.notification.service.NotificationService;
import com.b110.jjeonchongmu.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    /**
     * 알림 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse> createNotification(@RequestBody NotificationCreateRequest request) {
        notificationService.createNotification(request);
        return ResponseEntity.status(201)
                .body(new ApiResponse(201, "알림 생성 성공"));
    }

    /**
     * 미확인 알림 조회
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse> getUnreadNotifications() {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(new ApiResponse(200, "미확인 알림 조회 성공", notifications));
    }
}
