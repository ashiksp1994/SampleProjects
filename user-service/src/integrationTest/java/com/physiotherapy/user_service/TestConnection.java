package com.physiotherapy.user_service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.physiotherapy.userservice.UserServiceApplication;

@SpringBootTest(classes = UserServiceApplication.class) // Specify your main application class
public class TestConnection {

    private static final Logger logger = LoggerFactory.getLogger(TestConnection.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private static Connection connection;

    @BeforeAll
    static void setUp() {
        // This method can be used to set up other resources before the test
    }

    @Test
    public void testDatabaseConnection() {
        logger.info("Attempting to connect to the database...");

        // Test connection to the database
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            assertNotNull(connection, "Database connection should not be null");
            logger.info("Connection to the database established successfully!");

            // Run a simple SQL query to validate the connection
            String testQuery = "SELECT 1";
            try (PreparedStatement preparedStatement = connection.prepareStatement(testQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    logger.info("Test query successful! Database connection is working.");
                } else {
                    logger.error("Test query failed. No results returned.");
                    assertTrue(false, "Test query failed. No results returned.");
                }
            }
        } catch (SQLException e) {
            // Log the exception details and fail the test
            logger.error("Failed to connect to the database.", e);
            assertTrue(false, "Failed to connect to the database: " + e.getMessage());
        }
    }
}
