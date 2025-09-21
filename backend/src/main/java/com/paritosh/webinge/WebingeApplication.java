package com.paritosh.webinge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebingeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebingeApplication.class, args);
	}

}
