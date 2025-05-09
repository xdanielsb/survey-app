package com.survey.backend.dto;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseDTO {
  private Long surveyId;
  private List<AnswerDTO> answers;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AnswerDTO {
    private Long questionId;
    private String answer;
  }
}
