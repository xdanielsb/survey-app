package com.survey.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import com.survey.backend.repository.PaymentRepository;
import com.survey.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  @Value("${stripe.secret-key}")
  private String stripeSecretKey;

  @Value("${stripe.webhook-secret}")
  private String webhookSecret;

  @Value("${stripe.price-id}")
  private String priceId;

  @Value("${stripe.frontend.url}")
  private String frontendUrl;

  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;

  @PostConstruct
  public void init() {
    Stripe.apiKey = stripeSecretKey;
  }

  public Optional<Payment> findBySessionId(String sessionId) {
    return paymentRepository.findByStripeSessionId(sessionId);
  }

  public String createCheckoutSession(User user) throws StripeException {
    SessionCreateParams params =
        SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(frontendUrl + "/payment-success?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(frontendUrl + "/payment-cancel")
            .setCustomerEmail(user.getEmail())
            .addLineItem(
                SessionCreateParams.LineItem.builder().setPrice(priceId).setQuantity(1L).build())
            .build();

    Session session = Session.create(params);

    Payment payment =
        Payment.builder()
            .stripeSessionId(session.getId())
            .status("PENDING")
            .user(user)
            .creditsGranted(1)
            .amountCents(500)
            .currency("eur")
            .email(user.getEmail())
            .build();

    paymentRepository.save(payment);

    log.info("Created Stripe session: id={}, user={}", session.getId(), user.getEmail());

    return session.getId();
  }

  @Transactional
  public void handleWebhook(String payload, String signatureHeader) throws StripeException {
    Event event;
    try {
      event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
    } catch (SignatureVerificationException e) {
      log.warn("Stripe signature verification failed", e);
      throw e;
    }

    log.info("Received Stripe webhook: type={}, id={}", event.getType(), event.getId());

    switch (event.getType()) {
      case "checkout.session.completed" -> handleCheckoutCompleted(event);
      default -> log.debug("Ignoring unsupported event type: {}", event.getType());
    }
  }

  private void handleCheckoutCompleted(Event event) {
    Optional<Session> sessionOpt =
        event
            .getDataObjectDeserializer()
            .getObject()
            .filter(Session.class::isInstance)
            .map(Session.class::cast);

    if (sessionOpt.isEmpty()) {
      log.warn("Failed to deserialize Session from event: {}", event.toJson());
      return;
    }

    Session session = sessionOpt.get();
    String sessionId = session.getId();
    String eventId = event.getId();

    Optional<Payment> paymentOpt = paymentRepository.findByStripeSessionId(sessionId);
    if (paymentOpt.isEmpty()) {
      log.warn("Payment not found for sessionId={}", sessionId);
      return;
    }

    Payment payment = paymentOpt.get();
    if (eventId.equals(payment.getStripeEventId())) {
      log.info("Duplicate event received: {}", eventId);
      return;
    }

    if (!"PAID".equals(payment.getStatus())) {
      payment.setStatus("PAID");
      payment.setStripeEventId(eventId);
      payment.setStripePaymentIntent(session.getPaymentIntent());

      User user = payment.getUser();
      user.setSurveyCredits(user.getSurveyCredits() + payment.getCreditsGranted());

      paymentRepository.save(payment);
      userRepository.save(user);

      log.info(
          "Payment confirmed: user={}, credits={}, session={}",
          user.getEmail(),
          user.getSurveyCredits(),
          sessionId);
    }
  }
}
