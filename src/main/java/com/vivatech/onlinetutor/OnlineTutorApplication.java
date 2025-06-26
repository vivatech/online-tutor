package com.vivatech.onlinetutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnlineTutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineTutorApplication.class, args);
	}

}
