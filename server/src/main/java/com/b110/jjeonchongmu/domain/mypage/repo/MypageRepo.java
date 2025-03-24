package com.b110.jjeonchongmu.domain.mypage.repo;

import com.b110.jjeonchongmu.domain.mypage.dto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.MyPageResponse;
import com.b110.jjeonchongmu.domain.mypage.entity.AutoPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MypageRepo extends JpaRepository<AutoPayment, Long> {
    
    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.response.AutoPaymentResponse(" +
           "ap.id, ap.amount, ap.paymentDate, ap.isActive, " +
           "g.name, g.deposit, ga.accountNo, ga.balance) " +
           "FROM AutoPayment ap " +
           "JOIN ap.gathering g " +
           "JOIN g.account ga " +
           "WHERE ga.type = :type")
    List<AutoPaymentResponse> findAutoPaymentsByType(@Param("type") String type);

    @Query("SELECT new com.b110.jjeonchongmu.domain.mypage.dto.response.MyPageResponse(" +
           "u.id, u.name, u.email) " +
           "FROM User u " +
           "WHERE u.id = :userId")
    MyPageResponse findMyPageInfo(@Param("userId") Long userId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.user.id = :userId")
    Long findTotalExpenses(@Param("userId") Long userId);

    @Query("SELECT g.name, COUNT(s), COUNT(a), SUM(p.amount) " +
           "FROM Gathering g " +
           "LEFT JOIN g.schedules s " +
           "LEFT JOIN s.attendees a " +
           "LEFT JOIN g.payments p " +
           "WHERE g.id IN (SELECT m.gathering.id FROM Member m WHERE m.user.id = :userId) " +
           "GROUP BY g.id")
    List<Object[]> findGatheringStatistics(@Param("userId") Long userId);

    @Query("SELECT MONTH(p.paymentDate), SUM(p.amount) " +
           "FROM Payment p " +
           "WHERE p.user.id = :userId " +
           "GROUP BY MONTH(p.paymentDate)")
    List<Object[]> findMonthlyExpenses(@Param("userId") Long userId);
} 