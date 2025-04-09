package com.b110.jjeonchongmu.domain.mypage.service;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.repo.AutoPaymentRepo;
import com.b110.jjeonchongmu.domain.account.repo.PersonalAccountRepo;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.mypage.dto.MyPageResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentDto;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.GatheringProjection;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.UpdateAutoPaymentRequestDto;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.ProfileDefaultResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.UpdateUserRequestDto;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.UpdateUserResponseDto;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.ParticipationRateData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.StatisticsResponse;
import com.b110.jjeonchongmu.domain.mypage.repo.CustomMyPageRepository;
import com.b110.jjeonchongmu.domain.mypage.repo.MypageRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.component.TradeHistoryComponent;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

	private final MypageRepo mypageRepo;
	private final UserRepo userRepo;
	private final ScheduleRepo scheduleRepo;
	private final GatheringRepo gatheringRepo;
	private final AutoPaymentRepo autoPaymentRepo;
	private final PersonalAccountRepo personalAccountRepo;
	private final TradeHistoryComponent tradeHistoryComponent;
	private final CustomMyPageRepository customMyPageRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public List<AutoPaymentDto> getAutoPayments(String type) {
		return null;
	}

	public MyPageResponse getMyPage(Long userId) {
		return mypageRepo.findMyPageInfo(userId);
	}

	public StatisticsResponse getStatistics(Long userId) {

		User user = userRepo.getUserByUserId(userId);
		LocalDate today = LocalDate.now();
		int endYear = today.getYear();
		int endMonth = today.getMonthValue();
		int startMonth = endMonth - 6;
		int startYear = endYear;
		if (startMonth <= 0) {
			startMonth += 12;
			startYear += 1;
		}
		YearMonth startYearMonth = YearMonth.of(startYear, startMonth);
		YearMonth endYearMonth = YearMonth.of(endYear, endMonth);
		LocalDateTime start = startYearMonth.atDay(1).atStartOfDay(); // ex) 2025-02-01 00:00:00
		LocalDateTime end = endYearMonth.atEndOfMonth().atTime(23, 59, 59); // ex) 2025-02-28 23:59:59

		// ###################################
		List<MonthlyExpenseData> monthlyExpenseDatas =
				scheduleRepo.findMonthlyExpenseDataByUserIdAndDateBetween(
						userId, start, end);

		List<GroupExpenseData> groupExpenseDatas = scheduleRepo.findGroupExpensesByUserId(
				userId);

		List<ParticipationRateData> participationRateDatas = new ArrayList<>();
		// ###################################

		List<Gathering> gatherings = gatheringRepo.findByManager_UserId(userId);

		for (Gathering gathering : gatherings) {
			int participate = 0;
			int scheduleSize = 0;
			List<Schedule> schedules = gathering.getSchedules();
			scheduleSize = schedules.size();
			for (Schedule schedule : schedules) {
				List<ScheduleMember> scheduleMembers = schedule.getAttendees();
				for (ScheduleMember scheduleMember : scheduleMembers) {
					if (scheduleMember.getScheduleMember().getUserId().equals(userId)) {
						participate++;
						break;
					}
				}
			}
			String name = gathering.getGatheringName();
			ParticipationRateData participationRateData = new ParticipationRateData(
					name, participate, scheduleSize
			);
			participationRateDatas.add(participationRateData);
		}
		return new StatisticsResponse(monthlyExpenseDatas, groupExpenseDatas, participationRateDatas);
	}

	private Long getCurrentUserId() {
		return jwtTokenProvider.getUserId();
	}

	public AutoPaymentResponse getAutoPaymentResponseByUserId(Long id) {
		User user = userRepo.getUserByUserId(id);
		PersonalAccount personalAccount = personalAccountRepo.findByUserId(user.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("개인 계좌를 찾을 수 없습니다."));

		List<AutoPayment> autoPayments = autoPaymentRepo.findByUserId(user.getUserId());

		List<GatheringProjection> gatheringProjections = customMyPageRepository.getAutoPaymentDtos(
				user.getUserId());
		List<AutoPaymentDto> autoPaymentDtos = autoPayments.stream().flatMap(
				autoPayment -> {
					return gatheringProjections.stream()
							.filter(projection -> projection.getGatheringId()
									.equals(autoPayment.getGatheringAccount().getGathering().getGatheringId()))
							.map(matchedProjection -> new AutoPaymentDto(
									autoPayment.getAutoPaymentId(),
									matchedProjection.getBasicFee(),
									autoPayment.getAutoPaymentDate(),
									autoPayment.getIsActive(),
									matchedProjection.getAccountNo(),
									matchedProjection.getGatheringDeposit(),
									matchedProjection.getGatheringName(),
									matchedProjection.getAccountBalance() + matchedProjection.getAccountDeposit(),
									matchedProjection.isPaymentStatus()
							));
				}).collect(Collectors.toList());

		return new AutoPaymentResponse(user, personalAccountRepo.findByUserId(user.getUserId()).map(
						PersonalAccount::getAccountBalance)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")), autoPaymentDtos);
	}

	public ProfileDefaultResponse getProfileDefaultByUserId(Long id) {
		User user = userRepo.getUserByUserId(id);
		return new ProfileDefaultResponse(user);
	}

	@Transactional
	public void updateAutoTransfer(Long id, Long autoPaymentId,
			UpdateAutoPaymentRequestDto requestDto) {
		User user = userRepo.getUserByUserId(id);

		AutoPayment autoPayment = autoPaymentRepo.findById(autoPaymentId)
				.orElseThrow(() -> new RuntimeException("autoPayment를 autoPaymentId로 찾을 수 없습니다"));
		if (!autoPayment.getPersonalAccount().getUser().getUserId().equals(user.getUserId())) {
			throw new RuntimeException("유저가 자동이체의 주인이 아닙니다.");
		}

		long amount = requestDto.getAmount();
		int day = requestDto.getDay();
		autoPayment.updateAutoPaymentAmount(amount);
		autoPayment.updateAutoPaymentDate(day);
		autoPayment.updateIsActive(requestDto.isStatus());
		autoPaymentRepo.save(autoPayment);
	}

	@Transactional
	public UpdateUserResponseDto updateUserProfile(UpdateUserRequestDto requestDto, Long userId) {
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원 정보 조회 오류"));
		user.updateName(requestDto.getName());
		return new UpdateUserResponseDto(true);
	}

//    public MyAccountResponseDto getMyAccount(Long userId) {
//        User user;
//        try {
//            user = userRepo.getUserByUserId(userId);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        AccountType accountType = AccountType.PERSONAL;
//        Long accountId = user.getPersonalAccount().getAccountId();
//        List<TradeHistoryDTO> tradeList;
//        try {
//            tradeList = tradeHistoryComponent.getTradeHistory(new TradeHistoryRequestDTO(
//                accountType, accountId
//            ));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return new MyAccountResponseDto(user, tradeList);
//    }
}
