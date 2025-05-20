package com.survey.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.IntegrationTest;
import com.survey.backend.respository.UserRepository;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class UserControllerIntegrationTest extends IntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;

  @Test
  void createUser_savesUserAndReturnsOk() throws Exception {
    var requestBody = Map.of("uid", "firebase-123", "email", "test@user.com");

    mockMvc
        .perform(
            post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.uid").value("firebase-123"))
        .andExpect(jsonPath("$.email").value("test@user.com"));

    var saved = userRepository.findByUid("firebase-123");
    assertThat(saved).isPresent();
    assertThat(saved.get().getEmail()).isEqualTo("test@user.com");
  }

  @Test
  void createUser_returnsBadRequest_whenMissingFields() throws Exception {
    var invalidBody = Map.of("uid", "missing-email");

    mockMvc
        .perform(
            post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidBody)))
        .andExpect(status().isBadRequest());
  }
}
