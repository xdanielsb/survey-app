package com.survey.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.survey.backend.entity.Payment;
import com.survey.backend.entity.User;
import com.survey.backend.service.PaymentService;
import com.survey.backend.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class PaymentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private PaymentService paymentService;

  @MockBean private UserService userService;

  @Test
  @WithMockUser(username = "abc123", roles = "CUSTOMER")
  void testCreateSession_returnsSessionId() throws Exception {
    // Arrange
    User mockUser = new User();
    mockUser.setUid("abc123");
    mockUser.setEmail("test@user.com");

    when(userService.getCurrentUser()).thenReturn(mockUser);
    when(paymentService.createCheckoutSession(mockUser)).thenReturn("cs_test_123456");

    // Act & Assert
    mockMvc
        .perform(post("/payments/session"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sessionId").value("cs_test_123456"));
  }

  @Test
  void testWebhook_callsServiceAndReturns200() throws Exception {
    String payload = "{\"id\":\"evt_test\"}";
    String signature = "sig_test";

    doNothing().when(paymentService).handleWebhook(payload, signature);

    mockMvc
        .perform(
            post("/payments/webhook")
                .content(payload)
                .header("Stripe-Signature", signature)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(paymentService).handleWebhook(payload, signature);
  }

  @Test
  void testCreateSession_whenNotAuthenticated_returns401() throws Exception {
    when(userService.getCurrentUser()).thenThrow(new AccessDeniedException("Not authenticated"));

    mockMvc.perform(post("/payments/session")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "abc123", roles = "CUSTOMER")
  void testVerifyPayment_returnsPaymentInfo() throws Exception {
    // Arrange
    String sessionId = "cs_test_123456";
    Payment payment =
        Payment.builder()
            .stripeSessionId(sessionId)
            .status("PAID")
            .email("test@user.com")
            .creditsGranted(1)
            .build();

    when(paymentService.findBySessionId(sessionId)).thenReturn(Optional.of(payment));

    // Act & Assert
    mockMvc
        .perform(MockMvcRequestBuilders.get("/payments/verify").param("session_id", sessionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.paid").value(true))
        .andExpect(jsonPath("$.status").value("PAID"))
        .andExpect(jsonPath("$.email").value("test@user.com"))
        .andExpect(jsonPath("$.creditsGranted").value(1));
  }

  @Test
  @WithMockUser
  void testVerifyPayment_notFound() throws Exception {
    String sessionId = "cs_test_missing";
    when(paymentService.findBySessionId(sessionId)).thenReturn(Optional.empty());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/payments/verify").param("session_id", sessionId))
        .andExpect(status().isNotFound());
  }
}
