package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.dto.LoginResponseDTO;
import com.survey.backend.entity.User;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private RestTemplate restTemplate;
  @Mock private UserService userService;
  @InjectMocks private LoginService loginService;
  @Mock private KeycloakAdminService keycloakAdminService;

  private final String keycloakToken = "token123";

  @Test
  void login_success_returnsTokenAndRoles() {
    Map<String, Object> tokenResp = Map.of("access_token", keycloakToken);
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(tokenResp, HttpStatus.OK));

    Map<String, Object> infoResp = Map.of("email", "test@example.com");
    when(restTemplate.exchange(
            anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(infoResp, HttpStatus.OK));
    LoginRequestDTO req = new LoginRequestDTO();
    req.email("test@example.com");
    req.password("pw");

    User user = User.builder().email("test@example.com").isPremium(true).build();
    when(userService.saveUser("test@example.com")).thenReturn(user);
    when(keycloakAdminService.getUserRoles("test@example.com")).thenReturn(List.of("CUSTOMER"));

    LoginResponseDTO result = loginService.login(req);

    assertEquals(keycloakToken, result.token());
    assertEquals(List.of("CUSTOMER"), result.roles());
    assertTrue(result.isPremium());
  }

  @Test
  void login_invalidCredentials_throwsUnauthorized() {
    LoginRequestDTO req = new LoginRequestDTO();
    req.email("bad@example.com");
    req.password("wrong");

    Map<String, Object> badResp = Map.of("error", "invalid_grant");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(badResp, HttpStatus.OK));

    assertThrows(ResponseStatusException.class, () -> loginService.login(req));
  }
}
