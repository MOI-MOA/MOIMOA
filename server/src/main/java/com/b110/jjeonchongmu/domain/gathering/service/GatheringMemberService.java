package com.b110.jjeonchongmu.domain.gathering.service;

import com.b110.jjeonchongmu.domain.account.dto.AccountType;
import com.b110.jjeonchongmu.domain.account.dto.TransferRequestDTO;
import com.b110.jjeonchongmu.domain.account.dto.TransferTransactionHistoryDTO;
import com.b110.jjeonchongmu.domain.account.service.AutoPaymentService;
import com.b110.jjeonchongmu.domain.account.service.GatheringAccountService;
import com.b110.jjeonchongmu.domain.account.service.PersonalAccountService;
import com.b110.jjeonchongmu.domain.gathering.dto.*;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMemberStatus;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleMemberRepo;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.SchemaPropertyDeprecatingConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GatheringMemberService {

	private final GatheringMemberRepo gatheringMemberRepo;    // 모임 회원 레포지토리
	private final GatheringRepo gatheringRepo;               // 모임 레포지토리
	private final UserRepo userRepo;                         // 사용자 레포지토리
	private final JwtTokenProvider jwtTokenProvider;         // JWT 토큰 제공자
	private final EntityManager em;
	private final AutoPaymentService autoPaymentService;
	private final ScheduleRepo scheduleRepo;
	private final ScheduleMemberRepo scheduleMemberRepo;
	private final SchemaPropertyDeprecatingConverter schemaPropertyDeprecatingConverter;
	private final PersonalAccountService personalAccountService;
	private final GatheringAccountService gatheringAccountService;

	/**
	 * 모임 초대 링크 생성 (총무 전용)
	 *
	 * @param gatheringId 모임 ID
	 * @return 생성된 초대 링크 정보를 담은 DTO
	 * @throws RuntimeException 모임을 찾을 수 없거나 총무가 아닌 경우
	 */
	@Transactional
	public InviteResponseDTO createInviteLink(Long gatheringId) {
		Long userId = jwtTokenProvider.getUserId();
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		if (!gathering.getManagerId().equals(userId)) {
			throw new RuntimeException("총무만 초대 링크를 생성할 수 있습니다.");
		}

		String inviteLink = UUID.randomUUID().toString();

		return InviteResponseDTO.builder()
				.inviteLink(inviteLink)
				.build();
	}

	/**
	 * 모임 참여 요청을 거절합니다. (총무 전용)
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      거절할 사용자 ID
	 * @throws RuntimeException 모임을 찾을 수 없거나 총무가 아닌 경우
	 */
	@Transactional
	public void rejectGathering(Long gatheringId, Long userId) {
		Long currentUserId = jwtTokenProvider.getUserId();
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		if (!gathering.getManagerId().equals(currentUserId)) {
			throw new RuntimeException("총무만 참여를 거절할 수 있습니다.");
		}

		gatheringMemberRepo.deleteByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId, userId);
	}

	/**
	 * 모임 회원 삭제 (총무 전용)
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      삭제할 회원 ID
	 * @throws RuntimeException 모임을 찾을 수 없거나 총무가 아닌 경우
	 */
	@Transactional
	public void deleteMember(Long gatheringId, Long userId) {
		Long currentUserId = jwtTokenProvider.getUserId();
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		if (!gathering.getManagerId().equals(currentUserId)) {
			throw new RuntimeException("총무만 회원을 삭제할 수 있습니다.");
		}

		gatheringMemberRepo.deleteByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId, userId);
	}

	/**
	 * 모임 참여 요청을 수락 (총무 전용)
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      수락할 사용자 ID
	 * @throws RuntimeException 모임/사용자를 찾을 수 없거나, 총무가 아니거나, 이미 가입된 회원인 경우
	 */
	@Transactional
	public void acceptGathering(Long gatheringId, Long memberId, Long userId) { // 모임Id, 참가userId, 총무Id
		Gathering gathering = gatheringRepo.getGatheringByGatheringId(gatheringId);
		if (gathering.getManager().getUserId() != userId) {
			throw new RuntimeException("총무가 아닌데 접근함.");
		}
		GatheringMember gatheringMember =
				gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, memberId)
						.orElseThrow(() -> new RuntimeException("gatheringId와 userId로 gatheringMember를 찾을 수 없습니다."));

		// 참여 시킴
		gatheringMember.updateStatus(GatheringMemberStatus.ACTIVE);

		// 참여 하면 해당모임중 아직 시작되지 않은 일정에
		// 일정멤버 생성해줌. (이렇게 해야 그룹 참여시 미확인 일정에 나와서 수락 거절 할 수 있음.)
		User targetUser = userRepo.getUserByUserId(memberId);
		List<Schedule> schedules = gathering.getSchedules();
		LocalDateTime nowDateTime = LocalDateTime.now();
		for (Schedule schedule : schedules) {
			// 이미 지난 스케줄은 확인 했다고 생성
			// %%%%%%%%%%주의 날짜가 지났을떄 수락거절 버튼이 떠있을것 같음%%%%%%%%%%%%%%%% 일단 진행중
			if (nowDateTime.isAfter(schedule.getStartTime())) {
				ScheduleMember sm = ScheduleMember.builder()
						.schedule(schedule)
						.scheduleMember(targetUser)
						.scheduleIsCheck(true)
						.isPenaltyApply(false)
						.isAttend(false)
						.build();
				scheduleMemberRepo.save(sm);
			}
			// 안지난 스케줄은 확인 안했다고
			else {
				ScheduleMember sm = ScheduleMember.builder()
						.schedule(schedule)
						.scheduleMember(targetUser)
						.scheduleIsCheck(false)
						.isPenaltyApply(false)
						.isAttend(false)
						.build();
				scheduleMemberRepo.save(sm);
			}
		}
	}

	/**
	 * 모임 참여 요청을 거부 (총무 전용)
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      수락할 사용자 ID
	 * @throws RuntimeException 모임/사용자를 찾을 수 없거나, 총무가 아니거나, 이미 가입된 회원인 경우
	 */
	@Transactional
	public void rejectGathering(Long gatheringId, Long memberId, Long userId) { // 모임Id, 참가userId, 총무Id
		Gathering gathering = gatheringRepo.getGatheringByGatheringId(gatheringId);
		if (gathering.getManager().getUserId() != userId) {
			throw new RuntimeException("총무가 아닌데 접근함.");
		}
		GatheringMember gatheringMember =
				gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, memberId)
						.orElseThrow(() -> new RuntimeException("gatheringId와 userId로 gatheringMember를 찾을 수 없습니다."));

		gatheringMember.updateStatus(GatheringMemberStatus.REJECTED);
	}

	/**
	 * 모임 회원 관리 정보를 조회 (총무 전용) 회원 목록, 납부 현황 등의 상세 정보를 포함
	 *
	 * @param gatheringId 모임 ID
	 * @return 회원 관리 정보를 담은 DTO
	 * @throws RuntimeException 모임을 찾을 수 없거나 총무가 아닌 경우
	 */
	public MemberManageResponseDTO getMemberManage(Long gatheringId) {
		Long currentUserId = jwtTokenProvider.getUserId();
//		Long currentUserId = 1L;
		// gatheringMember찾기
		GatheringMember gatheringMember =
				gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, currentUserId)
						.orElseThrow(() -> new RuntimeException("gatheringId와 userId로 gahteringMember를 찾을 수 없습니다"));
		// 프론트 실수로 총무가 아닌 사람이 관리 들어왔을때
		if (!gatheringMember.getGathering().getManagerId().equals(currentUserId)) {
			throw new RuntimeException("총무만 회원 관리를 조회할 수 있습니다.");
		}
		List<GatheringMember> members = gatheringMember.getGathering().getGatheringMembers();
		Gathering gathering = gatheringRepo.getGatheringByGatheringId(gatheringId);
		Long deposit = gathering.getGatheringDeposit();


		User manager = gatheringMember.getGathering().getManager();
		boolean isManager = Objects.equals(manager.getUserId(), currentUserId);
		// 초대된 회원 목록
		List<MemberManageResponseDTO.InviteMemberDTO> inviteMemberDTO = members.stream()
				// 총무는 빼고 보여줌
				.filter(member -> {
					System.out.println(member.toString());
					if (!Objects.equals(member.getGatheringMemberUser().getUserId(), currentUserId)
							&& member.getGatheringMemberStatus() == GatheringMemberStatus.PENDING) {
						return true;
					}
					return false;
				})

				.map(member -> {
					User user = member.getGatheringMemberUser();
					return MemberManageResponseDTO.InviteMemberDTO.builder()
							.id(user.getUserId())
							.name(user.getName())
							.email(user.getEmail())
							.createdAt(user.getCreatedAt())
							.build();
				})
				.collect(Collectors.toList());

		// 총무 정보 DTO 생성
		MemberManageResponseDTO.ManagerDTO managerDTO = MemberManageResponseDTO.ManagerDTO.builder()
				.name(manager.getName())
				.email(manager.getEmail())
				.createdAt(manager.getCreatedAt())
				.balance(gatheringMember.getGatheringMemberAccountBalance())
				.gatheringPaymentStatus(true)
				.build();

		// 회원 목록 DTO 생성
		List<MemberManageResponseDTO.MemberDTO> memberDTOs = members.stream()
				// 총무는 빼고 보여줌
				.filter(member -> {
					System.out.println(member.toString());
					if (!Objects.equals(member.getGatheringMemberUser().getUserId(), currentUserId)
							&& member.getGatheringMemberStatus() == GatheringMemberStatus.ACTIVE) {
						return true;
					}
					return false;
				})

				.map(member -> {
					User user = member.getGatheringMemberUser();
					return MemberManageResponseDTO.MemberDTO.builder()
							.name(user.getName())
							.email(user.getEmail())
							.createdAt(user.getCreatedAt())
							.balance(member.getGatheringMemberAccountBalance())
							.gatheringPaymentStatus(member.getGatheringPaymentStatus())
							.build();
				})
				.collect(Collectors.toList());

		return MemberManageResponseDTO.builder()
				.inviteList(inviteMemberDTO)
				.manager(managerDTO)
				.memberList(memberDTOs)
				.isManager(isManager)
				.myDeposit(deposit)
				.build();
	}

	/**
	 * 모임 회원 목록 조회 (모든 사용자)
	 *
	 * @param gatheringId 모임 ID
	 * @return 회원 목록 정보를 담은 DTO
	 * @throws RuntimeException 모임을 찾을 수 없는 경우
	 */
	public MemberListResponseDTO getMembers(Long gatheringId, Long userId) {
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		List<GatheringMember> members = gatheringMemberRepo.findByGatheringGatheringId(gatheringId);
		User manager = userRepo.findById(gathering.getManagerId())
				.orElseThrow(() -> new RuntimeException("총무 정보를 찾을 수 없습니다."));

		// 총무 정보 DTO 생성
		MemberListResponseDTO.ManagerDTO managerDTO = MemberListResponseDTO.ManagerDTO.builder()
				.name(manager.getName())
				.email(manager.getEmail())
				.createdAt(manager.getCreatedAt())
				.build();
		int memberCount = 1;
		// 회원 목록 DTO 생성
		List<MemberListResponseDTO.MemberDTO> memberDTOs = members.stream()
				.filter(member -> {
					if (!Objects.equals(member.getGatheringMemberUser().getUserId(), manager.getUserId())
							&& member.getGatheringMemberStatus() == GatheringMemberStatus.ACTIVE) {
						return true;
					}
					return false;
				})
				.map(member -> {
					User user = userRepo.findById(member.getGatheringMemberUser().getUserId())
							.orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
					return MemberListResponseDTO.MemberDTO.builder()
							.name(user.getName())
							.email(user.getEmail())
							.createdAt(user.getCreatedAt())
							.build();
				})
				.collect(Collectors.toList());

		return MemberListResponseDTO.builder()
				.memberCount(memberDTOs.size() + 1)
				.manager(managerDTO)
				.members(memberDTOs)
				.build();
	}

	/**
	 * 모임탈퇴 (총무 제외 모든 사용자)
	 *
	 * @param gatheringId 모임 ID
	 * @throws RuntimeException 모임을 찾을 수 없거나, 총무이거나, 회원이 아닌 경우
	 */
	@Transactional
	public void leaveGathering(Long gatheringId, Long userId) {
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		if (gathering.getManagerId().equals(userId)) {
			throw new RuntimeException("총무는 모임을 탈퇴할 수 없습니다.");
		}

		if (!gatheringMemberRepo.existsByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId,
				userId)) {
			throw new RuntimeException("모임 회원이 아닙니다.");
		}

		/**
		 * 해당 모임의 일정중 내가 참여한 일정 찾기
		 */
		GatheringMember gatheringMember = gatheringMemberRepo.getGatheringMemberByGatheringIdAndUserId(gatheringId, userId)
				.orElseThrow(() -> new RuntimeException("gatheringId, userId로 gatheringMember를 찾을 수 없습니다."));
		List<Schedule> schedules = gatheringMember.getGathering().getSchedules();

		Long totalGivebackAmount = 0L;
		totalGivebackAmount += gatheringMember.getGatheringMemberAccountDeposit();
		totalGivebackAmount += gatheringMember.getGatheringMemberAccountBalance();

		// 모임의 모든 일정에 모든 일정사람들을 살펴본다.
		for (Schedule schedule : schedules) {
			Long paybackAmount = 0L;
			for (ScheduleMember sm : schedule.getAttendees()) {
				// 일점을 참여 혹은 거절을 눌렀고,
				// 참여를 눌렀거나, 페널티 적용이 된 일정 찾기.
				if (sm.getScheduleMember().getUserId().equals(userId)) {
					if (sm.getScheduleIsCheck() && (sm.getIsAttend() || sm.isPenaltyApply())) {
						LocalDateTime now = LocalDateTime.now();
						LocalDateTime paybackDate = schedule.getPenaltyApplyDate();
						LocalDateTime startDate = schedule.getStartTime();
						int paybackRate = schedule.getPenaltyRate();
						Long perBudget = schedule.getPerBudget();

						//	&& now.isBefore(startDate) 추가 할까말까 고민함.
						//	isAttend가 일정끝날때 false로 바뀌면 추가 안해도 되고
						//	안바뀌게해놓으면 위에 조건 추가하고 일정시작날짜와 일정마감누를때까지의 기간에 특수한 로직이 필요함.
						if (now.isAfter(paybackDate)) {
							paybackAmount += (perBudget * paybackRate) / 100;
						} else {
							paybackAmount += perBudget;
						}
					}
					// 내 sm만 삭제
					scheduleMemberRepo.delete(sm);
				}
			}
			// 일정계좌에서 빼줌. 밑에 transfer에서 해주는듯
			schedule.getScheduleAccount().decreaseBalance(paybackAmount);
			totalGivebackAmount += paybackAmount;
		}

		TransferTransactionHistoryDTO dto = gatheringAccountService.initTransfer(TransferRequestDTO.builder()
						.fromAccountType(AccountType.GATHERING)
						.fromAccountId(gathering.getGatheringAccount().getAccountId())
						.toAccountType(AccountType.PERSONAL)
						.toAccountNo(gatheringMember.getGatheringMemberUser().getPersonalAccount().getAccountNo())
						.tradeDetail(gathering.getGatheringName() + " 탈퇴")
						.transferAmount(totalGivebackAmount)
						.accountPw(gathering.getGatheringAccount().getAccountPw())
						.build());
		boolean isTransfer = gatheringAccountService.processTransfer(dto);

		gatheringMemberRepo.delete(gatheringMember);
	}

	/**
	 * 모임 멤버 추가 (모임 생성할때 최초로 총무 추가하는 경우)
	 *
	 * @param gatheringId 모임 ID
	 * @param userId      추가할 사용자 ID
	 * @throws RuntimeException 모임을 찾을 수 없거나, 사용자를 찾을 수 없거나, 이미 가입된 회원인 경우
	 */
	@Transactional
	public void addMember(Long gatheringId, Long userId) {
		// 모임 조회
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		// 사용자 조회
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

		// 이미 가입된 회원인지 확인
		if (gatheringMemberRepo.existsByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId, userId)) {
			throw new RuntimeException("이미 가입된 회원입니다.");
		}

		// 새로운 회원 정보 생성
		GatheringMember member = GatheringMember.builder()
				.gathering(gathering)
				.gatheringMemberUser(user)
				.gatheringAttendCount(0)
				.gatheringMemberAccountBalance(0L)
				.gatheringMemberAccountDeposit(0L)
				.gatheringPaymentStatus(false)
				.gatheringMemberStatus(GatheringMemberStatus.ACTIVE)
				.build();

		// 회원 저장
		gatheringMemberRepo.save(member);

		// 모임의 회원 수 업데이트
		long memberCount = gatheringMemberRepo.countByGatheringGatheringId(gatheringId);
		gathering.updateMemberCount((int) memberCount);
	}

	public GatheringAcceptPageResponseDTO gatheringAccpetPage(Long gatheringId) {
		Gathering gathering = gatheringRepo.getGatheringByGatheringId(gatheringId);
		return GatheringAcceptPageResponseDTO.builder()
				.name(gathering.getGatheringName())
				.introduction(gathering.getGatheringIntroduction())
				.memberCount(gathering.getGatheringMembers().size())
				.build();
	}

	@Transactional
	public GatheringJoinResponseDTO requestGatheringJoin(Long userId, Long gatheringId) {
		Gathering gathering = gatheringRepo.getGatheringByGatheringId(gatheringId);
		User user = userRepo.getUserByUserId(userId);

		List<GatheringMember> gatheringMembers = gathering.getGatheringMembers();

		for (GatheringMember gatheringMember : gatheringMembers) {
			if (Objects.equals(gatheringMember.getGatheringMemberUser().getUserId(), userId)) {
				throw new RuntimeException("이미 신청했습니다");
			}
		}

		GatheringMember gatheringMember =
				GatheringMember.builder()
						.gathering(gathering)
						.gatheringMemberUser(user)
						.gatheringAttendCount(0)
						.gatheringMemberAccountBalance(0L)
						.gatheringMemberAccountDeposit(0L)
						.gatheringPaymentStatus(false)
						.gatheringMemberStatus(GatheringMemberStatus.PENDING)
						.build();
		gatheringMemberRepo.save(gatheringMember);

		// 자동이체 등록
		autoPaymentService.createAutoPaymentForGatheringMember(gatheringMember);

		return GatheringJoinResponseDTO.builder()
				.isSave(true)
				.build();
	}
}