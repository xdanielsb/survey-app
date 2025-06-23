package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.ws.rs.core.Response;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class KeycloakAdminServiceTest {

  @InjectMocks private KeycloakAdminService service;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(service, "serverUrl", "http://keycloak");
    ReflectionTestUtils.setField(service, "realm", "test");
    ReflectionTestUtils.setField(service, "username", "admin");
    ReflectionTestUtils.setField(service, "password", "pw");
  }

  @Test
  void createUser_withPassword_assignsRolesAndPassword() {
    Keycloak kc = mock(Keycloak.class);
    RealmResource rr = mock(RealmResource.class);
    UsersResource users = mock(UsersResource.class);
    UserResource userRes = mock(UserResource.class);
    RolesResource roles = mock(RolesResource.class);
    RoleResource role = mock(RoleResource.class);
    RoleRepresentation rep = new RoleRepresentation();
    RoleMappingResource mapping = mock(RoleMappingResource.class);
    RoleScopeResource scope = mock(RoleScopeResource.class);
    Response resp = mock(Response.class);

    when(kc.realm("test")).thenReturn(rr);
    when(rr.users()).thenReturn(users);
    when(users.create(any(UserRepresentation.class))).thenReturn(resp);
    when(resp.getStatus()).thenReturn(201);
    when(users.get("uid")).thenReturn(userRes);
    when(rr.roles()).thenReturn(roles);
    when(roles.get("admin")).thenReturn(role);
    when(role.toRepresentation()).thenReturn(rep);
    when(userRes.roles()).thenReturn(mapping);
    when(mapping.realmLevel()).thenReturn(scope);
    when(rr.users()).thenReturn(users);

    KeycloakBuilder builder = mock(KeycloakBuilder.class, RETURNS_SELF);
    try (MockedStatic<KeycloakBuilder> builderStatic = mockStatic(KeycloakBuilder.class);
        MockedStatic<CreatedResponseUtil> createdStatic = mockStatic(CreatedResponseUtil.class)) {
      builderStatic.when(KeycloakBuilder::builder).thenReturn(builder);
      when(builder.build()).thenReturn(kc);
      createdStatic.when(() -> CreatedResponseUtil.getCreatedId(resp)).thenReturn("uid");

      service.createUser("a@b.com", "secret", List.of("ADMIN"));

      verify(users).create(argThat(u -> "a@b.com".equals(u.getEmail()) && u.isEnabled()));
      verify(userRes).resetPassword(any(CredentialRepresentation.class));
      ArgumentCaptor<List<RoleRepresentation>> captor = ArgumentCaptor.forClass(List.class);
      verify(scope).add(captor.capture());
      assertEquals(List.of(rep), captor.getValue());
    }
  }

  @Test
  void getUserRoles_returnsRoleNames() {
    Keycloak kc = mock(Keycloak.class);
    RealmResource rr = mock(RealmResource.class);
    UsersResource users = mock(UsersResource.class);
    UserResource userRes = mock(UserResource.class);
    RoleMappingResource mapping = mock(RoleMappingResource.class);
    RoleScopeResource scope = mock(RoleScopeResource.class);
    RoleRepresentation rep = new RoleRepresentation();
    rep.setName("ADMIN");

    when(kc.realm("test")).thenReturn(rr);
    when(rr.users()).thenReturn(users);
    UserRepresentation ur = new UserRepresentation();
    ur.setId("id1");
    when(users.search("user@test.com", true)).thenReturn(List.of(ur));
    when(users.get("id1")).thenReturn(userRes);
    when(userRes.roles()).thenReturn(mapping);
    when(mapping.realmLevel()).thenReturn(scope);
    when(scope.listAll()).thenReturn(List.of(rep));

    KeycloakBuilder builder = mock(KeycloakBuilder.class, RETURNS_SELF);
    try (MockedStatic<KeycloakBuilder> builderStatic = mockStatic(KeycloakBuilder.class)) {
      builderStatic.when(KeycloakBuilder::builder).thenReturn(builder);
      when(builder.build()).thenReturn(kc);

      List<String> result = service.getUserRoles("user@test.com");
      assertEquals(List.of("ADMIN"), result);
    }
  }
}
