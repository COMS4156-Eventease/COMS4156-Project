package com.eventease.eventease_service.integration_test;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DatabaseIntegrationTest {

  @Autowired
  private DataSource dataSource;

  @Test
  public void shouldConnectToDatabase() throws Exception {
    // Act: Obtain a connection from the data source
    try (Connection connection = dataSource.getConnection()) {
      // Assert: Check if the connection is valid
      assertNotNull(connection, "Connection should not be null");
      assertTrue(connection.isValid(2), "Connection should be valid");
    }
  }
}
