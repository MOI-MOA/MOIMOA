package com.b110.jjeonchongmu.domain.mypage.repo;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.MyPageResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MypageRepo extends JpaRepository<AutoPayment, Long> {

    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.MyPageResponse(" +
           "u.userId, u.name, u.email) " +
           "FROM User u " +
           "WHERE u.userId = :userId")
    MyPageResponse findMyPageInfo(@Param("userId") Long userId);
} 