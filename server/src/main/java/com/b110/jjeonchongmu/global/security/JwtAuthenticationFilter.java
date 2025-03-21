package com.b110.jjeonchongmu.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰 기반의 인증을 처리하는 필터
 * 모든 요청에 대해 JWT 토큰을 검증하고 인증 정보를 설정
 * 
 * 주요 기능:
 * 1. 요청 헤더에서 JWT 토큰 추출
 * 2. 토큰 유효성 검증
 * 3. 인증 정보를 SecurityContext에 설정
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * 실제 필터링 로직이 수행되는 메서드
     * 
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null) {
            // 블랙리스트 체크
            if (tokenBlacklistService.isBlacklisted(token)) {
                log.debug("Blacklisted token: {}", token);
                filterChain.doFilter(request, response);
                return;
            }

            // 토큰 유효성 검사
            if (jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Set Authentication to security context for '{}', uri: {}", 
                        auth.getName(), request.getRequestURI());
            } else {
                log.debug("Invalid JWT token.");
            }
        } else {
            log.debug("JWT token does not begin with Bearer String");
        }

        filterChain.doFilter(request, response);
    }
} 