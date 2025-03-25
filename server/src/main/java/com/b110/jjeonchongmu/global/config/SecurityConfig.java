package com.b110.jjeonchongmu.global.config;

import com.b110.jjeonchongmu.global.security.CustomUserDetailsService;
import com.b110.jjeonchongmu.global.security.JwtAuthenticationFilter;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import com.b110.jjeonchongmu.global.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * JWT 기반의 인증/인가를 처리하기 위한 설정을 포함
 */
@Configuration  // 스프링 설정 클래스임을 명시
@EnableWebSecurity  // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;  // JWT 토큰 생성 및 검증을 위한 컴포넌트
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Spring Security FilterChain 구성
     * 
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 구성 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 기능 비활성화 (JWT를 사용하므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                
                // 세션 관리 설정
                .sessionManagement(sessionManagement -> 
                        // 세션을 생성하지 않고 상태를 저장하지 않음 (JWT 사용)
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(authorizeRequests -> 
                        authorizeRequests
                                // 인증 없이 접근 가능한 경로 설정
                                .requestMatchers("/api/v1/login", "/api/v1/signup", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                // 그 외 모든 요청은 인증 필요
                                .anyRequest().permitAll())

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, tokenBlacklistService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
     * BCrypt 해시 함수를 사용하여 비밀번호를 암호화
     * 
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 