//package com.b110.jjeonchongmu.global.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.StringRedisSerializer;
//
///**
// * Redis 설정 클래스
// * 토큰 블랙리스트 관리 및 기타 캐시 데이터를 저장하기 위한 Redis 설정
// *
// * 주요 기능:
// * 1. Redis 연결 설정
// * 2. RedisTemplate 구성
// * 3. 직렬화 방식 설정
// */
//@Configuration
//public class RedisConfig {
//
//    @Value("${spring.redis.host}")
//    private String host;
//
//    @Value("${spring.redis.port}")
//    private int port;
//
//    /**
//     * Redis 연결을 위한 ConnectionFactory 빈 등록
//     * Lettuce 클라이언트를 사용하여 Redis 서버와 연결
//     *
//     * @return LettuceConnectionFactory 인스턴스
//     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(host, port);
//    }
//
//    /**
//     * Redis 데이터 접근을 위한 RedisTemplate 빈 등록
//     * String 형식의 키-값 저장소로 사용
//     *
//     * @param connectionFactory Redis 연결 팩토리
//     * @return RedisTemplate 인스턴스
//     */
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(connectionFactory);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }
//}