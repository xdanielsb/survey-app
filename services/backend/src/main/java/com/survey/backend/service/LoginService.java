package com.survey.backend.service;

import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.dto.LoginResponseDTO;
import com.survey.backend.dto.SignupRequestDTO;
import com.survey.backend.entity.User;
import com.survey.backend.security.KeycloakRole;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final RestTemplate restTemplate;
  private final UserService userService;
  private final KeycloakAdminService keycloakAdminService;

  @Value("${keycloak.url}")
  private String keycloakUrl;

  @Value("${keycloak.realm}")
  private String keycloakRealm;

  @Value("${keycloak.client-id:backoffice}")
  private String keycloakClientId;

  @Value("${keycloak.client-secret}")
  private String keycloakClientSecret;

  public LoginResponseDTO login(LoginRequestDTO request) {
    String tokenUrl =
        String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, keycloakRealm);
    String userInfoUrl =
        String.format("%s/realms/%s/protocol/openid-connect/userinfo", keycloakUrl, keycloakRealm);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
    form.add("grant_type", "password");
    form.add("client_id", keycloakClientId);
    form.add("client_secret", keycloakClientSecret);
    form.add("username", request.getEmail());
    form.add("password", request.getPassword());
    form.add("scope", "openid profile email");

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);

    ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
    if (!response.getStatusCode().is2xxSuccessful()
        || !response.getBody().containsKey("access_token")) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    String accessToken = (String) response.getBody().get("access_token");

    HttpHeaders infoHeaders = new HttpHeaders();
    infoHeaders.setBearerAuth(accessToken);
    HttpEntity<Void> infoEntity = new HttpEntity<>(infoHeaders);
    ResponseEntity<Map> infoResp =
        restTemplate.exchange(userInfoUrl, HttpMethod.GET, infoEntity, Map.class);
    if (!infoResp.getStatusCode().is2xxSuccessful()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    Map info = infoResp.getBody();
    String uid = (String) info.get("sub");
    String email = info.getOrDefault("email", request.getEmail()).toString();

    User user = userService.saveUser(email);
    List<String> roles = keycloakAdminService.getUserRoles(email);

    return new LoginResponseDTO(accessToken, roles, user.isPremium());
  }

  public void signup(SignupRequestDTO request) {
    keycloakAdminService.createUser(
        request.getEmail(), request.getPassword(), List.of(KeycloakRole.CUSTOMER.name()));
    userService.saveUser(request.getEmail());
  }
}
