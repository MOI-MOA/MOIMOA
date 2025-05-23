package com.b110.jjeonchongmu.domain.account.repo;

import com.b110.jjeonchongmu.domain.account.entity.PersonalAccount;
import com.b110.jjeonchongmu.domain.account.entity.ScheduleAccount;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleAccountRepo extends JpaRepository<ScheduleAccount, Long> {

	@Query("SELECT a FROM Account a WHERE a.accountId = :accountId AND TYPE(a) = ScheduleAccount ")
	Optional<ScheduleAccount> findByAccount(Long accountId);

	@Query("SELECT a FROM ScheduleAccount a WHERE a.user.userId = :userId AND a.schedule.id =:scheduleId")
	Optional<ScheduleAccount> findScheduleAccountByUserIdAndScheduleId(@Param("userId") Long userId , @Param("scheduleId") Long scheduleId);


}