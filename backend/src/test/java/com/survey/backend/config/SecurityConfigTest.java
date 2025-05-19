package com.survey.backend.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void publicEndpointsShouldBeAccessibleWithoutAuthentication() throws Exception {
    mockMvc.perform(get("/surveys/1")).andExpect(status().isOk());
    mockMvc.perform(get("/surveys/1/results")).andExpect(status().isOk());
    mockMvc.perform(get("/surveys")).andExpect(status().isOk());
  }

  @Test
  void protectedEndpointShouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
    mockMvc
        .perform(
            post("/surveys/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"Test\", \"questions\": [] }"))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser
  void protectedEndpointShouldAllowAccessWhenAuthenticated() throws Exception {
    mockMvc
        .perform(
            post("/surveys/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"Test\", \"questions\": [] }"))
        .andExpect(status().isOk());
  }

  @Test
  void unknownEndpointsShouldBeDenied() throws Exception {
    mockMvc.perform(get("/admin")).andExpect(status().isForbidden());
  }
}
