package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan({
		"com.example.demo.management.mapper",
		"com.example.demo.company.mapper",
		"com.example.demo.login.mapper",
		"com.example.demo.project.issue.mapper",
		"com.example.demo.project.main.mapper",
		"com.example.demo.project.notice.mapper",
		"com.example.demo.project.option.mapper",
		"com.example.demo.project.calender.mapper"
})
public class FinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

}
