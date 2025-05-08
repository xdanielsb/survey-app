package com.survey.backend.helper;


import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.entity.Answer;
import com.survey.backend.entity.LikertScale;
import com.survey.backend.entity.Question;
import com.survey.backend.entity.Response;

public class AnswerMapper {

    public static Answer fromDTO(SurveyResponseDTO.AnswerDTO dto, Question question, Response response) {
        return Answer.builder()
                .question(question)
                .response(response)
                .answer(LikertScale.valueOf(dto.getAnswer().toUpperCase().replace(" ", "_")))
                .build();
    }
}