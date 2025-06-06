package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import com.survey.backend.repository.PaymentRepository;
import com.survey.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceCreateSessionTest {

  @InjectMocks private PaymentService paymentServiceMock;

  @Mock private PaymentRepository paymentRepo;

  @Mock private UserRepository userRepo;

  @Mock private EmailService emailService;

  private static final String DUMMY_STRIPE_SECRET = "sk_test_dummy";
  private static final String DUMMY_PRICE_ID = "price_12345";
  private static final String DUMMY_FRONTEND_URL = "https://frontend.example.com";

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(paymentServiceMock, "stripeSecretKey", DUMMY_STRIPE_SECRET);
    ReflectionTestUtils.setField(paymentServiceMock, "priceId", DUMMY_PRICE_ID);
    ReflectionTestUtils.setField(paymentServiceMock, "frontendUrl", DUMMY_FRONTEND_URL);
  }

  @Test
  void createCheckoutSession_savesPaymentAndReturnsSessionId() throws Exception {
    User user = User.builder().id(99L).email("tester@example.com").surveyCredits(0).build();
    Session fakeSession = new Session();
    fakeSession.setId("cs_test_ABC");

    // 3) Mock the static call to Session.create(SessionCreateParams) so it returns  fakeSession
    try (MockedStatic<Session> sessionMock = mockStatic(Session.class)) {
      sessionMock
          .when(() -> Session.create(ArgumentMatchers.<SessionCreateParams>any()))
          .thenReturn(fakeSession);

      String returnedId = paymentServiceMock.createCheckoutSession(user);
      assertEquals("cs_test_ABC", returnedId);
      verify(paymentRepo)
          .save(
              argThat(
                  (Payment p) ->
                      "cs_test_ABC".equals(p.getStripeSessionId())
                          && "PENDING".equals(p.getStatus())
                          && "tester@example.com".equals(p.getEmail())
                          && p.getCreditsGranted() == 1
                          && p.getAmountCents() == 500
                          && "eur".equals(p.getCurrency())));
    }
  }
}
