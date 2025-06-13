package com.survey.backend.helper;

import com.survey.backend.dto.PaymentDTO;
import com.survey.backend.entity.Payment;

public class PaymentMapper {
  public static PaymentDTO toDTO(Payment payment) {
    return PaymentDTO.builder()
        .id(payment.getId())
        .email(payment.getEmail())
        .amountCents(payment.getAmountCents())
        .creditsGranted(payment.getCreditsGranted())
        .status(payment.getStatus())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
