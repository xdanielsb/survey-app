package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.entity.User;
import com.survey.backend.repository.RoleRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceGetCurrentUserTest {

  @Mock private UserRepository userRepository;
  @Mock private RoleRepository roleRepository;
  @InjectMocks private UserService userService;

  @AfterEach
  void clearContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void returnsUser_whenAuthenticatedWithStringPrincipal() {
    User user = User.builder().uid("uid123").email("test@example.com").build();
    when(userRepository.findByUid("uid123")).thenReturn(Optional.of(user));

    var auth = new UsernamePasswordAuthenticationToken("uid123", "pw", List.of());
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
  void throwsUsernameNotFound_whenUserMissing() {
    when(userRepository.findByUid("missing")).thenReturn(Optional.empty());

    var auth = new UsernamePasswordAuthenticationToken("missing", "pw", List.of());
    SecurityContextHolder.getContext().setAuthentication(auth);

    assertThrows(UsernameNotFoundException.class, () -> userService.getCurrentUser());
  }
}
