package com.physiotherapy.user_service;

import com.physiotherapy.userservice.model.UserModel;
import com.physiotherapy.userservice.repository.UserRepository;
import com.physiotherapy.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @MockBean  // Mock the repository to isolate the service layer
    private UserRepository userRepository;

    @Test
    public void testSaveUserWithMockRepository() {
        // Arrange
        UserModel user = new UserModel();
        user.setUsername("integrationTestUser");
        user.setPassword("testPassword");
        user.setRole("USER");

        // Mock the repository's save method
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel savedUser = invocation.getArgument(0);
            savedUser.setId(1L);  // Simulate an ID being set
            return savedUser;
        });

        // Act
        UserModel savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser.getId());  // ID should not be null after save
        assertEquals("integrationTestUser", savedUser.getUsername());
        assertEquals("USER", savedUser.getRole());

        // Verify the save method was called once
        verify(userRepository, times(1)).save(any(UserModel.class));
    }

    @Test
    public void testFindUserByUsernameWithMockRepository() {
        // Arrange
        UserModel mockUser = new UserModel();
        mockUser.setUsername("findTestUser");
        mockUser.setPassword("testPassword");
        mockUser.setRole("USER");

        // Mock the repository's findByUsername method
        when(userRepository.findByUsername("findTestUser")).thenReturn(mockUser);

        // Act
        UserModel foundUser = userService.findByUsername("findTestUser");

        // Assert
        assertNotNull(foundUser);
        assertEquals("findTestUser", foundUser.getUsername());

        // Verify that the findByUsername method was called once
        verify(userRepository, times(1)).findByUsername("findTestUser");
    }
}
