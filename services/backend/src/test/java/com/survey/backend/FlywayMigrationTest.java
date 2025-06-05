package com.survey.backend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class FlywayMigrationTest {

  @Autowired private Flyway flyway;

  @Test
  public void allMigrationsShouldApplyCleanly() {
    assertDoesNotThrow(() -> flyway.validate(), "Flyway validation failed");
  }
}
