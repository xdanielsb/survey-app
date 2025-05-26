package com.survey.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String stripeSessionId;

  private String stripePaymentIntent;
  private String email;

  @Column(nullable = false)
  private int amountCents;

  @Column(nullable = false)
  private String currency;

  @Column(nullable = false)
  private int creditsGranted;

  @Column(nullable = false)
  private String status;

  private String failureReason;
  private String stripeEventId;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @CreationTimestamp
  @Column(updatable = false)
  private Instant createdAt;

  @UpdateTimestamp private Instant updatedAt;
}
