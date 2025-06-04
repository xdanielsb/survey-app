package com.survey.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class LoginResponseDTO {
  private String token;
  private List<String> roles;
  private boolean isPremium;
}
