package com.physiotherapy.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ProfileTest {

    @Autowired
    private Environment environment;

    @Test
    public void testActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            System.out.println("Active Profile: " + profile);
        }
        // Use the updated method to check if the "test" profile is active
        assertTrue(environment.acceptsProfiles(Profiles.of("test")), "Test profile should be active");
    }
}

