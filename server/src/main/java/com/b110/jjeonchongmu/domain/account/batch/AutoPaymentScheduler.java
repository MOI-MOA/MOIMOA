package com.b110.jjeonchongmu.domain.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPaymentScheduler {

    private final JobLauncher jobLauncher;
    private final Job autoPaymentJob;

    /**
     * 매일 특정시간에  자동이체 배치 작업을 실행
     */
//    @Scheduled(cron = "0 * * * * ?")  // 매 분마다 실행 (테스트용)
    @Scheduled(cron = "0 30 15 * * ?")  // 15시 30분 실행 (테스트용)

    public void runAutoPaymentJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            
            log.info("자동이체 스케줄러 실행");
            jobLauncher.run(autoPaymentJob, jobParameters);
            log.info("자동이체 스케줄러 완료");
        } catch (Exception e) {
            log.error("자동이체 스케줄러 실행 중 오류 발생: {}", e.getMessage());
        }
    }
} 