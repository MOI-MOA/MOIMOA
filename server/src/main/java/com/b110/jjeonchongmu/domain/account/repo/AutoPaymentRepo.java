package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPaymentRepo extends JpaRepository<AutoPayment, Long> {
	List<AutoPayment> findByPersonalAccountAndIsActiveTrue(PersonalAccount personalAccount);
	List<AutoPayment> findByGatheringAccountAndIsActiveTrue(GatheringAccount gatheringAccount);
	Optional<AutoPayment> findByPersonalAccountAndGatheringAccount(PersonalAccount personalAccount, GatheringAccount gatheringAccount);

	@Query("SELECT a FROM AutoPayment a WHERE a.personalAccount.user.userId = :userId")
	List<AutoPayment> findByUserId(@Param("userId") Long userId);
	
	/**
	 * 활성화된 자동이체 중 특정 날짜에 처리해야 할 자동이체 조회
	 */
	@Query("SELECT a FROM AutoPayment a WHERE a.autoPaymentDate = :dayOfMonth AND a.isActive = true")
	List<AutoPayment> findByIsActiveTrueAndAutoPaymentDate(@Param("dayOfMonth") int dayOfMonth);
}