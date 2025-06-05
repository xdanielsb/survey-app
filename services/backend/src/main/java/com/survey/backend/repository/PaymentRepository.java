package com.survey.backend.repository;

import com.survey.backend.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByStripeSessionId(String sessionId);
}
