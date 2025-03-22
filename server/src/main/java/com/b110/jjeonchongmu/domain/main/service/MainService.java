package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.*;
import com.b110.jjeonchongmu.domain.main.repo.MainRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

    private final MainRepo mainRepo;

    public MainHomeResponse getMainHome() {
        int uncheckCount = mainRepo.countUncheckSchedules();
        List<MainHomeResponse.DateDto> dateList = mainRepo.findCurrentMonthDates().stream()
                .map(date -> MainHomeResponse.DateDto.builder().date(date).build())
                .collect(Collectors.toList());
        List<ScheduleDto> todaySchedules = mainRepo.findTodaySchedules();
        List<ScheduleDto> upcomingSchedules = mainRepo.findUpcomingSchedules();

        return MainHomeResponse.builder()
                .uncheckScheduleCount(uncheckCount)
                .dateList(dateList)
                .todayScheduleList(todaySchedules)
                .upcommingScheduleList(upcomingSchedules)
                .build();
    }

    public ScheduleListResponse getUncheckSchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepo.findUncheckSchedules())
                .build();
    }

    public ScheduleListResponse getPersonalSchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepo.findPersonalSchedules())
                .build();
    }

    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {
        List<MonthlyScheduleResponse.DateDto> dates = mainRepo.findScheduleDatesForMonth(year, month).stream()
                .map(date -> MonthlyScheduleResponse.DateDto.builder().date(date).build())
                .collect(Collectors.toList());
        
        return MonthlyScheduleResponse.builder()
                .datas(dates)
                .build();
    }

    public ScheduleListResponse getDailySchedules(int year, int month, int date) {
        LocalDate targetDate = LocalDate.of(year, month, date);
        return ScheduleListResponse.builder()
                .datas(mainRepo.findSchedulesByDate(targetDate))
                .build();
    }

    public ScheduleListResponse getTodaySchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepo.findTodaySchedules())
                .build();
    }
}
