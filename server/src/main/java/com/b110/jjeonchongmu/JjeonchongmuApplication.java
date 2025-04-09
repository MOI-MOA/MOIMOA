package com.b110.jjeonchongmu;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
@EntityScan(basePackages = "com.b110.jjeonchongmu.domain")
public class JjeonchongmuApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjeonchongmuApplication.class, args);
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
