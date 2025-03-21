package com.b110.jjeonchongmu.domain.main.service;

import com.b110.jjeonchongmu.domain.main.dto.response.*;
import com.b110.jjeonchongmu.domain.main.repository.MainRepository;
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

    private final MainRepository mainRepository;

    public MainHomeResponse getMainHome() {
        int uncheckCount = mainRepository.countUncheckSchedules();
        List<MainHomeResponse.DateDto> dateList = mainRepository.findCurrentMonthDates().stream()
                .map(date -> MainHomeResponse.DateDto.builder().date(date).build())
                .collect(Collectors.toList());
        List<ScheduleDto> todaySchedules = mainRepository.findTodaySchedules();
        List<ScheduleDto> upcomingSchedules = mainRepository.findUpcomingSchedules();

        return MainHomeResponse.builder()
                .uncheckScheduleCount(uncheckCount)
                .dateList(dateList)
                .todayScheduleList(todaySchedules)
                .upcommingScheduleList(upcomingSchedules)
                .build();
    }

    public ScheduleListResponse getUncheckSchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepository.findUncheckSchedules())
                .build();
    }

    public ScheduleListResponse getPersonalSchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepository.findPersonalSchedules())
                .build();
    }

    public MonthlyScheduleResponse getMonthlySchedules(int year, int month) {
        List<MonthlyScheduleResponse.DateDto> dates = mainRepository.findScheduleDatesForMonth(year, month).stream()
                .map(date -> MonthlyScheduleResponse.DateDto.builder().date(date).build())
                .collect(Collectors.toList());
        
        return MonthlyScheduleResponse.builder()
                .datas(dates)
                .build();
    }

    public ScheduleListResponse getDailySchedules(int year, int month, int date) {
        LocalDate targetDate = LocalDate.of(year, month, date);
        return ScheduleListResponse.builder()
                .datas(mainRepository.findSchedulesByDate(targetDate))
                .build();
    }

    public ScheduleListResponse getGatheringSchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepository.findGatheringSchedules())
                .build();
    }

    public ScheduleListResponse getTodaySchedules() {
        return ScheduleListResponse.builder()
                .datas(mainRepository.findTodaySchedules())
                .build();
    }
}
