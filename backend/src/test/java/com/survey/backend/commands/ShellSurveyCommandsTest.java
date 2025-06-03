package com.survey.backend.commands;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.entity.Survey;
import com.survey.backend.repository.SurveyRepository;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class ShellSurveyCommandsTest {

  @MockBean private SurveyRepository surveyRepository;

  @Test
  void testGenerateSurveys_CreatesCorrectNumber() {
    ShellSurveyCommands shellCommands = new ShellSurveyCommands(surveyRepository);
    int count = 3;
    AttributedString result = shellCommands.generateSurveys(count);
    verify(surveyRepository, times(count)).save(any(Survey.class));
    ArgumentCaptor<Survey> captor = ArgumentCaptor.forClass(Survey.class);
    verify(surveyRepository, times(count)).save(captor.capture());
    assertThat(captor.getAllValues()).hasSize(count);
    assertThat(result.toString()).contains("Generated " + count + " surveys.");
  }

  @Test
  void testGenerateSurveys_Zero() {
    ShellSurveyCommands shellCommands = new ShellSurveyCommands(surveyRepository);
    AttributedString result = shellCommands.generateSurveys(0);
    verify(surveyRepository, never()).save(any());
  }
}
