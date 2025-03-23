//package com.b110.jjeonchongmu.global.config;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Configuration;
//import redis.embedded.RedisServer;
//
///**
// * 임베디드 Redis 서버 설정
// * spring.redis.embedded.enabled=true 일 때만 활성화
// */
//@Configuration
//@ConditionalOnProperty(name = "spring.redis.embedded.enabled", havingValue = "true")
//public class EmbeddedRedisConfig {
//
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//    private RedisServer redisServer;
//
//    @PostConstruct
//    public void startRedis() {
//        try {
//            redisServer = new RedisServer(redisPort);
//            redisServer.start();
//        } catch (Exception e) {
//            // 이미 Redis가 실행 중인 경우 무시
//        }
//    }
//
//    @PreDestroy
//    public void stopRedis() {
//        if (redisServer != null && redisServer.isActive()) {
//            redisServer.stop();
//        }
//    }
//}