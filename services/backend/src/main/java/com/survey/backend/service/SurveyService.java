package com.survey.backend.service;

import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.entity.*;
import com.survey.backend.helper.AnswerMapper;
import com.survey.backend.helper.LikertScore;
import com.survey.backend.helper.QuestionResult;
import com.survey.backend.helper.SurveyMapper;
import com.survey.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

  private final SurveyRepository surveyRepo;
  private final QuestionRepository questionRepo;
  private final ResponseRepository responseRepo;
  private final AnswerRepository answerRepo;
  private final UserRepository userRepo;
  private final UserService userService;

  public Optional<SurveyDTO> getSurveyById(Long id) {
    return surveyRepo.findById(id).map(SurveyMapper::toDTO);
  }

  public Page<SurveyDTO> getAllSurveys(Pageable pageable, String query) {
    Page<Survey> page =
        (query == null || query.isBlank())
            ? surveyRepo.findAll(pageable)
            : surveyRepo.findByTitleContainingIgnoreCase(query, pageable);
    return page.map(SurveyMapper::toDTO);
  }

  public void deleteSurvey(Long surveyId) {
    surveyRepo
        .findById(surveyId)
        .ifPresentOrElse(
            survey -> {
              surveyRepo.delete(survey);
            },
            () -> {
              log.warn("Attempted to delete non-existent survey: id={}", surveyId);
              throw new EntityNotFoundException("Survey not found with ID: " + surveyId);
            });
  }

  @Transactional
  public boolean saveSurveyResponse(Long surveyId, SurveyResponseDTO dto) {
    Optional<Survey> surveyOpt = surveyRepo.findById(surveyId);
    if (surveyOpt.isEmpty()) {
      log.warn("Survey ID {} not found", surveyId);
      return false;
    }

    try {
      Survey survey = surveyOpt.get();
      Response response = responseRepo.save(Response.builder().survey(survey).build());
      List<Answer> answers = convertDtoToAnswers(dto, response);
      answerRepo.saveAll(answers);
      surveyRepo.incrementResponseCount(survey.getId());
      survey.setResponseCount(survey.getResponseCount() + 1);
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
      List<SurveyResultDTO.QuestionResultDTO> questionResults =
          survey.getQuestions().stream()
              .map(this::computeQuestionResult)
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      SurveyResultDTO result = SurveyMapper.buildSurveyResultDTO(survey, questionResults);
      return Optional.of(result);
    } catch (Exception ex) {
      log.error(
          "Failed to compute survey results for surveyId {}: {}", surveyId, ex.getMessage(), ex);
      return Optional.empty();
    }
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
          answers.stream()
              .mapToInt(a -> LikertScore.mapLikertToScore(a.getAnswer()))
              .average()
              .orElse(0.0);

      return QuestionResult.getQuestionResultDTO(question, answers, averageScore, distribution);

    } catch (Exception ex) {
      log.error(
          "Failed to compute result for questionId {}: {}", question.getId(), ex.getMessage(), ex);
      return null;
    }
  }

  @Transactional
  public Survey createSurvey(CreateSurveyDTO dto) {
    User user = userService.getCurrentUser();
    int updated = userRepo.decrementCreditIfAvailable(user.getId());
    if (updated == 0) {
      throw new IllegalStateException("Not enough survey credits to create a survey.");
    }
    // Sync the in-memory entity so subsequent save() persists the new credit value
    user.setSurveyCredits(user.getSurveyCredits() - 1);

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
    userRepo.save(user); // Save user to update credits
    return surveyRepo.save(survey);
  }
}
