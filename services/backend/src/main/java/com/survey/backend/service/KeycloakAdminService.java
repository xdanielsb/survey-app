package com.survey.backend.service;

import jakarta.ws.rs.core.Response;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

  @Value("${keycloak.url}")
  private String serverUrl;

  @Value("${keycloak.realm}")
  private String realm;

  @Value("${keycloak.admin.username}")
  private String username;

  @Value("${keycloak.admin.password}")
  private String password;

  private static final Logger log = LoggerFactory.getLogger(KeycloakAdminService.class);

  private Keycloak getInstance() {
    log.info("Connecting to Keycloak at {}", serverUrl);
    return KeycloakBuilder.builder()
        .serverUrl(serverUrl)
        .realm("master")
        .clientId("admin-cli")
        .grantType(OAuth2Constants.PASSWORD)
        .username(username)
        .password(password)
        .build();
  }

  public void createUser(String email, List<String> roles) {
    createUser(email, null, roles);
  }

  public void createUser(String email, String password, List<String> roles) {
    Keycloak kc = getInstance();
    UsersResource users = kc.realm(realm).users();
    UserRepresentation user = new UserRepresentation();
    user.setUsername(email);
    user.setEmail(email);
    user.setEnabled(true);
    Response resp = users.create(user);
    if (resp.getStatus() >= 300) {
      return; // ignore failures
    }
    log.info("Created user {} in Keycloak", email);
    String userId = CreatedResponseUtil.getCreatedId(resp);

    if (password != null) {
      CredentialRepresentation cred = new CredentialRepresentation();
      cred.setType(CredentialRepresentation.PASSWORD);
      cred.setValue(password);
      cred.setTemporary(false);
      users.get(userId).resetPassword(cred);
    }

    log.info("Assigned roles {} to user {}", roles, email);
    // TODO: verify roles exist before assigning
    RealmResource rr = kc.realm(realm);
    List<RoleRepresentation> roleReps =
        roles.stream().map(r -> rr.roles().get(r.toLowerCase()).toRepresentation()).toList();
    rr.roles().list().forEach(role -> log.info("Available role: {}", role.getName()));
    rr.users().get(userId).roles().realmLevel().add(roleReps);
  }

  public List<String> getUserRoles(String email) {
    Keycloak kc = getInstance();
    UsersResource users = kc.realm(realm).users();
    List<UserRepresentation> result = users.search(email, true);
    if (result.isEmpty()) {
      log.warn("User {} not found in Keycloak", email);
      return List.of();
    }
    String userId = result.get(0).getId();
    return users.get(userId).roles().realmLevel().listAll().stream()
        .map(RoleRepresentation::getName)
        .toList();
  }
}
