package com.b110.jjeonchongmu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.b110.jjeonchongmu.domain")
public class JjeonchongmuApplication {

	public static void main(String[] args) {
		SpringApplication.run(JjeonchongmuApplication.class, args);
	}
}
