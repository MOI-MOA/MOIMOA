package com.b110.jjeonchongmu.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String USER_ID_PREFIX = "user_id:";

    /**
     * Refresh Token을 Redis에 저장
     * @param userId 사용자 ID
     * @param refreshToken Refresh Token
     * @param expirationTime 만료 시간 (밀리초)
     */
    public void saveRefreshToken(Long userId, String refreshToken, long expirationTime) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userIdKey = USER_ID_PREFIX + userId;
        
        // Refresh Token 저장
        redisTemplate.opsForValue().set(tokenKey, String.valueOf(userId), expirationTime, TimeUnit.MILLISECONDS);
        // User ID로 Refresh Token 저장 (로그아웃 시 필요)
        redisTemplate.opsForValue().set(userIdKey, refreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh Token으로 사용자 ID 조회
     * @param refreshToken Refresh Token
     * @return 사용자 ID
     */
    public Long getUserIdByRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(tokenKey);
        return userId != null ? Long.parseLong(userId) : null;
    }

    /**
     * 사용자 ID로 Refresh Token 조회
     * @param userId 사용자 ID
     * @return Refresh Token
     */
    public String getRefreshTokenByUserId(Long userId) {
        String userIdKey = USER_ID_PREFIX + userId;
        return redisTemplate.opsForValue().get(userIdKey);
    }

    /**
     * Refresh Token 삭제
     * @param refreshToken Refresh Token
     */
    public void deleteRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        Long userId = getUserIdByRefreshToken(refreshToken);
        
        if (userId != null) {
            String userIdKey = USER_ID_PREFIX + userId;
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userIdKey);
        }
    }

    /**
     * 사용자 ID로 Refresh Token 삭제
     * @param userId 사용자 ID
     */
    public void deleteRefreshTokenByUserId(Long userId) {
        String userIdKey = USER_ID_PREFIX + userId;
        String refreshToken = redisTemplate.opsForValue().get(userIdKey);
        
        if (refreshToken != null) {
            String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
            redisTemplate.delete(tokenKey);
            redisTemplate.delete(userIdKey);
        }
    }

    /**
     * Refresh Token 존재 여부 확인
     * @param refreshToken Refresh Token
     * @return 존재 여부
     */
    public boolean existsRefreshToken(String refreshToken) {
        String tokenKey = REFRESH_TOKEN_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }
} 