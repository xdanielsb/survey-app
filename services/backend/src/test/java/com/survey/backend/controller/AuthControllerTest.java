package com.survey.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.entity.User;
import com.survey.backend.service.KeycloakAdminService;
import com.survey.backend.service.UserService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
public class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UserService userService;

  @MockBean private RestTemplate restTemplate;
  @MockBean private KeycloakAdminService keycloakAdminService;

  private ObjectMapper objectMapper = new ObjectMapper();

  private final String keycloakToken = "mock-access-token";
  private final String keycloakUid = "kc-uid-123";

  @BeforeEach
  public void setup() {
    Map<String, Object> tokenResponse = new HashMap<>();
    tokenResponse.put("access_token", keycloakToken);

    ResponseEntity<Map> tokenResp = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
    Mockito.when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(tokenResp);

    Map<String, Object> infoResponse = new HashMap<>();
    infoResponse.put("sub", keycloakUid);
    infoResponse.put("email", "test@example.com");

    ResponseEntity<Map> infoResp = new ResponseEntity<>(infoResponse, HttpStatus.OK);
    Mockito.when(
            restTemplate.exchange(
                anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(infoResp);
  }

  @Test
  public void testLoginSuccess() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");
    String email = "test@example.com";

    User mockUser = User.builder().uid(keycloakUid).email("x@test.com").isPremium(true).build();

    Mockito.when(userService.saveUser(keycloakUid, email)).thenReturn(mockUser);
    Mockito.when(keycloakAdminService.getUserRoles(email)).thenReturn(List.of("CUSTOMER"));

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(keycloakToken))
        .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"))
        .andExpect(jsonPath("$.premium").value(true));
  }

  @Test
  public void testLoginFailsWhenKeycloakResponseIsInvalid() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail("user@example.com");
    loginRequest.setPassword("password");

    // Keycloak returns 200 OK but no access token (invalid credentials)
    Map<String, Object> badTokenResp = Map.of("error", "invalid_grant");

    Mockito.when(
            restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class)))
        .thenReturn(new ResponseEntity<>(badTokenResp, HttpStatus.OK));

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }
}
