package com.b110.jjeonchongmu.domain.user.service;

import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.user.dto.request.LoginRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.PasswordChangeRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.SignupRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.TokenResponseDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.UserResponseDTO;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import com.b110.jjeonchongmu.global.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final PersonalAccountRepo accountRepo; //추가. 

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequestDTO request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .userKey(UUID.randomUUID().toString())
                .password(passwordEncoder.encode(request.getPassword()))
                .birth(request.getBirth())
                .build();

        userRepo.save(user);

        //계좌 생성 로직 추가 하기.

    }
    /**
     * 로그인
     */
    public TokenResponseDTO login(LoginRequestDTO request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUserId());

        return TokenResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * 로그아웃
     */
    public void logout() {
        String token = jwtTokenProvider.resolveToken(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        if (token != null) {
            // 엑세스 토큰 블랙리스트 추가
            long expirationTime = jwtTokenProvider.getExpirationTime(token);
            tokenBlacklistService.addToBlacklist(token, expirationTime);

            // 리프레시 토큰 블랙리스트 추가.
            long userId = jwtTokenProvider.getUserId(token);
            String refreshToken = jwtTokenProvider.createRefreshToken(userId); //현재 유효한 리프레시 토큰 조회 로직 필요
            tokenBlacklistService.addToBlacklist(refreshToken, jwtTokenProvider.getExpirationTime(refreshToken));
        }
    }

    /**
     * 사용자 정보 조회
     */
    public UserResponseDTO getMyInfo(long userId) {
        return UserResponseDTO.from(userRepo.getUserByUserId(userId));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdraw(Long userId) {

        userRepo.deleteById(userId);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(PasswordChangeRequestDTO request, long userId) {
        User user = userRepo.getUserByUserId(userId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));
    }

//    /**
//     * 현재 로그인한 사용자 조회
//     */
//    private User getCurrentUser() {
//        String userId = jwtTokenProvider.getUserId();
//        return userRepo.findById(Long.valueOf(userId))
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//    }
}
