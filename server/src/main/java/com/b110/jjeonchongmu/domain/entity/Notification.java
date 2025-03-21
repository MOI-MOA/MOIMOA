package com.b110.jjeonchongmu.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notification_content", nullable = true)
    private String notificationContent;

    @Column(name = "notification_type", nullable = true)
    private String notificationType;

    @Column(name = "data_id", nullable = true)
    private Integer dataId;

    @Column(name = "data_type", nullable = true)
    private Integer dataType;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "notification_created_at", nullable = false)
    private LocalDateTime notificationCreatedAt;


}