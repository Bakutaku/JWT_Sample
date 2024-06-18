package com.example.jwt_sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // スケジュール機能の有効化
@SpringBootApplication
public class JwtSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtSampleApplication.class, args);
	}

}
