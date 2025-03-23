package com.b110.jjeonchongmu.domain.notification.entity;

import com.b110.jjeonchongmu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
//@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_content", nullable = false)
    private String notificationContent;

    @Column(name = "notification_type", nullable = false)
    private int notificationType;

    @Column(name = "data_id", nullable = false)
    private int dataId;

    @Column(name = "data_type", nullable = false)
    private int dataType;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "notification_created_at", nullable = false)
    private LocalDateTime notificationCreatedAt;

    @Builder
    public Notification(String notificationContent, int notificationType, int dataId, int dataType, 
                       LocalDateTime notificationCreatedAt, User user) {
        this.notificationContent = notificationContent;
        this.notificationType = notificationType;
        this.dataId = dataId;
        this.dataType = dataType;
        this.notificationCreatedAt = notificationCreatedAt;
        this.user = user;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}