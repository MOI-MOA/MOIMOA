package com.b110.jjeonchongmu.global.security;

import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
//            if (tokenBlacklistService.isBlacklisted(token)) {
//                log.debug("Blacklisted token: {}", token);
//                filterChain.doFilter(request, response);
//                return;
//            }

            // 토큰 유효성 검사
            System.out.println(token);
            try {
                // 토큰이 만료되었는지 확인
                Claims claims = null;
                try {
                    claims = Jwts.parserBuilder()
                            .setSigningKey(jwtTokenProvider.getKey())
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
                } catch (Exception e) {
                    // 토큰 파싱 실패
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다.");
                    return;
                }
                
                // 토큰 만료 여부 확인
                Date expiration = claims.getExpiration();
                boolean isExpired = expiration.before(new Date());
                
                if (isExpired) {
                    // 엑세스 토큰인지 리프레시 토큰인지 확인
                    boolean isAccessToken = jwtTokenProvider.isAccessToken(token);
                    
                    if (isAccessToken) {
                        // 엑세스 토큰이 만료된 경우 401 응답 반환
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "엑세스 토큰이 만료되었습니다.");
                    } else {
                        // 리프레시 토큰이 만료된 경우 401 응답 반환
                        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.");
                    }
                    return;
                }
                
                // 토큰이 유효한 경우 인증 정보 설정
                Authentication auth = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Set Authentication to security context for '{}', uri: {}",
                        auth.getName(), request.getRequestURI());
            } catch (Exception e) {
                log.error("Error validating token: {}", e.getMessage());
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰 검증 중 오류가 발생했습니다.");
                return;
            }
        } else {
            log.debug("JWT token does not begin with Bearer String");
            // 토큰이 없는 경우 401 응답 반환
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 토큰이 필요합니다.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(jwtTokenProvider.getUserId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status);
        errorResponse.put("message", message);
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}