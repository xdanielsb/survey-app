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
public class SurveyDTO {
  private Long id;
  private String title;
  private int responseCount;
  private List<QuestionDTO> questions;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class QuestionDTO {
    private Long id;
    private String questionText;
    private Integer position;
  }
}
