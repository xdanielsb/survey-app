package com.survey.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
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

  private ObjectMapper objectMapper = new ObjectMapper();

  private final String firebaseToken = "mock-firebase-token";
  private final String firebaseUid = "firebase-uid-123";

  @BeforeEach
  public void setup() {
    Map<String, Object> firebaseResponse = new HashMap<>();
    firebaseResponse.put("idToken", firebaseToken);
    firebaseResponse.put("localId", firebaseUid);

    ResponseEntity<Map> mockResponse = new ResponseEntity<>(firebaseResponse, HttpStatus.OK);
    Mockito.when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(mockResponse);
  }

  @Test
  public void testLoginSuccess() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password");
    String email = "test@example.com";

    Role customerRole = new Role(1L, "CUSTOMER");
    User mockUser =
        User.builder()
            .uid(firebaseUid)
            .roles(Set.of(customerRole))
            .email("x@test.com")
            .isPremium(true)
            .build();

    Mockito.when(userService.saveUser(firebaseUid, email)).thenReturn(mockUser);

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value(firebaseToken))
        .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"))
        .andExpect(jsonPath("$.premium").value(true));
  }

  @Test
  public void testLoginFailsWhenFirebaseResponseIsInvalid() throws Exception {
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail("user@example.com");
    loginRequest.setPassword("password");

    // Firebase returns 200 OK but no idToken (invalid credentials)
    Map<String, Object> firebaseResponse = Map.of("someOtherField", "value");

    Mockito.when(
            restTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(Map.class)))
        .thenReturn(new ResponseEntity<>(firebaseResponse, HttpStatus.OK));

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized());
  }
}
