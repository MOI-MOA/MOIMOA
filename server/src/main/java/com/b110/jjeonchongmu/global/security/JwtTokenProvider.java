package com.b110.jjeonchongmu.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;

    private Key key;

    /**
     * 현재 요청의 HttpServletRequest 가져오기
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("Request not found");
        }
        return attributes.getRequest();
    }

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenValidity);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenValidity);
    }

    /**
     * 토큰 생성
     */
    private String createToken(Long userId, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }



    /**
     * 현재 사용자의 ID 추출
     */
    public Long getUserId() {
        String token = resolveToken(getCurrentRequest());
        if (token != null && validateToken(token)) {
            return getUserId(token);
        }
        throw new RuntimeException("Invalid token");
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public Long getUserId(String token) {
        String subject = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return Long.parseLong(subject);
    }

    /**
     * Request의 Header에서 token 값 추출
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 토큰 유효성 + 만료일자 확인
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰의 만료 시간 조회
     */
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().getTime() - new Date().getTime();
    }
} 