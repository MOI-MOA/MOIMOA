package com.b110.jjeonchongmu.domain.user.service;

import com.b110.jjeonchongmu.domain.account.dto.MakeExternalAccountDTO;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.account.service.PersonalAccountService;
import com.b110.jjeonchongmu.domain.user.dto.request.LoginRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.PasswordChangeRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.request.SignupRequestDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.MakeUserResponseDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.TokenResponseDTO;
import com.b110.jjeonchongmu.domain.user.dto.response.UserResponseDTO;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import com.b110.jjeonchongmu.global.security.RefreshTokenService;
import com.b110.jjeonchongmu.global.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final TokenBlacklistService tokenBlacklistService;
	private final PersonalAccountRepo accountRepo; //추가.
	private final ExternalBankApiComponent externalBankApiComponent;
	private final PersonalAccountService personalAccountService;
	private final RefreshTokenService refreshTokenService;
	@Value("${external.bank.api.accountType}")
	private String externalAccountType;
	/**
	 * 회원가입
	 */
	@Transactional
	public void signup(SignupRequestDTO request) {
		if (userRepo.existsByEmail(request.getEmail())) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}

		log.info("은행 유저 생성 시작 - email: {}", request.getEmail());
		//은행 유저 생성.
		MakeUserResponseDTO makeUserResponseDTO = externalBankApiComponent.createBankAppUser(
				request.getEmail());
		log.info("은행 유저 생성 완료 - userKey: {}", makeUserResponseDTO.getUserKey());

		User user = User.builder()
				.email(request.getEmail())
				.name(request.getName())
				.userKey(makeUserResponseDTO.getUserKey())
				.password(passwordEncoder.encode(request.getPassword()))
				.birth(request.getBirth())
				.build();

		userRepo.save(user);
		log.info("사용자 DB 저장 완료 - userId: {}", user.getUserId());

		MakeExternalAccountDTO makeExternalAccountDTO = new MakeExternalAccountDTO(
				makeUserResponseDTO.getUserKey(), externalAccountType, request.getPersonalAccountPW());
		log.info("개인 계좌 생성 시작 - userKey: {}", makeUserResponseDTO.getUserKey());
		personalAccountService.addPersonalAccount(makeExternalAccountDTO, user.getUserId());
		log.info("개인 계좌 생성 완료");
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

		// Refresh Token을 Redis에 저장
		refreshTokenService.saveRefreshToken(user.getUserId(), refreshToken, jwtTokenProvider.getExpirationTime(refreshToken));

		return TokenResponseDTO.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	/**
	 * 로그아웃
	 */
	public void logout() {
		String token = jwtTokenProvider.resolveToken(
				((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
		if (token != null) {
			// 엑세스 토큰 블랙리스트 추가
			long expirationTime = jwtTokenProvider.getExpirationTime(token);
			tokenBlacklistService.addToBlacklist(token, expirationTime);

			// Refresh Token 삭제
			Long userId = jwtTokenProvider.getUserId(token);
			refreshTokenService.deleteRefreshTokenByUserId(userId);
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
	public void withdraw() {
		userRepo.delete(getCurrentUser());
		SecurityContextHolder.clearContext();
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

	// 현재 사용자 정보 조회
	public User getCurrentUser() {
//		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		Long userId = jwtTokenProvider.getUserId();

		System.out.println("UserService 현재 사용자 정보 조회 userId ==  " + userId);
		return userRepo.findById(userId)
				.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	// 현재 사용자 아이디 조회
	public Long getCurrentUserId() {
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			return Long.valueOf(userId);
		} catch (NumberFormatException e) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}
	}



}
