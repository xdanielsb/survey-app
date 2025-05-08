package com.survey.backend.helper;

import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.entity.Question;
import com.survey.backend.entity.Survey;


public class QuestionMapper {
    public static SurveyDTO.QuestionDTO toDTO(Question q) {
        return SurveyDTO.QuestionDTO.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .position(q.getPosition())
                .build();
    }

    public static Question fromDTO(CreateSurveyDTO.QuestionDTO dto, Survey survey) {
        return Question.builder()
                .survey(survey)
                .questionText(dto.getQuestionText())
                .position(dto.getPosition())
                .build();
    }
}