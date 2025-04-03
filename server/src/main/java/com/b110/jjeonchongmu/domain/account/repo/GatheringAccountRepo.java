package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.GatheringAccount;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GatheringAccountRepo extends JpaRepository<GatheringAccount, Long> {

	@Query("SELECT a FROM Account a WHERE a.accountId = :accountId AND TYPE(a) = GatheringAccount")
	Optional<GatheringAccount> findByAccount(Long accountId);

	GatheringAccount findAccountByAccountNo(String toAccountNo);

	Boolean existsByAccountNo(String toAccountNo);
}