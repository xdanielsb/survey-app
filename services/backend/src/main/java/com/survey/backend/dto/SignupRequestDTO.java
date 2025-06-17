package com.survey.backend.dto;

import lombok.Data;

@Data
public class SignupRequestDTO {
  private String email;
  private String password;
}
