package com.survey.backend.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyDTO {
  private Long id;
  private String title;
  private List<QuestionDTO> questions;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class QuestionDTO {
    private Long id;
    private String questionText;
    private Integer position;
  }
}
