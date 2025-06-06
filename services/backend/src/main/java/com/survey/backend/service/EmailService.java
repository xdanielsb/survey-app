package com.survey.backend.service;

import com.survey.backend.entity.User;

public interface EmailService {
  void sendCreditPurchaseEmail(User user, int credits);
}
