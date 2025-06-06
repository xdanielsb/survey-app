package com.survey.backend.helper;

import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.entity.Survey;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyMapper {

  public static SurveyDTO toDTO(Survey survey) {
    return SurveyDTO.builder()
        .id(survey.getId())
        .title(survey.getTitle())
        .responseCount(survey.getResponseCount())
        .questions(
            survey.getQuestions().stream().map(QuestionMapper::toDTO).collect(Collectors.toList()))
        .build();
  }

  public static SurveyResultDTO buildSurveyResultDTO(
      Survey survey, List<SurveyResultDTO.QuestionResultDTO> questionResults) {
    return SurveyResultDTO.builder()
        .surveyId(survey.getId())
        .surveyTitle(survey.getTitle())
        .questionResults(questionResults)
        .build();
  }
}
