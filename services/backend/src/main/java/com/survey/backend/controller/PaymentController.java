package com.survey.backend.controller;

import com.stripe.exception.StripeException;
import com.survey.backend.dto.PaymentDTO;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import com.survey.backend.helper.PaymentMapper;
import com.survey.backend.security.KeycloakRole;
import com.survey.backend.security.RequireRole;
import com.survey.backend.service.PaymentService;
import com.survey.backend.service.UserService;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final UserService userService;
  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

  @PostMapping("/session")
  public Map<String, String> createSession() throws StripeException {
    User user = userService.getCurrentUser();
    log.info("Payment started by: " + user.getEmail());
    String sessionId = paymentService.createCheckoutSession(user);
    return Map.of("sessionId", sessionId);
  }

  @PostMapping("/webhook")
  public ResponseEntity<Void> webhook(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sig) throws Exception {
    paymentService.handleWebhook(payload, sig);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/verify")
  public ResponseEntity<Map<String, Object>> verifyPayment(
      @RequestParam("session_id") String sessionId) {
    Optional<Payment> paymentOpt = paymentService.findBySessionId(sessionId);

    if (paymentOpt.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Payment payment = paymentOpt.get();
    boolean paid = "PAID".equalsIgnoreCase(payment.getStatus());

    return ResponseEntity.ok(
        Map.of(
            "paid", paid,
            "status", payment.getStatus(),
            "email", payment.getEmail(),
            "creditsGranted", payment.getCreditsGranted()));
  }

  @GetMapping
  @RequireRole({KeycloakRole.ADMIN, KeycloakRole.MANAGER})
  public ResponseEntity<java.util.List<PaymentDTO>> getPayments() {
    var payments =
        paymentService.getAllPayments().stream()
            .map(PaymentMapper::toDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(payments);
  }
}
