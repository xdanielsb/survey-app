package com.survey.backend.controller;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.User;
import com.survey.backend.helper.UserMapper;
import com.survey.backend.security.KeycloakRole;
import com.survey.backend.security.RequireAuth;
import com.survey.backend.security.RequireRole;
import com.survey.backend.service.KeycloakAdminService;
import com.survey.backend.service.UserService;
import java.util.Map;
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

  @PostMapping("/create")
  public ResponseEntity<UserDTO> createUser(@RequestBody Map<String, String> body) {
    String uid = body.get("uid");
    String email = body.get("email");

    if (uid == null || email == null) {
      return ResponseEntity.badRequest().build();
    }

    User saved = userService.saveUser(uid, email);
    var roles = keycloakAdminService.getUserRoles(email);
    return ResponseEntity.ok(UserMapper.toDTO(saved, roles));
  }

  @GetMapping("/credits")
  @RequireAuth
  public ResponseEntity<Map<String, Integer>> getCredits() {
    User user = userService.getCurrentUser();
    return ResponseEntity.ok(Map.of("credits", user.getSurveyCredits()));
  }

  @GetMapping
  @RequireRole({KeycloakRole.ADMIN, KeycloakRole.MANAGER})
  public ResponseEntity<java.util.List<UserDTO>> getUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }
}
