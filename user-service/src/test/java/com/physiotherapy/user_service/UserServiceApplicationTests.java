package com.physiotherapy.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.physiotherapy.userservice.UserServiceApplication;

@SpringBootTest(classes = UserServiceApplication.class)
@ActiveProfiles("test")
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
