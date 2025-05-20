package com.survey.backend.controller;

import com.survey.backend.dto.LoginRequest;
import com.survey.backend.dto.LoginResponse;
import com.survey.backend.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

  private final LoginService loginService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(loginService.login(request));
  }
}
