package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordTest {
	@Test
	public void test() {
		PasswordEncoder encoder = new BCryptPasswordEncoder(10);
		String pw = encoder.encode("admin");
		System.out.println(pw);
		boolean result = encoder.matches("admin", "$2a$10$phU9FnLKsmIgaJugM85o1.uzOEUSw0rw1qMMhqQVVEyOs19H5ZEeu");
		System.out.println(result);
	}
}
