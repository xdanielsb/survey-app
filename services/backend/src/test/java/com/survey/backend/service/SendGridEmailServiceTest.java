package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import java.io.IOException;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SendGridEmailServiceTest {

  @InjectMocks SendGridEmailService sendgridEmailService;

  @BeforeEach
  void setup() {
    ReflectionTestUtils.setField(sendgridEmailService, "apiKey", "SG_TEST_KEY");
    ReflectionTestUtils.setField(sendgridEmailService, "fromEmail", "no-reply@example.com");
    ReflectionTestUtils.setField(sendgridEmailService, "website", "website");
    ReflectionTestUtils.setField(sendgridEmailService, "companyName", "companyName");
    ReflectionTestUtils.setField(sendgridEmailService, "logoUrl", "logoUrl.png");
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
      sendgridEmailService.sendCreditPurchaseEmail(payment);

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

  @Test
  void fallsBackToSimpleMessage_whenTemplateUnavailable() throws Exception {
    User user = User.builder().email("x@y.com").build();
    Payment payment =
        Payment.builder()
            .id(2L)
            .email(user.getEmail())
            .amountCents(500)
            .currency("usd")
            .creditsGranted(1)
            .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
            .user(user)
            .build();
    Response resp = new Response();
    resp.setStatusCode(202);

    try (MockedConstruction<ClassPathResource> resMock =
            Mockito.mockConstruction(
                ClassPathResource.class,
                (mock, ctx) -> when(mock.getInputStream()).thenThrow(new IOException("no")));
        MockedConstruction<SendGrid> sgMock =
            Mockito.mockConstruction(
                SendGrid.class,
                (mock, ctx) -> when(mock.api(any(Request.class))).thenReturn(resp))) {
      sendgridEmailService.sendCreditPurchaseEmail(payment);
      SendGrid sg = sgMock.constructed().get(0);
      ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
      verify(sg).api(captor.capture());
      Request req = captor.getValue();
      assertTrue(req.getBody().contains("Hello, you have purchased 1 survey credit."));
    }
  }
}
