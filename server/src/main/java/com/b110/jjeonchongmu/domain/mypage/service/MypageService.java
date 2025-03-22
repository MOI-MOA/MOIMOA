package com.b110.jjeonchongmu.domain.mypage.service;

import com.b110.jjeonchongmu.domain.mypage.dto.*;
import com.b110.jjeonchongmu.domain.mypage.repo.MypageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {
    
    private final MypageRepo mypageRepo;

    public List<AutoPaymentResponse> getAutoPayments(String type) {
        return mypageRepo.findAutoPaymentsByType(type);
    }

    public MyPageResponse getMyPage() {
        Long userId = getCurrentUserId();
        return mypageRepo.findMyPageInfo(userId);
    }

    public StatisticsResponse getStatistics() {
        Long userId = getCurrentUserId();
        
        Long totalExpenses = mypageRepo.findTotalExpenses(userId);
        List<Object[]> gatheringStats = mypageRepo.findGatheringStatistics(userId);
        List<Object[]> monthlyExpenses = mypageRepo.findMonthlyExpenses(userId);
        
        StatisticsResponse.StatisticsData statisticsData = calculateOverallStatistics(totalExpenses, gatheringStats);
        List<StatisticsResponse.GatheringStatistics> gatheringStatistics = convertGatheringStatistics(gatheringStats);
        List<StatisticsResponse.MonthlyStatistics> monthlyStatistics = convertMonthlyStatistics(monthlyExpenses);
        
        return StatisticsResponse.builder()
                .data(statisticsData)
                .datas(gatheringStatistics)
                .month(monthlyStatistics)
                .build();
    }

    private Long getCurrentUserId() {
        // SecurityContext에서 현재 사용자 ID를 가져오는 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private StatisticsResponse.StatisticsData calculateOverallStatistics(Long totalExpenses, List<Object[]> gatheringStats) {
        // 전체 통계 계산 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private List<StatisticsResponse.GatheringStatistics> convertGatheringStatistics(List<Object[]> gatheringStats) {
        // 모임별 통계 변환 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private List<StatisticsResponse.MonthlyStatistics> convertMonthlyStatistics(List<Object[]> monthlyExpenses) {
        // 월별 통계 변환 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
