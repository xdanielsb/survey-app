package com.survey.backend.helper;

import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.entity.Answer;
import com.survey.backend.entity.LikertScale;
import com.survey.backend.entity.Question;
import java.util.List;
import java.util.Map;

public class QuestionResult {

  public static SurveyResultDTO.QuestionResultDTO getQuestionResultDTO(
      Question question,
      List<Answer> answers,
      double averageScore,
      Map<LikertScale, Long> distribution) {
    return SurveyResultDTO.QuestionResultDTO.builder()
        .questionId(question.getId())
        .questionText(question.getQuestionText())
        .totalResponses(answers.size())
        .averageScore(averageScore)
        .distribution(
            SurveyResultDTO.QuestionResultDTO.LikertDistribution.builder()
                .totallyDisagree(distribution.getOrDefault(LikertScale.TOTALLY_DISAGREE, 0L))
                .disagree(distribution.getOrDefault(LikertScale.DISAGREE, 0L))
                .neutral(distribution.getOrDefault(LikertScale.NEUTRAL, 0L))
                .agree(distribution.getOrDefault(LikertScale.AGREE, 0L))
                .fullyAgree(distribution.getOrDefault(LikertScale.FULLY_AGREE, 0L))
                .build())
        .build();
  }
}
