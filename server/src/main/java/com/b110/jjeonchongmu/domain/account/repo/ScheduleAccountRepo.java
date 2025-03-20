package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.entity.ScheduleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleAccountRepo extends JpaRepository<ScheduleAccount, Long> {

    // 계좌 ID로 계좌 조회
    Optional<ScheduleAccount> findById(Long ScheduleAccountId);

    // 특정 사용자의 모든 계좌 조회
    List<ScheduleAccount> findByUserId(Long userId);

    // 계좌 비밀번호 확인 (비밀번호가 맞는지 체크)
    Optional<ScheduleAccount> findByIdAndPassword(Long ScheduleAccountId, String password);

    // 특정 계좌가 존재하는지 확인
    boolean existsById(Long ScheduleAccountId);

    // 계좌 삭제
    void deleteById(Long ScheduleAccountId);
}