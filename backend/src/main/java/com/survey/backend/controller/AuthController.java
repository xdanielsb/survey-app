package com.survey.backend.controller;

import com.survey.backend.dto.LoginRequestDTO;
import com.survey.backend.dto.LoginResponseDTO;
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
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
    return ResponseEntity.ok(loginService.login(request));
  }
}
