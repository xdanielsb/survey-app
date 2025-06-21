package com.survey.backend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.survey.backend.controller.PaymentController;
import com.survey.backend.entity.Payment;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service
@RequiredArgsConstructor
public class SendGridEmailService implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

  @Value("${sendgrid.api-key}")
  private String apiKey;

  @Value("${sendgrid.from-email}")
  private String fromEmail;

  @Value("${company.website}")
  private String website;

  @Value("${company.name}")
  private String companyName;

  @Value("${company.logo-url}")
  private String logoUrl;

  @Override
  public void sendCreditPurchaseEmail(Payment payment) {
    int credits = payment.getCreditsGranted();
    Email from = new Email(fromEmail);
    Email to = new Email(payment.getEmail());
    String subject = "Your credits purchase";
    String body;
    try {
      ClassPathResource res = new ClassPathResource("assets/credit_purchase_email.html");
      body = StreamUtils.copyToString(res.getInputStream(), StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error("Failed to load email template", e);
      body =
          "Hello, you have purchased "
              + credits
              + " survey credit"
              + (credits > 1 ? "s" : "")
              + ".";
    }

    DateTimeFormatter fmt =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    String amount = String.format("%.2f", payment.getAmountCents() / 100.0);
    body = body.replace("{{credits}}", String.valueOf(credits));
    body = body.replace("{{plural}}", credits > 1 ? "s" : "");
    body = body.replace("{{email}}", payment.getEmail());
    body = body.replace("{{paymentId}}", String.valueOf(payment.getId()));
    body = body.replace("{{amount}}", amount);
    body = body.replace("{{currency}}", payment.getCurrency().toUpperCase());
    body = body.replace("{{date}}", fmt.format(payment.getCreatedAt()));
    body = body.replace("{{website}}", website);
    body = body.replace("{{companyName}}", companyName);
    body = body.replace("{{supportEmail}}", fromEmail);
    body = body.replace("{{logoUrl}}", logoUrl);

    Content content = new Content("text/html", body);
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
