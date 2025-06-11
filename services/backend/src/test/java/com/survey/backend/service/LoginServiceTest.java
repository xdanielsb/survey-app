package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.dto.LoginResponseDTO;
import com.survey.backend.entity.User;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

  @Mock private RestTemplate restTemplate;
  @Mock private UserService userService;
  @InjectMocks private LoginService loginService;
  @Mock private KeycloakAdminService keycloakAdminService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(loginService, "firebaseApiKey", "dummy-key");
  }

  @Test
  void login_success_returnsTokenAndRoles() {
    LoginRequestDTO req = new LoginRequestDTO();
    req.setEmail("test@example.com");
    req.setPassword("pw");

    Map<String, Object> firebaseResp = new HashMap<>();
    firebaseResp.put("idToken", "token123");
    firebaseResp.put("localId", "uid123");

    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(firebaseResp, HttpStatus.OK));

    User user = User.builder().uid("uid123").email("test@example.com").isPremium(true).build();
    when(userService.saveUser("uid123", "test@example.com")).thenReturn(user);
    when(keycloakAdminService.getUserRoles("test@example.com")).thenReturn(List.of("CUSTOMER"));

    LoginResponseDTO result = loginService.login(req);

    assertEquals("token123", result.getToken());
    assertEquals(List.of("CUSTOMER"), result.getRoles());
    assertTrue(result.isPremium());
  }

  @Test
  void login_invalidCredentials_throwsUnauthorized() {
    LoginRequestDTO req = new LoginRequestDTO();
    req.setEmail("bad@example.com");
    req.setPassword("wrong");

    Map<String, Object> firebaseResp = Map.of("noToken", "value");
    when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(Map.class)))
        .thenReturn(new ResponseEntity<>(firebaseResp, HttpStatus.OK));

    assertThrows(ResponseStatusException.class, () -> loginService.login(req));
  }
}
