package com.survey.backend.controller;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.User;
import com.survey.backend.security.KeycloakRole;
import com.survey.backend.security.RequireAuth;
import com.survey.backend.security.RequireRole;
import com.survey.backend.service.KeycloakAdminService;
import com.survey.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

  private final UserService userService;

  private final KeycloakAdminService keycloakAdminService;

  @GetMapping("/fetch")
  @RequireAuth
  public ResponseEntity<User> getUser() {
    return ResponseEntity.ok(userService.getCurrentUser());
  }

  @GetMapping
  @RequireRole({KeycloakRole.ADMIN, KeycloakRole.MANAGER})
  public ResponseEntity<java.util.List<UserDTO>> getUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }
}
