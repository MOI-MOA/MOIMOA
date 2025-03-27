package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepo extends JpaRepository<Account, Long> {


	Boolean existsByAccountNo(String toAccountNo);

	Account findAccountByAccountNo(String accountNo);
}
