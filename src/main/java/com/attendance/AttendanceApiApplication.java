package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class AttendanceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AttendanceApiApplication.class, args);
	}

}
