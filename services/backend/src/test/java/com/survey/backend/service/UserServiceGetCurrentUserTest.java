package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.entity.User;
import com.survey.backend.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceGetCurrentUserTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private UserService userService;

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
}
