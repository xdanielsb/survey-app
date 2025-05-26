package com.survey.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_events")
public class PaymentEvent {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "payment_id")
  private Payment payment;

  private String eventType;
  private String stripeEventId;
  private String status;
  private Instant receivedAt = Instant.now();
}
