package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.entity.User;
import com.survey.backend.repository.AnswerRepository;
import com.survey.backend.repository.QuestionRepository;
import com.survey.backend.repository.ResponseRepository;
import com.survey.backend.repository.SurveyRepository;
import com.survey.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
class SurveyServiceNoCreditTest {

  @InjectMocks SurveyService surveyService;

  @Mock SurveyRepository surveyRepo;

  @Mock QuestionRepository questionRepo;

  @Mock AnswerRepository answerRepo;

  @Mock ResponseRepository responseRepo;

  @Mock UserRepository userRepo;

  @Mock UserService userService;

  @Test
  @WithMockUser
  void throws_if_user_has_no_credits() {
    User u = User.builder().id(7L).surveyCredits(0).build();
    when(userService.getCurrentUser()).thenReturn(u);

    when(userRepo.decrementCreditIfAvailable(7L)).thenReturn(0);

    CreateSurveyDTO dto = new CreateSurveyDTO();
    dto.title("won't matter");

    // Act & Assert: should throw IllegalStateException
    assertThrows(IllegalStateException.class, () -> surveyService.createSurvey(dto));
    verifyNoInteractions(surveyRepo);
  }
}
