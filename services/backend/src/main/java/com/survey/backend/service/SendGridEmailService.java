package com.survey.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.survey.backend.controller.PaymentController;
import com.survey.backend.entity.User;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

  @Value("${sendgrid.api-key}")
  private String apiKey;

  @Value("${sendgrid.from-email}")
  private String fromEmail;

  @Override
  public void sendCreditPurchaseEmail(User user, int credits) {
    Email from = new Email(fromEmail);
    Email to = new Email(user.getEmail());
    String subject = "Your credits purchase";
    String body =
        "Hello, you have purchased " + credits + " survey credit" + (credits > 1 ? "s" : "") + ".";
    Content content = new Content("text/plain", body);
    Mail mail = new Mail(from, subject, to, content);

    SendGrid sg = new SendGrid(apiKey);
    Request request = new Request();
    try {
      request.setMethod(Method.POST);
      request.setEndpoint("mail/send");
      request.setBody(mail.build());
      Response response = sg.api(request);
      log.info("SendGrid response: {}: {}", response.getStatusCode(), response.getBody());
      if (response.getStatusCode() >= 400) {
        log.warn(
            "SendGrid failed with status {} while emailing {}",
            response.getStatusCode(),
            to.getEmail());
      } else {
        log.info("Purchase email sent to {}", to.getEmail());
      }
    } catch (IOException e) {
      log.error("Error sending email via SendGrid to {}: {}", to.getEmail(), e.getMessage());
    }
  }
}
