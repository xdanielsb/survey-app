package com.survey.backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSurveyDTO {
  private String title;
  private List<QuestionDTO> questions;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class QuestionDTO {
    private String questionText;
    private Integer position;
  }
}
