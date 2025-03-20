package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.entity.PersonalAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalAccountRepo extends JpaRepository<PersonalAccount, Long> {

    // 계좌 ID로 계좌 조회
    Optional<PersonalAccount> findById(Long PersonalAccountId);

    // 특정 사용자의 모든 계좌 조회
    List<PersonalAccount> findByUserId(Long userId);


    // 송금내역 거래테이블에 추가

    //

    // 계좌 비밀번호 확인 (비밀번호가 맞는지 체크)
    Optional<PersonalAccount> findByIdAndPassword(Long PersonalAccountId, String password);


    // 특정 계좌가 존재하는지 확인
    boolean existsById(Long PersonalAccountId);

    // 계좌 삭제
    void deleteById(Long PersonalAccountId);
}