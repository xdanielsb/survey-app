package com.survey.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void publicEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/surveys")).andExpect(status().isOk());
    mockMvc.perform(get("/surveys/1")).andExpect(status().isOk());
    mockMvc.perform(get("/surveys/1/results")).andExpect(status().isOk());
  }

  @Test
  void protectedEndpointsShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
    mockMvc
        .perform(
            post("/surveys/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"Test\", \"questions\": [] }"))
        .andExpect(status().isUnauthorized());

    mockMvc.perform(post("/surveys/delete/1")).andExpect(status().isUnauthorized());
    mockMvc.perform(post("/payments/session")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/payments/verify?session_id=130")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/payments")).andExpect(status().isUnauthorized());
    mockMvc.perform(get("/users/credits")).andExpect(status().isUnauthorized());
    mockMvc
        .perform(
            post("/surveys/1/chat")
                .content("{\"question\":\"hi\"}")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void unknownEndpointsShouldBeDenied() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
    mockMvc.perform(get("/secret/hidden")).andExpect(status().isForbidden());
    mockMvc.perform(post("/internal/ops")).andExpect(status().isForbidden());
  }
}
