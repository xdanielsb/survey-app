package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.User;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.security.KeycloakRole;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private UserService userService;
  @Mock KeycloakAdminService keycloakAdminService;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void returnsUser_whenAuthenticatedWithStringPrincipal() {
    User user = User.builder().email("test@example.com").build();
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    var auth = new UsernamePasswordAuthenticationToken("test@example.com", "pw", List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);

    User result = userService.getCurrentUser();

    assertEquals(user, result);
  }

  @Test
  void throwsAccessDenied_whenNoAuthentication() {
    SecurityContextHolder.clearContext();
    assertThrows(AccessDeniedException.class, () -> userService.getCurrentUser());
  }

  @Test
  void saveUser_returnsExisting_whenFound() {
    User existing = User.builder().email("a@b.com").build();
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(existing));

    User result = userService.saveUser("a@b.com");

    assertSame(existing, result);
    verify(userRepository, never()).save(any());
    verifyNoInteractions(keycloakAdminService);
  }

  @Test
  void saveUser_createsNew_whenMissing() {
    when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.empty());
    User saved = User.builder().email("a@b.com").isPremium(false).build();
    when(userRepository.save(any(User.class))).thenReturn(saved);

    User result = userService.saveUser("a@b.com");

    assertEquals(saved, result);
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertEquals("a@b.com", captor.getValue().getEmail());
    verify(keycloakAdminService).createUser("a@b.com", List.of(KeycloakRole.CUSTOMER.name()));
  }

  @Test
  void getAllUsers_mapsRolesCorrectly() {
    User u1 = User.builder().id(1L).email("u1@test.com").surveyCredits(2).isPremium(true).build();
    User u2 = User.builder().id(2L).email("u2@test.com").surveyCredits(0).isPremium(false).build();
    when(userRepository.findAll()).thenReturn(List.of(u1, u2));
    when(keycloakAdminService.getUserRoles("u1@test.com")).thenReturn(List.of("ADMIN"));
    when(keycloakAdminService.getUserRoles("u2@test.com")).thenReturn(List.of("CUSTOMER"));

    List<UserDTO> result = userService.getAllUsers();

    assertEquals(2, result.size());
    UserDTO dto1 = result.get(0);
    assertEquals(1L, dto1.getId());
    assertTrue(dto1.isPremium());
    assertEquals(List.of("ADMIN"), dto1.getRoles());
  }
}
