package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoPaymentRepo extends JpaRepository<AutoPayment, Long> {

}