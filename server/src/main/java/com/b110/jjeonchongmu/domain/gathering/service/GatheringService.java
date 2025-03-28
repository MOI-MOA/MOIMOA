package com.b110.jjeonchongmu.domain.gathering.service;

import com.b110.jjeonchongmu.domain.account.dto.MakeExternalAccountDTO;
import com.b110.jjeonchongmu.domain.account.dto.MakeGatheringAccountDTO;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.repo.GatheringAccountRepo;
import com.b110.jjeonchongmu.domain.account.service.GatheringAccountService;
import com.b110.jjeonchongmu.domain.gathering.dto.*;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.b110.jjeonchongmu.domain.account.dto.MakeGatheringAccountDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringService {

	private final GatheringRepo gatheringRepo;
	private final GatheringMemberRepo gatheringMemberRepo;
	private final UserRepo userRepo;
	private final GatheringAccountRepo gatheringAccountRepo;
	private final ExternalBankApiComponent externalBankApiComponent;
	@Value("${external.bank.api.accountType}")
	private String externalAccountType;

	/**
	 * 모임 생성
	 *
	 * @param request 모임 생성 요청 DTO
	 * @return
	 * @throws CustomException 동일한 모임명이 이미 존재하는 경우
	 */
	private final UserService userService;
	private final GatheringAccountService gatheringAccountService;

	//계좌 생성 및 추가.
	@Transactional
	public GatheringDTO addGathering(AddGatheringDTO request) {
		User currentUser = userService.getCurrentUser();

		// 해당 총무가 동일한 모임명 갖고 있는지  확인
		if (gatheringRepo.existsByGatheringNameAndManager_UserId(request.getGatheringName(),
				currentUser.getUserId())) {
			throw new CustomException(ErrorCode.DUPLICATE_GATHERING_NAME);
		}
		// 총무 설정.
		User manager = currentUser;

//        // 모임 계좌 조회
//        GatheringAccount account = gatheringAccountRepo.findById(request.getGatheringAccountId())
//                .orElseThrow(() -> new CustomException(ErrorCode.GATHERING_ACCOUNT_NOT_FOUND));

		// 현재 유저(총무)로 모임 계좌 생성. (유저키, 계좌타입, 비밀번호) + ()
		MakeExternalAccountDTO makeExternalAccountDTO = new MakeExternalAccountDTO(
				currentUser.getUserKey(), externalAccountType, request.getGatheringAccountPW());

		GatheringAccount account = gatheringAccountService.addGroupAccount(makeExternalAccountDTO,
				currentUser.getUserId());

		// 모임 생성
		Gathering gathering = Gathering.builder()
				.manager(manager)
				.gatheringAccount(account)
				.gatheringName(request.getGatheringName())
				.gatheringIntroduction(request.getGatheringIntroduction())
				.memberCount(1) // 기본값 1 설정.
				.penaltyRate(request.getPenaltyRate())
				.depositDate(request.getDepositDate())
				.basicFee((long) request.getBasicFee())
				.gatheringDeposit((long) request.getGatheringDeposit())
				.build();
		//모임 저장.
		gatheringRepo.save(gathering);

		// 모임계좌 DB 저장.

		return null;
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
		if (!gatheringRepo.existsById(gatheringId)) {
			throw new CustomException(ErrorCode.GATHERING_NOT_FOUND);
		}
		gatheringRepo.deleteById(gatheringId);
	}

	/**
	 * 전체 모임 목록 조회
	 */
	public GatheringListResponseDTO getAllGatherings() {
		List<Gathering> gatherings = gatheringRepo.findAll();
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