package com.survey.backend.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "surveys")
@Entity
@Builder
public class Survey {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
  private List<Question> questions;
}
