package com.b110.jjeonchongmu.domain.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AutoPaymentBatchConfig {

    private final AutoPaymentBatchService autoPaymentBatchService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job autoPaymentJob() {
        return new JobBuilder("autoPaymentJob", jobRepository)
                .start(autoPaymentStep())
                .build();
    }

    @Bean
    public Step autoPaymentStep() {
        return new StepBuilder("autoPaymentStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("자동이체 배치 작업 시작");
                    
                    // 자동이체 배치 작업 실행
                    autoPaymentBatchService.processAutoPayments();
                    
                    log.info("자동이체 배치 작업 완료");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
} 