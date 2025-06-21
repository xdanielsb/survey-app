package com.survey.backend.service;

import com.survey.backend.entity.Payment;

public interface EmailService {
  void sendCreditPurchaseEmail(Payment payment);
}
