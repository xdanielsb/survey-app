package com.survey.backend.service;

import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.entity.*;
import com.survey.backend.helper.AnswerMapper;
import com.survey.backend.helper.SurveyMapper;
import com.survey.backend.respository.AnswerRepository;
import com.survey.backend.respository.QuestionRepository;
import com.survey.backend.respository.ResponseRepository;
import com.survey.backend.respository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepo;
    private final QuestionRepository questionRepo;
    private final ResponseRepository responseRepo;
    private final AnswerRepository answerRepo;

    // ✅ 1. Return a survey with questions
    public Optional<SurveyDTO> getSurveyById(Long id) {
        return surveyRepo.findById(id).map(SurveyMapper::toDTO);
    }

    // ✅ 2. Store an anonymous response
    public boolean saveSurveyResponse(Long surveyId, SurveyResponseDTO dto) {
        return surveyRepo.findById(surveyId).map(survey -> {
            Response response = responseRepo.save(
                    Response.builder().survey(survey).build()
            );

            List<Answer> answers = dto.getAnswers().stream()
                    .map(dtoAnswer -> {
                        Question question = questionRepo.findById(dtoAnswer.getQuestionId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid question ID: " + dtoAnswer.getQuestionId()));
                        return AnswerMapper.fromDTO(dtoAnswer, question, response);
                    })
                    .toList();

            answerRepo.saveAll(answers);
            return true;
        }).orElse(false);
    }

    public Optional<SurveyResultDTO> getSurveyResults(Long surveyId) {
        Optional<Survey> surveyOpt = surveyRepo.findById(surveyId);
        if (surveyOpt.isEmpty()) return Optional.empty();

        Survey survey = surveyOpt.get();

        List<SurveyResultDTO.QuestionResultDTO> results = survey.getQuestions().stream().map(question -> {
            List<Answer> answers = answerRepo.findByQuestionId(question.getId());

            Map<LikertScale, Long> distribution = Arrays.stream(LikertScale.values())
                    .collect(Collectors.toMap(
                            scale -> scale,
                            scale -> answers.stream().filter(a -> a.getAnswer() == scale).count()
                    ));

            double averageScore = answers.stream()
                    .mapToInt(a -> mapLikertToScore(a.getAnswer()))
                    .average()
                    .orElse(0.0);

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
                                    .build()
                    )
                    .build();
        }).collect(Collectors.toList());

        SurveyResultDTO result = SurveyResultDTO.builder()
                .surveyId(survey.getId())
                .surveyTitle(survey.getTitle())
                .questionResults(results)
                .build();

        return Optional.of(result);
    }

    // Helper: map Likert scale to numeric for average score
    private int mapLikertToScore(LikertScale scale) {
        return switch (scale) {
            case TOTALLY_DISAGREE -> 1;
            case DISAGREE -> 2;
            case NEUTRAL -> 3;
            case AGREE -> 4;
            case FULLY_AGREE -> 5;
        };
    }
}
