package com.survey.backend.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import com.survey.backend.repository.PaymentRepository;
import com.survey.backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentWebhookEdgeCasesTest {

  @InjectMocks PaymentService paymentService;

  @Mock PaymentRepository paymentRepo;
  @Mock UserRepository userRepo;
  @Mock EmailService emailService;
  @Mock InvoiceService invoiceService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_test");
  }

  private Event buildEvent(Session session) {
    Event real = new Event();
    real.setId("evt_1");
    real.setType("checkout.session.completed");
    real.setApiVersion("2020-08-27");
    real.setObject("event");
    Event spy = Mockito.spy(real);
    EventDataObjectDeserializer dos = mock(EventDataObjectDeserializer.class);
    when(dos.getObject()).thenReturn(Optional.ofNullable(session));
    doReturn(dos).when(spy).getDataObjectDeserializer();
    return spy;
  }

  @Test
  void doesNothing_whenPaymentMissing() throws Exception {
    Session session = new Session();
    session.setId("sess_missing");
    Event event = buildEvent(session);

    when(paymentRepo.findByStripeSessionId("sess_missing")).thenReturn(Optional.empty());
    try (MockedStatic<Webhook> webHookMock = mockStatic(Webhook.class)) {
      webHookMock
          .when(() -> Webhook.constructEvent(eq("{}"), anyString(), eq("whsec_test")))
          .thenReturn(event);

      paymentService.handleWebhook("{}", "sig");

      verify(paymentRepo, never()).save(any());
      verifyNoInteractions(userRepo, emailService, invoiceService);
    }
  }

  @Test
  void ignoresDuplicateEvent() throws Exception {
    Session session = new Session();
    session.setId("sess_dup");
    Event event = buildEvent(session);
    event.setId("evt_dup");

    User user = User.builder().email("dup@test.com").surveyCredits(1).build();
    Payment payment =
        Payment.builder()
            .stripeSessionId("sess_dup")
            .stripeEventId("evt_dup")
            .status("PAID")
            .user(user)
            .build();
    when(paymentRepo.findByStripeSessionId("sess_dup")).thenReturn(Optional.of(payment));

    try (MockedStatic<Webhook> webHookMock = mockStatic(Webhook.class)) {
      webHookMock
          .when(() -> Webhook.constructEvent(eq("{}"), anyString(), eq("whsec_test")))
          .thenReturn(event);

      paymentService.handleWebhook("{}", "sig");

      verify(paymentRepo, never()).save(any());
      verifyNoInteractions(userRepo, emailService, invoiceService);
    }
  }
}
