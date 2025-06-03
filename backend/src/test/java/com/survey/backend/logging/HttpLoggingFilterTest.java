package com.survey.backend.logging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.security.FirebaseAuthFilter;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

@WebMvcTest(
    controllers = HttpLoggingFilterTest.DemoController.class,
    excludeAutoConfiguration = {
      org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
      org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class
    })
@ActiveProfiles("test")
class HttpLoggingFilterTest {

  @Autowired MockMvc mockMvc;
  @MockBean UserRepository userRepository;
  @MockBean FirebaseAuthFilter firebaseAuthFilter;

  @RestController
  static class DemoController {
    @PostMapping("/api")
    public String echo(@RequestBody(required = false) String body) {
      return body == null ? "" : body;
    }
  }

  @Test
  void logsRedactedSensitiveInfo() throws Exception {
    Logger logger = (Logger) LoggerFactory.getLogger(HttpLoggingFilter.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);

    String json = "{\"user\":\"alice\",\"password\":\"1234\",\"token\":\"t1234\"}";

    mockMvc
        .perform(
            post("/api")
                .header("Authorization", "Bearer shouldberedacted")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
        .andExpect(status().isOk());

    boolean found = listAppender.list.toString().contains("Authorization=[REDACTED]");
    assertTrue(found, "Expected HTTP Request Log with redacted sensitive data");
  }
}
