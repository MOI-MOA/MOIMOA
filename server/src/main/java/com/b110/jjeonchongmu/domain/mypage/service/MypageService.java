package com.b110.jjeonchongmu.domain.mypage.service;

import com.b110.jjeonchongmu.domain.account.entity.AutoPayment;
import com.b110.jjeonchongmu.domain.account.repo.AutoPaymentRepo;
import com.b110.jjeonchongmu.domain.gathering.entity.Gathering;
import com.b110.jjeonchongmu.domain.gathering.repo.GatheringRepo;
import com.b110.jjeonchongmu.domain.mypage.dto.*;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.AutoPaymentDto;
import com.b110.jjeonchongmu.domain.mypage.dto.auto.UpdateAutoPaymentRequestDto;
import com.b110.jjeonchongmu.domain.mypage.dto.profile.ProfileDefaultResponse;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.GroupExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.MonthlyExpenseData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.ParticipationRateData;
import com.b110.jjeonchongmu.domain.mypage.dto.statistics.StatisticsResponse;
import com.b110.jjeonchongmu.domain.mypage.repo.MypageRepo;
import com.b110.jjeonchongmu.domain.schedule.entity.Schedule;
import com.b110.jjeonchongmu.domain.schedule.entity.ScheduleMember;
import com.b110.jjeonchongmu.domain.schedule.repo.ScheduleRepo;
import com.b110.jjeonchongmu.domain.user.entity.User;
import com.b110.jjeonchongmu.domain.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

    private final MypageRepo mypageRepo;
    private final UserRepo userRepo;
    private final ScheduleRepo scheduleRepo;
    private final GatheringRepo gatheringRepo;
    private final AutoPaymentRepo autoPaymentRepo;

    public List<AutoPaymentDto> getAutoPayments(String type) {
        return null;
    }

    public MyPageResponse getMyPage(Long userId) {
        return mypageRepo.findMyPageInfo(userId);
    }

    public StatisticsResponse getStatistics(Long userId) {

        User user = userRepo.getUserByUserId(userId);
        LocalDate today = LocalDate.now();
        int endYear = today.getYear();
        int endMonth = today.getMonthValue();
        int startMonth = endMonth - 6;
        int startYear = endYear;
        if (startMonth <= 0) {
            startMonth += 12;
            startYear += 1;
        }
        YearMonth startYearMonth = YearMonth.of(startYear, startMonth);
        YearMonth endYearMonth = YearMonth.of(endYear, endMonth);
        LocalDateTime start = startYearMonth.atDay(1).atStartOfDay(); // ex) 2025-02-01 00:00:00
        LocalDateTime end = endYearMonth.atEndOfMonth().atTime(23, 59, 59); // ex) 2025-02-28 23:59:59

        // ###################################
        List<MonthlyExpenseData> monthlyExpenseDatas =
                scheduleRepo.findMonthlyExpenseDataByUserIdAndDateBetween(
                        userId, start, end);

        List<GroupExpenseData> groupExpenseDatas = scheduleRepo.findGroupExpensesByUserId(
                userId);

        List<ParticipationRateData> participationRateDatas = new ArrayList<>();
        // ###################################

        List<Gathering> gatherings = gatheringRepo.findByManager_UserId(userId);

        for (Gathering gathering : gatherings) {
            int participate = 0;
            int scheduleSize = 0;
            List<Schedule> schedules = gathering.getSchedules();
            scheduleSize = schedules.size();
            for (Schedule schedule : schedules) {
                List<ScheduleMember> scheduleMembers = schedule.getAttendees();
                for (ScheduleMember scheduleMember : scheduleMembers) {
                    if (scheduleMember.getScheduleMember().getUserId().equals(userId)) {
                        participate++;
                        break;
                    }
                }
            }
            String name = gathering.getGatheringName();
            ParticipationRateData participationRateData = new ParticipationRateData(
                    name, participate, scheduleSize
            );
            participationRateDatas.add(participationRateData);
        }
        return new StatisticsResponse(monthlyExpenseDatas, groupExpenseDatas, participationRateDatas);
    }

    private Long getCurrentUserId() {
        // SecurityContext에서 현재 사용자 ID를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public AutoPaymentResponse getAutoPaymentResponseByUserId(Long id) {
        User user = userRepo.getUserByUserId(id);
        return new AutoPaymentResponse(user);
    }

    public ProfileDefaultResponse getProfileDefaultByUserId(Long id) {
        User user = userRepo.getUserByUserId(id);

        return new ProfileDefaultResponse(user);
    }

    public void updateAutoTransfer(Long id, Long autoPaymentId, UpdateAutoPaymentRequestDto requestDto) {
        User user = userRepo.getUserByUserId(id);

        AutoPayment autoPayment = autoPaymentRepo.findById(autoPaymentId)
                .orElseThrow(() -> new RuntimeException("autoPayment를 autoPaymentId로 찾을 수 없습니다"));

        if (!autoPayment.getPersonalAccount().getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("유저가 자동이체의 주인이 아닙니다.");
        }

        int amount = requestDto.getAmount();
        int day = requestDto.getDay();
        boolean status = false;
        if (requestDto.getStatus().equals("active")) {
            status = true;
        } else if (requestDto.getStatus().equals("inactive")) {
            status = false;
        } else {
            throw new RuntimeException("자동이체의 활성화 여부가 active 또는 inactive로 오지 않았습니다");
        }

        autoPayment.updateAutoPaymentAmount(amount);
        autoPayment.updateAutoPaymentDate(day);
        autoPayment.updateIsActive(status);
    }
}
