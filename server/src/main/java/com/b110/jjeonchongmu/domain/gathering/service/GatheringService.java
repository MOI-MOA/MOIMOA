package com.b110.jjeonchongmu.domain.gathering.service;

import com.b110.jjeonchongmu.domain.account.dto.MakeExternalAccountDTO;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.account.service.GatheringAccountService;
import com.b110.jjeonchongmu.domain.account.service.AutoPaymentService;
import com.b110.jjeonchongmu.domain.gathering.dto.*;
import com.b110.jjeonchongmu.domain.gathering.dto.AddGatheringDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDetailAccountDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDetailManagerDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDetailResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringDetailSchedules;
import com.b110.jjeonchongmu.domain.gathering.dto.GatheringListResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMemberStatus;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.domain.user.service.UserService;
import com.b110.jjeonchongmu.global.component.ExternalBankApiComponent;
import com.b110.jjeonchongmu.global.exception.CustomException;
import com.b110.jjeonchongmu.global.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import org.springframework.web.client.HttpClientErrorException;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringService {

	private static final Logger log = LoggerFactory.getLogger(GatheringService.class);

	private final GatheringRepo gatheringRepo;
	private final GatheringMemberRepo gatheringMemberRepo;
	private final UserRepo userRepo;
	private final GatheringAccountRepo gatheringAccountRepo;
	private final ExternalBankApiComponent externalBankApiComponent;
	private final GatheringMemberService gatheringMemberService;
	private final UserService userService;
	private final GatheringAccountService gatheringAccountService;
	private final AutoPaymentService autoPaymentService;

	@Value("${external.bank.api.accountType}")
	private String externalAccountType;

	/**
	 * 모임 생성
	 *
	 * @param request 모임 생성 요청 DTO
	 * @return
	 * @throws CustomException 동일한 모임명이 이미 존재하는 경우
	 */
	@Transactional
	public GatheringDTO addGathering(AddGatheringDTO request) {
		// 인증된 사용자 확인
		User currentUser = userService.getCurrentUser();
		if (currentUser == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED);
		}
		
		// 총무 설정
		User manager = currentUser;
		
		// userKey 유효성 검사
		if (manager.getUserKey() == null || manager.getUserKey().isEmpty()) {
			log.error("유효하지 않은 userKey: {}", manager.getUserId());
			throw new CustomException(ErrorCode.INVALID_USER_CREDENTIALS);
		}
		
		log.info("모임 계좌 생성 시작 - userKey: {}", manager.getUserKey());

		// 현재 유저(총무)로 모임 계좌 생성
		MakeExternalAccountDTO makeExternalAccountDTO = new MakeExternalAccountDTO(
				manager.getUserKey(), externalAccountType, request.getGatheringAccountPW());

		try {
			GatheringAccount account = gatheringAccountService.addGroupAccount(makeExternalAccountDTO,
					manager.getUserId());
			log.info("모임 계좌 생성 성공");
			
			// 모임 생성
			Gathering gathering = Gathering.builder()
					.manager(manager)
					.gatheringAccount(account)
					.gatheringName(request.getGatheringName())
					.gatheringIntroduction(request.getGatheringIntroduction())
					.memberCount(0)
					.penaltyRate(request.getPenaltyRate())
					.depositDate(request.getDepositDate())
					.basicFee((long) request.getBasicFee())
					.gatheringDeposit((long) request.getGatheringDeposit())
					.build();

			// 모임 저장
			gatheringRepo.save(gathering);

			// 총무를 모임 멤버로 등록
			gatheringMemberService.addMember(gathering.getGatheringId(), manager.getUserId());

			// 총무의 자동이체 등록
			GatheringMember managerMember = gatheringMemberRepo.findByGatheringGatheringIdAndGatheringMemberUser_UserId(
					gathering.getGatheringId(), manager.getUserId())
					.orElseThrow(() -> new CustomException(ErrorCode.NOT_GATHERING_MEMBER));
			autoPaymentService.createAutoPaymentForGatheringMember(managerMember);

			return GatheringDTO.builder()
					.gatheringId(gathering.getGatheringId())
					.managerId(gathering.getManagerId())
					.gatheringAccountId(gathering.getGatheringAccount().getAccountId())
					.gatheringName(gathering.getGatheringName())
					.gatheringIntroduction(gathering.getGatheringIntroduction())
					.memberCount(gathering.getMemberCount())
					.penaltyRate(gathering.getPenaltyRate())
					.depositDate(gathering.getDepositDate())
					.basicFee(gathering.getBasicFee())
					.gatheringDeposit(gathering.getGatheringDeposit())
					.build();
		} catch (HttpClientErrorException e) {
			log.error("외부 은행 API 호출 실패: {}", e.getMessage());
			if (e.getStatusCode().is4xxClientError()) {
				throw new CustomException(ErrorCode.INVALID_USER_CREDENTIALS);
			}
			throw new CustomException(ErrorCode.GATHERING_ACCOUNT_NOT_FOUND);
		} catch (Exception e) {
			log.error("모임 계좌 생성 실패: {}", e.getMessage());
			throw new CustomException(ErrorCode.GATHERING_ACCOUNT_NOT_FOUND);
		}
	}

	/**
	 * 모임 정보 수정
	 *
	 * @param gatheringId 모임 ID
	 * @param request     수정할 모임 정보
	 * @throws CustomException 모임을 찾을 수 없거나, 동일한 모임명이 존재하는 경우
	 */
	@Transactional
	public void updateGathering(Long gatheringId, GatheringDTO request) {
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		// 총무 권한 체크
		User currentUser = userService.getCurrentUser();
		if (!gathering.getManagerId().equals(currentUser.getUserId())) {
			throw new CustomException(ErrorCode.NOT_GATHERING_MANAGER);
		}

		// 모임명 변경 시 중복 체크
		if (!gathering.getGatheringName().equals(request.getGatheringName()) &&
				gatheringRepo.existsByGatheringNameAndManager_UserId(request.getGatheringName(),
						gathering.getManagerId())) {
			throw new CustomException(ErrorCode.GATHERING_NAME_DUPLICATE);
		}

		gathering.updateGathering(
				request.getGatheringName(),
				request.getGatheringIntroduction(),
				request.getMemberCount(),
				request.getPenaltyRate(),
				request.getDepositDate(),
				request.getBasicFee(),
				request.getGatheringDeposit()
		);
	}

	/**
	 * 모임 삭제
	 */
	@Transactional
	public void deleteGathering(Long gatheringId) {
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		// 총무 권한 체크
		User currentUser = userService.getCurrentUser();
		if (!gathering.getManagerId().equals(currentUser.getUserId())) {
			throw new CustomException(ErrorCode.NOT_GATHERING_MANAGER);
		}

		gatheringRepo.deleteById(gatheringId);
	}

	/**
	 * 전체 모임 목록 조회
	 */
	public GatheringListResponseDTO getAllGatherings(Long userId) {
		User user = userRepo.getUserByUserId(userId);
		List<GatheringMember> gatheringMembers = user.getGatheringMembers();
		List<Gathering> gatherings = new ArrayList<>();
		for (GatheringMember gm : gatheringMembers) {
			gatherings.add(gm.getGathering());
		}
		return convertToGatheringListResponse(gatherings);
	}

	/**
	 * 내 모임 조회
	 */
	public GatheringListResponseDTO getMyGatherings(Long userId) {
		List<GatheringDTO> gatherings = gatheringMemberRepo.findGatheringsByUserId(userId);
		return GatheringListResponseDTO.builder()
				.gatherings(gatherings)
				.build();
	}

	/**
	 * 모임 상세 조회
	 */
	public GatheringDetailResponseDTO getGatheringDetail(Long userId, Long gatheringId) {
		System.out.println(gatheringId);
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		List<GatheringMember> members = gatheringMemberRepo.findByGatheringGatheringId(gatheringId);

		User user = userRepo.getUserByUserId(userId);
		List<Schedule> schedules = gathering.getSchedules();
		System.out.println("userId + gatheringId" + userId + ", " + gatheringId);
		GatheringMember gatheringMember =
				gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(
						gathering.getGatheringId(), user.getUserId())
						.orElseThrow(() -> new RuntimeException("NOTFOUND: gatheringId와 userId로 gatheringMember를 찾을 수 없습니다."));


		return GatheringDetailResponseDTO.builder()
				.id(gathering.getGatheringId())
				.name(gathering.getGatheringName())
				.description(gathering.getGatheringIntroduction())
				.totalMembers(gathering.getGatheringMembers().size())
				.monthlyFee(gathering.getBasicFee())
				.isManager(Objects.equals(gathering.getManagerId(), userId))
				.manager(new GatheringDetailManagerDTO(user))
				.accounts(new GatheringDetailAccountDTO(gathering, gatheringMember))
				.schedules(schedules.stream()
						.map(schedule -> GatheringDetailSchedules.builder()
								.id(schedule.getId())
								.date(schedule.getStartTime())
								.participants(schedule.getAttendees().size())
								.budgetPerPerson(schedule.getPerBudget())
								.totalBudget(schedule.getAttendees().size() * schedule.getPerBudget())
								.location(schedule.getPlace())
								.build())
						.collect(Collectors.toList()))
				.build();
	}

//    public TradeListResponseDTO getGatheringTrades(Long gatheringId) {
//        Gathering gathering = gatheringRepo.findById(gatheringId)
//                .orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));
//
//        GatheringAccount account = gathering.getGatheringAccountId();
//        if (account == null) {
//            throw new CustomException(ErrorCode.GATHERING_ACCOUNT_NOT_FOUND);
//        }
//
//        // TODO: 거래내역 조회 로직은 계좌 서비스와 연동 필요
//        return TradeListResponseDTO.builder()
//                .trades(List.of())
//                .build();
//    }

	private GatheringListResponseDTO convertToGatheringListResponse(List<Gathering> gatherings) {
		List<GatheringDTO> gatheringDTOs = gatherings.stream()
				.map(gathering -> GatheringDTO.builder()
						.gatheringId(gathering.getGatheringId())
						.managerId(gathering.getManagerId())
						.gatheringAccountId(gathering.getGatheringAccount().getAccountId())
						.gatheringName(gathering.getGatheringName())
						.gatheringIntroduction(gathering.getGatheringIntroduction())
						.memberCount(gathering.getMemberCount())
						.penaltyRate(gathering.getPenaltyRate())
						.depositDate(gathering.getDepositDate())
						.basicFee(gathering.getBasicFee())
						.gatheringDeposit(gathering.getGatheringDeposit())
						.build())
				.collect(Collectors.toList());

		return GatheringListResponseDTO.builder()
				.gatherings(gatheringDTOs)
				.build();
	}


	/**
	 * 사용자가 해당 모임의 총무인지 확인
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      사용자 ID
	 * @return 총무 여부
	 * @throws CustomException 모임을 찾을 수 없는 경우
	 */
	public boolean isManager(Long gatheringId, Long userId) {
		return gatheringRepo.findById(gatheringId)
				.map(gathering -> gathering.getManagerId().equals(userId))
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));
	}

	/**
	 * 사용자가 해당 모임의 회원인지 확인
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      사용자 ID
	 * @throws CustomException 모임을 찾을 수 없거나 회원이 아닌 경우
	 */
	public void validateGatheringMember(Long gatheringId, Long userId) {
		if (!gatheringMemberRepo.existsByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId,
				userId)) {
			throw new CustomException(ErrorCode.NOT_GATHERING_MEMBER);
		}
	}

	/**
	 * 계좌 ID로 모임 조회
	 *
	 * @param accountId 계좌 ID
	 * @return 모임 정보
	 * @throws CustomException 모임을 찾을 수 없는 경우
	 */
	public GatheringDTO getGatheringByAccountId(Long accountId) {
		Gathering gathering = gatheringRepo.findByGatheringAccountId(accountId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		return GatheringDTO.builder()
				.gatheringId(gathering.getGatheringId())
				.managerId(gathering.getManagerId())
				.gatheringAccountId(gathering.getGatheringAccount().getAccountId())
				.gatheringName(gathering.getGatheringName())
				.gatheringIntroduction(gathering.getGatheringIntroduction())
				.memberCount(gathering.getMemberCount())
				.penaltyRate(gathering.getPenaltyRate())
				.depositDate(gathering.getDepositDate())
				.basicFee(gathering.getBasicFee())
				.gatheringDeposit(gathering.getGatheringDeposit())
				.build();
	}

	/**
	 * 모임 계좌 유효성 검증
	 *
	 * @param gatheringId 모임 ID
	 * @param accountId   계좌 ID
	 * @throws CustomException 모임을 찾을 수 없거나, 계좌가 유효하지 않은 경우
	 */
	public void validateGatheringAccount(Long gatheringId, Long accountId) {
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		if (!accountId.equals(gathering.getGatheringAccount().getAccountId())) {
			throw new CustomException(ErrorCode.GATHERING_ACCOUNT_INVALID);
		}
	}

	/**
	 * 모임 회원 수 업데이트
	 *
	 * @param gatheringId 모임 ID
	 * @throws CustomException 모임을 찾을 수 없는 경우
	 */
	@Transactional
	public void updateMemberCount(Long gatheringId) {

		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

		// 총무 권한 체크
		User currentUser = userService.getCurrentUser();
		if (!gathering.getManagerId().equals(currentUser.getUserId())) {
			throw new CustomException(ErrorCode.NOT_GATHERING_MANAGER);
		}
				
		long memberCount = gatheringMemberRepo.countByGatheringGatheringId(gatheringId);
		gathering.updateMemberCount((int) memberCount);
	}

	/**
	 * 총무가 관리하는 모임 목록 조회
	 *
	 * @param managerId 총무 ID
	 * @return 모임 목록
	 */
	public GatheringListResponseDTO getManagerGatherings(Long managerId) {
		List<Gathering> gatherings = gatheringRepo.findByManager_UserId(managerId);
		return convertToGatheringListResponse(gatherings);
	}

}