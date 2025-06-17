package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.survey.backend.entity.Payment;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

class InvoiceServiceTest {

  private InvoiceService cut;

  @TempDir Path tempDir;

  @BeforeEach
  void setUp() {
    cut = new InvoiceService();
    ReflectionTestUtils.setField(cut, "storagePath", tempDir.toString());
  }

  @Test
  void generateInvoice_creates_pdf_with_payment_details() throws Exception {
    Payment payment =
        Payment.builder()
            .id(42L)
            .email("foo@example.com")
            .amountCents(2500)
            .currency("USD")
            .creditsGranted(3)
            .build();

    cut.generateInvoice(payment);

    Path pdf = tempDir.resolve("payment-42.pdf");
    assertTrue(Files.exists(pdf), "PDF file should be created");

    try (PDDocument doc = PDDocument.load(pdf.toFile())) {
      String text = new PDFTextStripper().getText(doc);
      assertTrue(text.contains("Payment ID: 42"));
      assertTrue(text.contains("Email: foo@example.com"));
      assertTrue(text.contains("Amount: 25.0 USD"));
      assertTrue(text.contains("Credits Granted: 3"));
    }
  }
}
