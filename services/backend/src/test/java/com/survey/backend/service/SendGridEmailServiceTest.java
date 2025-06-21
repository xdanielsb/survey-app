package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

  @InjectMocks SendGridEmailService cut;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(cut, "apiKey", "SG_TEST_KEY");
    ReflectionTestUtils.setField(cut, "fromEmail", "no-reply@example.com");
    ReflectionTestUtils.setField(cut, "website", "website");
    ReflectionTestUtils.setField(cut, "companyName", "companyName");
    ReflectionTestUtils.setField(cut, "logoUrl", "logoUrl.png");
  }

  @Test
  void sendsPurchaseEmail_withExpectedRequest() throws Exception {
    User user = User.builder().email("to@example.com").build();
    Payment payment =
        Payment.builder()
            .id(1L)
            .email(user.getEmail())
            .amountCents(1000)
            .currency("usd")
            .creditsGranted(2)
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .user(user)
            .build();
    Response resp = new Response();
    resp.setStatusCode(202);

    try (MockedConstruction<SendGrid> mocked =
        Mockito.mockConstruction(
            SendGrid.class, (mock, ctx) -> when(mock.api(any(Request.class))).thenReturn(resp))) {
      cut.sendCreditPurchaseEmail(payment);

      assertEquals(1, mocked.constructed().size());
      SendGrid sg = mocked.constructed().get(0);
      ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
      verify(sg).api(captor.capture());
      Request req = captor.getValue();

      assertEquals(Method.POST, req.getMethod());
      assertEquals("mail/send", req.getEndpoint());
      assertTrue(req.getBody().contains("\"type\":\"text/html\""));
      assertTrue(req.getBody().contains("Thank you for your purchase!"));
      assertTrue(req.getBody().contains("Payment ID"));
    }
  }
}
