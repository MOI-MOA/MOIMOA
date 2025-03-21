package com.b110.jjeonchongmu.domain.notification.api;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

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
    // TODO: 구현 예정
}
