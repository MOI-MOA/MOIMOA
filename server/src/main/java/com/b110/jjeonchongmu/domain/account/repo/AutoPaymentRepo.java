package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPaymentRepo extends JpaRepository<AutoPayment, Long> {

	@Query("select ap from AutoPayment ap where ap.personalAccount.user.userId = :userId")
	List<AutoPayment> findByUserId(Long userId);
}