package com.survey.backend.service;

import com.survey.backend.dto.LoginRequest;
import com.survey.backend.dto.LoginResponse;
import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final RestTemplate restTemplate;
  private final UserService userService;

  @Value("${firebase.api.key}")
  private String firebaseApiKey;

  public LoginResponse login(LoginRequest request) {
    String firebaseUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key="
            + firebaseApiKey;

    Map<String, Object> payload =
        Map.of(
            "email", request.getEmail(),
            "password", request.getPassword(),
            "returnSecureToken", true);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<?> entity = new HttpEntity<>(payload, headers);
    ResponseEntity<Map> response = restTemplate.postForEntity(firebaseUrl, entity, Map.class);
    if (!response.getStatusCode().is2xxSuccessful() || !response.getBody().containsKey("idToken")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String idToken = (String) response.getBody().get("idToken");
    String firebaseUid = (String) response.getBody().get("localId");

    User user = userService.saveUser(firebaseUid, request.getEmail());
    List<String> roles = user.getRoles().stream().map(Role::getName).toList();

    return new LoginResponse(idToken, roles);
  }
}
