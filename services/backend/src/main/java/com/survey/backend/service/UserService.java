package com.survey.backend.service;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.User;
import com.survey.backend.helper.UserMapper;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.security.KeycloakRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final KeycloakAdminService keycloakAdminService;

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  public User saveUser(String email) {
    return userRepository
        .findByEmail(email)
        .orElseGet(
            () -> {
              User user = User.builder().email(email).isPremium(false).build();
              User saved = userRepository.save(user);
              keycloakAdminService.createUser(email, List.of(KeycloakRole.CUSTOMER.name()));
              return saved;
            });
  }

  public User getCurrentUser() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new AccessDeniedException("Not authenticated");
    }

    String email;

    Object principal = auth.getPrincipal();

    if (principal instanceof String) {
      email = (String) principal;
    } else if (principal instanceof UserDetails userDetails) {
      email = userDetails.getUsername();
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
  }

  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(u -> UserMapper.toDTO(u, keycloakAdminService.getUserRoles(u.getEmail())))
        .toList();
  }
}
