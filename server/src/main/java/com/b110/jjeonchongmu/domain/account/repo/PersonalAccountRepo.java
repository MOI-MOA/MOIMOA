package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalAccountRepo extends JpaRepository<PersonalAccount, Long> {

	@Query("SELECT a FROM Account a WHERE a.accountId = :accountId AND TYPE(a) = PersonalAccount")
	Optional<PersonalAccount> findByAccount(Long accountId);

	@Query("SELECT a FROM Account a WHERE a.user.userId = :userId AND TYPE(a) = PersonalAccount")
	Optional<PersonalAccount> findByUserId(Long userId);

	PersonalAccount findByAccountNo(String accountNo);
}