package com.github.llm_redis_stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LlmRedisStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(LlmRedisStreamApplication.class, args);
	}

}
