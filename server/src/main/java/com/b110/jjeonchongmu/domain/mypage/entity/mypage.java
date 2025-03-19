//package com.b110.jjeonchongmu.domain.mypage.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "notification")
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class mypage {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "notification_id", nullable = false)
//    private Long notificationId;
//
//    @Column(name = "notification_content", nullable = true)
//    private String notificationContent;
//
//    @Column(name = "notification_type", nullable = true)
//    private String notificationType;
//
//    @Column(name = "data_id", nullable = true)
//    private Integer dataId;
//
//    @Column(name = "data_type", nullable = true)
//    private Integer dataType;
//
//    @Column(name = "is_read", nullable = false)
//    private Boolean isRead = false;
//
//    @Column(name = "notification_created_at", nullable = false)
//    private LocalDateTime notificationCreatedAt;
//
//    @Column(name = "user_id", nullable = false)
//    private String userId;
//}