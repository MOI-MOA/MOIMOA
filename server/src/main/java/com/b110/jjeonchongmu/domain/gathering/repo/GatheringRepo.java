package com.b110.jjeonchongmu.domain.gathering.repo;

import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GatheringRepo extends JpaRepository<Gathering, Long> {


	/**
	 * 모임 ID로 모임 조회
	 */
	Optional<Gathering> findById(Long gatheringId);
	/**
	 * 모임 계좌 ID로 모임 정보 조회
	 */
	@Query("SELECT g FROM Gathering g WHERE g.gatheringAccount.accountId = :accountId")
	Optional<Gathering> findByGatheringAccountId(@Param("accountId") Long accountId);

	/**
	 * 총무 ID로 모임 목록 조회
	 */
	List<Gathering> findByManager_UserId(Long managerId);

	/**
	 * 모임명으로 모임 검색
	 */
	List<Gathering> findByGatheringNameContaining(String gatheringName);

	/**
	 * 모임 ID와 총무 ID로 모임 조회
	 */
	Optional<Gathering> findByGatheringIdAndManager_UserId(Long gatheringId, Long managerId);

	/**
	 * 모임 계좌 잔액 조회
	 */
	@Query("SELECT g.gatheringDeposit FROM Gathering g WHERE g.gatheringId = :gatheringId")
	Integer findGatheringDepositById(@Param("gatheringId") Long gatheringId);

	/**
	 * 모임 기본 정보 존재 여부 확인
	 */
	boolean existsByGatheringIdAndManager_UserId(Long gatheringId, Long userId);

	/**
	 * 모임명과 총무 ID로 모임 존재 여부 확인
	 */
	boolean existsByGatheringNameAndManager_UserId(String gatheringName, Long managerId);

	/**
	 * 모임 ID로 존재 여부 확인
	 */
	boolean existsById(Long gatheringId);


	Gathering getGatheringByGatheringId(Long gatheringId);
}