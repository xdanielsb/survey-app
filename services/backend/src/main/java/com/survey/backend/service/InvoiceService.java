package com.survey.backend.service;

import com.survey.backend.entity.Payment;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

  @Value("${invoice.storage.path}")
  private String storagePath;

  private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

  public void generateInvoice(Payment payment) {
    try {
      Path dir = Paths.get(storagePath);
      if (!Files.exists(dir)) {
        Files.createDirectories(dir);
      }
      Path pdf = dir.resolve("payment-" + payment.getId() + ".pdf");
      try (PDDocument doc = new PDDocument()) {
        PDPage page = new PDPage();
        doc.addPage(page);
        try (PDPageContentStream stream = new PDPageContentStream(doc, page)) {
          stream.beginText();
          stream.setFont(PDType1Font.HELVETICA, 12);
          stream.newLineAtOffset(50, 700);
          stream.showText("Payment ID: " + payment.getId());
          stream.newLineAtOffset(0, -15);
          stream.showText("Email: " + payment.getEmail());
          stream.newLineAtOffset(0, -15);
          stream.showText(
              "Amount: " + payment.getAmountCents() / 100.0 + " " + payment.getCurrency());
          stream.newLineAtOffset(0, -15);
          stream.showText("Credits Granted: " + payment.getCreditsGranted());
          stream.endText();
        }
        doc.save(pdf.toFile());
      }
      log.info("Invoice stored at {}", pdf);
    } catch (IOException e) {
      log.error("Failed to generate invoice PDF for payment {}", payment.getId(), e);
    }
  }
}
