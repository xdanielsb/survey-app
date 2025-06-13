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

/**
 * Tests PaymentService.handleWebhook(...) by statically mocking Webhook.constructEvent(...) so that
 * it returns an Event whose EventDataObjectDeserializer yields a valid Session.
 */
@ExtendWith(MockitoExtension.class)
class PaymentWebhookTest {

  @InjectMocks PaymentService cut;

  @Mock PaymentRepository paymentRepo;

  @Mock UserRepository userRepo;

  @Mock EmailService emailService;
  @Mock InvoiceService invoiceService;

  @BeforeEach
  void setUp() {
    // Inject the webhook secret exactly as PaymentService will see it.
    ReflectionTestUtils.setField(cut, "webhookSecret", "whsec_test");
  }

  @Test
  void marks_payment_paid_and_grants_credit_and_premium() throws Exception {

    User user = User.builder().id(1L).email("daniel@email.com").surveyCredits(0).build();

    Payment payment =
        Payment.builder()
            .stripeSessionId("sess_123")
            .status("PENDING")
            .user(user)
            .creditsGranted(1)
            .build();

    when(paymentRepo.findByStripeSessionId("sess_123")).thenReturn(Optional.of(payment));

    Session session = new Session();
    session.setId("sess_123");
    session.setPaymentIntent("pi_1");
    // create event
    Event realEvent = new Event();
    realEvent.setId("evt_1");
    realEvent.setType("checkout.session.completed");
    realEvent.setApiVersion("2020-08-27"); // Must be non‐null to avoid internal NPE
    realEvent.setObject("event"); // Top-level object must be "event"

    Event spyEvent = Mockito.spy(realEvent);
    EventDataObjectDeserializer dosMock = mock(EventDataObjectDeserializer.class);
    when(dosMock.getObject()).thenReturn(Optional.of(session));
    doReturn(dosMock).when(spyEvent).getDataObjectDeserializer();
    String dummyPayload = "{ \"dummy\": \"payload\" }";

    // Stub out Webhook.constructEvent
    try (MockedStatic<Webhook> webHookMock = mockStatic(Webhook.class)) {
      webHookMock
          .when(() -> Webhook.constructEvent(eq(dummyPayload), anyString(), eq("whsec_test")))
          .thenReturn(spyEvent);

      cut.handleWebhook(dummyPayload, "t=ignored,v1=ignored");

      // should be paid
      verify(paymentRepo)
          .save(argThat(p -> "PAID".equals(p.getStatus()) && "evt_1".equals(p.getStripeEventId())));
      // should have granted 1 credit and marked premium
      verify(userRepo).save(argThat(u -> u.getSurveyCredits() == 1 && u.isPremium()));
      verify(emailService).sendCreditPurchaseEmail(user, 1);
      verify(invoiceService).generateInvoice(payment);
    }
  }
}
