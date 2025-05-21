package com.survey.backend.service;

import com.survey.backend.dto.CreateSurveyDTO;
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
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

  private final SurveyRepository surveyRepo;
  private final QuestionRepository questionRepo;
  private final ResponseRepository responseRepo;
  private final AnswerRepository answerRepo;

  public Optional<SurveyDTO> getSurveyById(Long id) {
    return surveyRepo.findById(id).map(SurveyMapper::toDTO);
  }

  public List<SurveyDTO> getAllSurveys() {
    return surveyRepo.findAll().stream()
        .map(SurveyMapper::toDTO) // assuming you have a mapper
        .collect(Collectors.toList());
  }

  public boolean saveSurveyResponse(Long surveyId, SurveyResponseDTO dto) {
    Optional<Survey> surveyOpt = surveyRepo.findById(surveyId);
    if (surveyOpt.isEmpty()) {
      log.warn("Survey ID {} not found", surveyId);
      return false;
    }

    try {
      Survey survey = surveyOpt.get();
      Response response = createSurveyResponseRecord(survey);
      List<Answer> answers = convertDtoToAnswers(dto, response);

      answerRepo.saveAll(answers);
      return true;
    } catch (IllegalArgumentException ex) {
      log.warn("Invalid data while saving survey response: {}", ex.getMessage());
      return false;
    } catch (Exception ex) {
      log.error(
          "Unexpected error saving survey response for surveyId {}: {}",
          surveyId,
          ex.getMessage(),
          ex);
      return false;
    }
  }

  private Response createSurveyResponseRecord(Survey survey) {
    return responseRepo.save(Response.builder().survey(survey).build());
  }

  private List<Answer> convertDtoToAnswers(SurveyResponseDTO dto, Response response) {
    return dto.getAnswers().stream()
        .map(
            dtoAnswer -> {
              Long questionId = dtoAnswer.getQuestionId();
              Question question =
                  questionRepo
                      .findById(questionId)
                      .orElseThrow(
                          () -> {
                            log.error("Invalid question ID: " + questionId);
                            return new IllegalArgumentException(
                                "Invalid question ID: " + questionId);
                          });
              return AnswerMapper.fromDTO(dtoAnswer, question, response);
            })
        .toList();
  }

  public Optional<SurveyResultDTO> getSurveyResults(Long surveyId) {
    Optional<Survey> surveyOpt = surveyRepo.findById(surveyId);
    if (surveyOpt.isEmpty()) {
      log.warn("Survey ID {} not found for result generation", surveyId);
      return Optional.empty();
    }

    try {
      Survey survey = surveyOpt.get();
      List<SurveyResultDTO.QuestionResultDTO> questionResults = generateResultsForSurvey(survey);
      SurveyResultDTO result = buildSurveyResultDTO(survey, questionResults);
      return Optional.of(result);
    } catch (Exception ex) {
      log.error(
          "Failed to compute survey results for surveyId {}: {}", surveyId, ex.getMessage(), ex);
      return Optional.empty();
    }
  }

  private List<SurveyResultDTO.QuestionResultDTO> generateResultsForSurvey(Survey survey) {
    return survey.getQuestions().stream()
        .map(this::computeQuestionResult)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private SurveyResultDTO.QuestionResultDTO computeQuestionResult(Question question) {
    try {
      List<Answer> answers = answerRepo.findByQuestionId(question.getId());

      Map<LikertScale, Long> distribution =
          Arrays.stream(LikertScale.values())
              .collect(
                  Collectors.toMap(
                      scale -> scale,
                      scale -> answers.stream().filter(a -> a.getAnswer() == scale).count()));

      double averageScore =
          answers.stream().mapToInt(a -> mapLikertToScore(a.getAnswer())).average().orElse(0.0);

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

    } catch (Exception ex) {
      log.error(
          "Failed to compute result for questionId {}: {}", question.getId(), ex.getMessage(), ex);
      return null;
    }
  }

  private SurveyResultDTO buildSurveyResultDTO(
      Survey survey, List<SurveyResultDTO.QuestionResultDTO> questionResults) {
    return SurveyResultDTO.builder()
        .surveyId(survey.getId())
        .surveyTitle(survey.getTitle())
        .questionResults(questionResults)
        .build();
  }

  public SurveyDTO createSurvey(CreateSurveyDTO dto) {
    Survey survey = Survey.builder().title(dto.getTitle()).build();

    List<Question> questions =
        dto.getQuestions().stream()
            .map(
                q ->
                    Question.builder()
                        .questionText(q.getQuestionText())
                        .position(q.getPosition())
                        .survey(survey)
                        .build())
            .collect(Collectors.toList());

    survey.setQuestions(questions);

    Survey s = this.surveyRepo.save(survey);
    return SurveyMapper.toDTO(s);
  }

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
