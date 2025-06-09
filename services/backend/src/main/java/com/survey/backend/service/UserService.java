package com.survey.backend.service;

import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
import com.survey.backend.repository.RoleRepository;
import com.survey.backend.repository.UserRepository;
import java.util.Set;
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
  private final RoleRepository roleRepository;
  private final KeycloakAdminService keycloakAdminService;

  private static final Logger log = LoggerFactory.getLogger(UserService.class);

  public User saveUser(String uid, String email) {
    return userRepository
        .findByUid(uid)
        .map(
            existing -> {
              existing.setEmail(email); // update email if changed
              log.info("Updating existing user with uid: {} and email: {}", uid, email);
              return userRepository.save(existing);
            })
        .orElseGet(
            () -> {
              Role customerRole =
                  roleRepository
                      .findByName("CUSTOMER")
                      .orElseThrow(() -> new IllegalStateException("CUSTOMER role not found"));
              User user =
                  User.builder()
                      .uid(uid)
                      .email(email)
                      .isPremium(false)
                      .roles(Set.of(customerRole))
                      .build();
              User saved = userRepository.save(user);
              keycloakAdminService.createUser(
                  email, saved.getRoles().stream().map(Role::getName).toList());
              return saved;
            });
  }

  public User getCurrentUser() {
    var auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new AccessDeniedException("Not authenticated");
    }

    String uid;

    Object principal = auth.getPrincipal();

    if (principal instanceof String) {
      uid = (String) principal;
    } else if (principal instanceof UserDetails userDetails) {
      uid = userDetails.getUsername();
    } else {
      throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    return userRepository
        .findByUid(uid)
        .orElseThrow(() -> new UsernameNotFoundException("User not found for uid: " + uid));
  }
}
