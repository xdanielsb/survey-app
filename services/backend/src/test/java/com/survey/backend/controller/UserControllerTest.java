package com.survey.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.IntegrationTest;
import com.survey.backend.entity.User;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.service.KeycloakAdminService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
  @WithMockUser(
      username = "daniel@user.com",
      authorities = {"CUSTOMER"})
  void fetchUser() throws Exception {
    mockMvc
        .perform(get("/users/fetch"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("daniel@user.com"));
  }

  @Test
  @WithMockUser(
      username = "daniel@user.com",
      authorities = {"CUSTOMER"})
  void fetchUserPremium() throws Exception {
    userRepository.save(User.builder().email("daniel@user.com").isPremium(true).build());
    mockMvc
        .perform(get("/users/fetch"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.premium").value(true))
        .andExpect(jsonPath("$.email").value("daniel@user.com"));
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  void getUsers_returnsAllUsers() throws Exception {
    userRepository.save(User.builder().email("u1@test.com").build());
    userRepository.save(User.builder().email("u2@test.com").build());
    Mockito.when(keycloakAdminService.getUserRoles(Mockito.anyString()))
        .thenReturn(List.of("ADMIN"));

    mockMvc
        .perform(get("/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].email").value("u1@test.com"))
        .andExpect(jsonPath("$[1].email").value("u2@test.com"));
  }
}
