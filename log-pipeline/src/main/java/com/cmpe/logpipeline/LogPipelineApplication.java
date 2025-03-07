package com.cmpe.logpipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Required for @Scheduled methods
public class LogPipelineApplication {
	public static void main(String[] args) {
		SpringApplication.run(LogPipelineApplication.class, args);
	}
}

