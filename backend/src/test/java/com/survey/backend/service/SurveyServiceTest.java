package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.dto.CreateSurveyDTO.QuestionDTO;
import com.survey.backend.entity.Survey;
import com.survey.backend.entity.User;
import com.survey.backend.repository.SurveyRepository;
import com.survey.backend.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
class SurveyServiceTest {

  @Mock private SurveyRepository surveyRepository;
  @Mock private UserRepository userRepository;
  @Mock private UserService userService;

  @InjectMocks private SurveyService surveyService;

  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSurvey_shouldSucceedAndDecreaseCredits() {
    // Arrange
    User user = new User();
    user.setUid("admin-user");
    user.setEmail("user@example.com");
    user.setSurveyCredits(1);

    CreateSurveyDTO dto = new CreateSurveyDTO();
    dto.setTitle("Test Survey");
    dto.setQuestions(List.of(new QuestionDTO("Q1", 0), new QuestionDTO("Q2", 1)));

    Survey savedSurvey = new Survey();
    savedSurvey.setId(1L);
    savedSurvey.setTitle("Test Survey");

    when(userService.getCurrentUser()).thenReturn(user);
    when(surveyRepository.save(any(Survey.class))).thenReturn(savedSurvey);
    when(userRepository.save(any(User.class))).thenReturn(user);

    // Act
    surveyService.createSurvey(dto);

    // Assert
    assertEquals(0, user.getSurveyCredits(), "User credits should decrease by 1");
    verify(surveyRepository, times(1)).save(any(Survey.class));
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void createSurvey_shouldFailWhenNoCredits() {
    // Arrange
    User user = new User();
    user.setUid("uid123");
    user.setEmail("user@example.com");
    user.setSurveyCredits(0);

    CreateSurveyDTO dto = new CreateSurveyDTO();
    dto.setTitle("Blocked Survey");

    when(userService.getCurrentUser()).thenReturn(user);

    // Act & Assert
    assertThrows(
        IllegalStateException.class,
        () -> {
          surveyService.createSurvey(dto);
        });
    verify(surveyRepository, never()).save(any());
    verify(userRepository, never()).save(any());
  }
}
