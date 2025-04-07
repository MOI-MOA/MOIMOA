package com.b110.jjeonchongmu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@EnableScheduling
@SpringBootApplication
public class JjeonchongmuApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjeonchongmuApplication.class, args);
	}
}
