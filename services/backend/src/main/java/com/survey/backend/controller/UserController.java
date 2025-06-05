package com.survey.backend.controller;

import com.survey.backend.entity.User;
import com.survey.backend.security.RequireAuth;
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

  @PostMapping("/create")
  public ResponseEntity<User> createUser(@RequestBody Map<String, String> body) {
    String uid = body.get("uid");
    String email = body.get("email");

    if (uid == null || email == null) {
      return ResponseEntity.badRequest().build();
    }

    User saved = userService.saveUser(uid, email);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/credits")
  @RequireAuth
  public ResponseEntity<Map<String, Integer>> getCredits() {
    User user = userService.getCurrentUser();
    return ResponseEntity.ok(Map.of("credits", user.getSurveyCredits()));
  }
}
