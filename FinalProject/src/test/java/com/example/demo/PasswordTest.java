package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {
	@Test
	public void test() {
		PasswordEncoder encoder = new BCryptPasswordEncoder(10);
		String pw = encoder.encode("user1");
		System.out.println(pw);
		boolean result = encoder.matches("user1", "$2a$10$b.7hRYnrOLRMu2Vx2dnPBOkDZADuEr9.XTGKjwPxiwONZv0vQ.Vv.");
		System.out.println(result);
	}
}
