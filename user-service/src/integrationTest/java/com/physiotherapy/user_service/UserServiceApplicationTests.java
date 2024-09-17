package com.physiotherapy.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.physiotherapy.userservice.UserServiceApplication;

@SpringBootTest(classes = UserServiceApplication.class) // Specify your main application class
@ActiveProfiles("test")
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
		
		// Basic context loading test to ensure application starts up for testing
	}
}
