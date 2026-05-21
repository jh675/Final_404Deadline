package com.example.demo;

import java.util.TimeZone;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@MapperScan({"com.example.demo.**.mapper"})
public class FinalProjectApplication {

	public static void main(String[] args) {
		// DB(Oracle) 서버가 UTC 로 동작하므로 JVM 도 UTC 로 맞춰
		// TIMESTAMP(타임존 없음) 컬럼을 JDBC 가 UTC 로 해석하게 한다.
		// 프론트는 받은 UTC ISO 문자열을 브라우저 로컬(KST) 로 변환해 표시.
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(FinalProjectApplication.class, args);
	}

}
