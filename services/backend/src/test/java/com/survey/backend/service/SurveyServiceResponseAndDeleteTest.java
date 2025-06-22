package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.entity.*;
import com.survey.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SurveyServiceResponseAndDeleteTest {

  @InjectMocks SurveyService surveyService;

  @Mock SurveyRepository surveyRepo;
  @Mock QuestionRepository questionRepo;
  @Mock ResponseRepository responseRepo;
  @Mock AnswerRepository answerRepo;
  @Mock UserRepository userRepo;
  @Mock UserService userService;

  @Test
  void saveSurveyResponse_returnsTrue_whenSuccessful() {
    Survey survey = Survey.builder().id(1L).build();
    when(surveyRepo.findById(1L)).thenReturn(Optional.of(survey));

    Response resp = Response.builder().id(10L).survey(survey).build();
    when(responseRepo.save(any(Response.class))).thenReturn(resp);

    Question q = Question.builder().id(2L).survey(survey).build();
    when(questionRepo.findById(2L)).thenReturn(Optional.of(q));
    when(surveyRepo.incrementResponseCount(1L)).thenReturn(1);

    SurveyResponseDTO request =
        SurveyResponseDTO.builder()
            .surveyId(1L)
            .answers(
                List.of(
                    SurveyResponseDTO.AnswerDTO.builder().questionId(2L).answer("AGREE").build()))
            .build();

    boolean result = surveyService.saveSurveyResponse(1L, request);

    assertTrue(result);
    verify(responseRepo).save(any(Response.class));
    verify(surveyRepo).incrementResponseCount(1L);
    verify(answerRepo)
        .saveAll(
            argThat(
                iterable -> {
                  List<Answer> list = StreamSupport.stream(iterable.spliterator(), false).toList();
                  return list.size() == 1
                      && list.get(0).getQuestion().equals(q)
                      && list.get(0).getAnswer() == LikertScale.AGREE;
                }));
  }

  @Test
  void saveSurveyResponse_returnsFalse_whenSurveyMissing() {
    when(surveyRepo.findById(99L)).thenReturn(Optional.empty());

    SurveyResponseDTO request = SurveyResponseDTO.builder().surveyId(99L).build();

    boolean result = surveyService.saveSurveyResponse(99L, request);

    assertFalse(result);
    verifyNoInteractions(responseRepo);
    verifyNoInteractions(answerRepo);
  }

  @Test
  void saveSurveyResponse_returnsFalse_whenQuestionInvalid() {
    Survey survey = Survey.builder().id(1L).build();
    when(surveyRepo.findById(1L)).thenReturn(Optional.of(survey));
    when(responseRepo.save(any(Response.class)))
        .thenReturn(Response.builder().id(5L).survey(survey).build());
    when(questionRepo.findById(anyLong())).thenReturn(Optional.empty());

    SurveyResponseDTO request =
        SurveyResponseDTO.builder()
            .surveyId(1L)
            .answers(
                List.of(
                    SurveyResponseDTO.AnswerDTO.builder().questionId(2L).answer("AGREE").build()))
            .build();

    boolean result = surveyService.saveSurveyResponse(1L, request);

    assertFalse(result);
    verify(answerRepo, never()).saveAll(any());
  }

  @Test
  void deleteSurvey_deletes_whenSurveyExists() {
    Survey survey = Survey.builder().id(3L).build();
    when(surveyRepo.findById(3L)).thenReturn(Optional.of(survey));

    surveyService.deleteSurvey(3L);

    verify(surveyRepo).delete(survey);
  }

  @Test
  void deleteSurvey_throws_whenSurveyMissing() {
    when(surveyRepo.findById(5L)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> surveyService.deleteSurvey(5L));
  }
}
