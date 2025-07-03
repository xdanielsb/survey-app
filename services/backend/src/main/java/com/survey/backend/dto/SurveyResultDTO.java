package com.survey.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SurveyResultDTO {
  private Long surveyId;
  private String surveyTitle;
  private List<QuestionResultDTO> questionResults;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class QuestionResultDTO {
    private Long questionId;
    private String questionText;
    private long totalResponses;
    private double averageScore;
    private LikertDistribution distribution;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class LikertDistribution {
      private long totallyDisagree;
      private long disagree;
      private long neutral;
      private long agree;
      private long fullyAgree;
    }
  }
}
