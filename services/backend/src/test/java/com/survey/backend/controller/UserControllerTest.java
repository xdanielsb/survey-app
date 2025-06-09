package com.survey.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.IntegrationTest;
import com.survey.backend.entity.User;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.service.KeycloakAdminService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest extends IntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private UserRepository userRepository;
  @MockBean private KeycloakAdminService keycloakAdminService;

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
        .andExpect(jsonPath("$.email").value("test@user.com"))
        .andExpect(jsonPath("$.premium").value(false))
        .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));

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

  @Test
  @WithMockUser(
      username = "test-uid-123",
      authorities = {"CUSTOMER"})
  void getCredits_returnsSurveyCredits() throws Exception {
    userRepository.save(
        User.builder().uid("test-uid-123").email("credits@test.com").surveyCredits(7).build());

    mockMvc
        .perform(get("/users/credits"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.credits").value(7));
  }
}
