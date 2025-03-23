package com.b110.jjeonchongmu.domain.notification.repo;

import com.b110.jjeonchongmu.domain.notification.dto.NotificationResponseDto;
import com.b110.jjeonchongmu.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Long> {
    
    @Query("SELECT new com.b110.jjeonchongmu.domain.notification.dto.NotificationResponseDto(" +
           "n.content, n.notificationType, n.dataId, n.dataType, n.createdAt) " +
           "FROM Notification n " +
           "WHERE n.user.id = :userId AND n.isRead = false " +
           "ORDER BY n.createdAt DESC")
    List<NotificationResponseDto> findUnreadNotifications(@Param("userId") Long userId);
}
