package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPaymentRepo extends JpaRepository<AutoPayment, Long> {
	List<AutoPayment> findByPersonalAccountAndIsActiveTrue(PersonalAccount personalAccount);
	List<AutoPayment> findByGatheringAccountAndIsActiveTrue(GatheringAccount gatheringAccount);
	Optional<AutoPayment> findByPersonalAccountAndGatheringAccount(PersonalAccount personalAccount, GatheringAccount gatheringAccount);

}