package com.b110.jjeonchongmu.domain.mypage.api;

import com.b110.jjeonchongmu.domain.mypage.dto.MyPageResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.UpdateAutoPaymentRequestDto;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.UpdateAutoPaymentResponseDto;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.ProfileDefaultResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.UpdateUserRequestDto;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.UpdateUserResponseDto;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.StatisticsResponse;
import com.b110.jjeonchongmu.domain.mypage.service.MypageService;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 마이페이지 관련 API 컨트롤러
 * <p>
 * 1. 마이페이지 조회 - GET /api/v1/mypage - Response: userId, name, email, scheduleNotificationEnabled
 * <p>
 * 2. 통계 현황 조회 - GET /api/v1/mypage/statistics - Response: - 총 지출액 - 월평균 지출액 - 최다 지출 모임 - 최다 지출 월 -
 * 평균 참석률 - 최다 참석 모임
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MypageController {

	private final MypageService myPageService;
	private final JwtTokenProvider jwtTokenProvider;

	@GetMapping("/profile")
	public ResponseEntity<ProfileDefaultResponse> getProfileDefault() {
		Long id = jwtTokenProvider.getUserId();
		return ResponseEntity.ok(myPageService.getProfileDefaultByUserId(id));
	}

	@GetMapping("/profile/autopayment")
	public ResponseEntity<AutoPaymentResponse> getAutoPayments() {
		Long id = jwtTokenProvider.getUserId();
		return ResponseEntity.ok(myPageService.getAutoPaymentResponseByUserId(id));
	}

	@GetMapping("/profile/mypage")
	public ResponseEntity<MyPageResponse> getMyPage() {
		Long id = jwtTokenProvider.getUserId();
		return ResponseEntity.ok(myPageService.getMyPage(id));
	}

	@GetMapping("/profile/mypage/statistics")
	public ResponseEntity<StatisticsResponse> getStatistics() {
		Long id = jwtTokenProvider.getUserId();
		return ResponseEntity.ok(myPageService.getStatistics(id));
	}

	@PatchMapping("/profile/autopayment/{autoTransferId}")
	public ResponseEntity<UpdateAutoPaymentResponseDto> updateAutoTransfer(
			@PathVariable Long autoTransferId,
			@RequestBody UpdateAutoPaymentRequestDto requestDto
	) {
		Long id = jwtTokenProvider.getUserId();
		try {
			myPageService.updateAutoTransfer(id, autoTransferId, requestDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(new UpdateAutoPaymentResponseDto("success"));
	}

	@PatchMapping("/profile")
	public ResponseEntity<UpdateUserResponseDto> updateUserProfile(
			@RequestBody UpdateUserRequestDto requestDto
	) {
		Long userId = jwtTokenProvider.getUserId();
		return ResponseEntity.ok(myPageService.updateUserProfile(requestDto, userId));
	}

//    @GetMapping("/profile/mypage/account-history")
//    public ResponseEntity<MyAccountResponseDto> myAccount() {
//        Long id = jwtTokenProvider.getUserId();
//
//        MyAccountResponseDto response;
//        try {
//            response = myPageService.getMyAccount(id);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return ResponseEntity.ok(response);
//    }

}
