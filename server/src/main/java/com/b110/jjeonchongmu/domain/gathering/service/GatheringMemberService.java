package com.b110.jjeonchongmu.domain.gathering.service;


import com.b110.jjeonchongmu.domain.gathering.dto.InviteResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberListResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.dto.MemberManageResponseDTO;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.entity.GatheringMember;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringMemberRepo;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import com.b110.jjeonchongmu.global.security.JwtTokenProvider;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
	public void acceptGathering(Long gatheringId, Long userId) {
		Long currentUserId = jwtTokenProvider.getUserId();
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));
		User user = userRepo.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원정보를 찾을 수 없습니다."));
		if (!gathering.getManagerId().equals(currentUserId)) {
			throw new RuntimeException("총무만 참여를 수락할 수 있습니다.");
		}

		// 사용자가 존재하는지 확인
		userRepo.findById(userId)
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

		if (gatheringMemberRepo.existsByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId,
				userId)) {
			throw new RuntimeException("이미 가입된 회원입니다.");
		}

		// 새로운 회원 정보 생성
		GatheringMember member = GatheringMember.builder()
				.gathering(gathering)
				.gatheringMemberId(user.getUserId())
				.gatheringAttendCount(0)
				.gatheringMemberAccountBalance(0)
				.gatheringMemberAccountDeposit(0)
				.gatheringPaymentStatus(false)
				.build();

		gatheringMemberRepo.save(member);
	}

	/**
	 * 모임 회원 관리 정보를 조회 (총무 전용) 회원 목록, 납부 현황 등의 상세 정보를 포함
	 *
	 * @param gatheringId 모임 ID
	 * @return 회원 관리 정보를 담은 DTO
	 * @throws RuntimeException 모임을 찾을 수 없거나 총무가 아닌 경우
	 */
	public MemberManageResponseDTO getMemberManage(Long gatheringId) {
//		Long currentUserId = jwtTokenProvider.getUserId();
		Long currentUserId = 1L;
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));


		if (!gathering.getManagerId().equals(currentUserId)) {
			throw new RuntimeException("총무만 회원 관리를 조회할 수 있습니다.");
		}

		List<GatheringMember> members = gatheringMemberRepo.findByGatheringGatheringId(gatheringId);
		User manager = userRepo.findById(gathering.getManagerId())
				.orElseThrow(() -> new RuntimeException("총무 정보를 찾을 수 없습니다."));

		// 총무 정보 DTO 생성
		MemberManageResponseDTO.ManagerDTO managerDTO = MemberManageResponseDTO.ManagerDTO.builder()
				.name(manager.getName())
				.email(manager.getEmail())
				.createdAt(manager.getCreatedAt())
				.gatheringPaymentStatus(true)
				.build();

		// 회원 목록 DTO 생성
		List<MemberManageResponseDTO.MemberDTO> memberDTOs = members.stream()
				.map(member -> {
					User user = userRepo.findById(member.getGatheringMemberUser().getUserId())
							.orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
					return MemberManageResponseDTO.MemberDTO.builder()
							.name(user.getName())
							.email(user.getEmail())
							.createdAt(user.getCreatedAt())
							.gatheringPaymentStatus(member.isGatheringPaymentStatus())
							.build();
				})
				.collect(Collectors.toList());

		return MemberManageResponseDTO.builder()
				.memberCount(members.size())
				.paymentCount(
						(int) members.stream().filter(GatheringMember::isGatheringPaymentStatus).count())
				.inviteCount(0)
				.inviteList(List.of())
				.manager(managerDTO)
				.memberList(memberDTOs)
				.build();
	}

	/**
	 * 모임 회원 목록 조회 (모든 사용자)
	 *
	 * @param gatheringId 모임 ID
	 * @return 회원 목록 정보를 담은 DTO
	 * @throws RuntimeException 모임을 찾을 수 없는 경우
	 */
	public MemberListResponseDTO getMembers(Long gatheringId) {
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

		// 회원 목록 DTO 생성
		List<MemberListResponseDTO.MemberDTO> memberDTOs = members.stream()
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
				.memberCount(members.size())
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
	public void leaveGathering(Long gatheringId) {
		Long userId = jwtTokenProvider.getUserId();
		Gathering gathering = gatheringRepo.findById(gatheringId)
				.orElseThrow(() -> new RuntimeException("모임을 찾을 수 없습니다."));

		if (gathering.getManagerId().equals(userId)) {
			throw new RuntimeException("총무는 모임을 탈퇴할 수 없습니다.");
		}

		if (!gatheringMemberRepo.existsByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId,
				userId)) {
			throw new RuntimeException("모임 회원이 아닙니다.");
		}

		gatheringMemberRepo.deleteByGatheringGatheringIdAndGatheringMemberUser_UserId(gatheringId, userId);
	}
}