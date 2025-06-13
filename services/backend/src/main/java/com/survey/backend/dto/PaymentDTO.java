package com.survey.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
  private Long id;
  private String email;
  private int amountCents;
  private int creditsGranted;
  private String status;
  private Instant createdAt;
}
