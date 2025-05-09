package com.survey.backend.helper;

import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.entity.Survey;
import java.util.stream.Collectors;

public class SurveyMapper {

  public static SurveyDTO toDTO(Survey survey) {
    return SurveyDTO.builder()
        .id(survey.getId())
        .title(survey.getTitle())
        .questions(
            survey.getQuestions().stream().map(QuestionMapper::toDTO).collect(Collectors.toList()))
        .build();
  }
}
