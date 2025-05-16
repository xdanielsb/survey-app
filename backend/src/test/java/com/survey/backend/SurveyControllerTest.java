package com.survey.backend;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.controller.SurveyController;
import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.service.SurveyService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SurveyController.class)
@ActiveProfiles("test")
class SurveyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SurveyService surveyService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testGetSurveyById() throws Exception {
    SurveyDTO dto =
        SurveyDTO.builder()
            .id(1L)
            .title("Team Survey")
            .questions(
                List.of(
                    SurveyDTO.QuestionDTO.builder()
                        .id(1L)
                        .position(1)
                        .questionText("How do you feel?")
                        .build()))
            .build();

    when(surveyService.getSurveyById(1L)).thenReturn(Optional.of(dto));

    mockMvc
        .perform(get("/surveys/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Team Survey"))
        .andExpect(jsonPath("$.questions[0].questionText").value("How do you feel?"));
  }

  @Test
  void testSubmitSurveyResponse() throws Exception {
    SurveyResponseDTO request =
        SurveyResponseDTO.builder()
            .surveyId(1L)
            .answers(
                List.of(
                    SurveyResponseDTO.AnswerDTO.builder().questionId(1L).answer("AGREE").build()))
            .build();

    when(surveyService.saveSurveyResponse(Mockito.eq(1L), any())).thenReturn(true);

    mockMvc
        .perform(
            post("/surveys/1/responses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testGetSurveyResults() throws Exception {
    SurveyResultDTO result =
        SurveyResultDTO.builder()
            .surveyId(1L)
            .surveyTitle("Team Survey")
            .questionResults(
                List.of(
                    SurveyResultDTO.QuestionResultDTO.builder()
                        .questionId(1L)
                        .questionText("How do you feel?")
                        .totalResponses(5)
                        .averageScore(4.2)
                        .distribution(
                            SurveyResultDTO.QuestionResultDTO.LikertDistribution.builder()
                                .agree(3)
                                .neutral(1)
                                .disagree(1)
                                .fullyAgree(0)
                                .totallyDisagree(0)
                                .build())
                        .build()))
            .build();

    when(surveyService.getSurveyResults(1L)).thenReturn(Optional.of(result));

    mockMvc
        .perform(get("/surveys/1/results"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.surveyTitle").value("Team Survey"))
        .andExpect(jsonPath("$.questionResults[0].questionText").value("How do you feel?"));
  }

  @Test
  void shouldReturnListOfSurveys() throws Exception {
    SurveyDTO survey1 = new SurveyDTO(1L, "Team Feedback", null);
    SurveyDTO survey2 = new SurveyDTO(2L, "Sprint Review", null);

    Mockito.when(surveyService.getAllSurveys()).thenReturn(List.of(survey1, survey2));

    mockMvc
        .perform(get("/surveys"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].title").value("Team Feedback"))
        .andExpect(jsonPath("$[1].title").value("Sprint Review"));
  }

  @Test
  void shouldReturnsCreatedSurvey() throws Exception {
    // Arrange
    CreateSurveyDTO dto =
        CreateSurveyDTO.builder()
            .title("User Feedback")
            .questions(
                List.of(
                    CreateSurveyDTO.QuestionDTO.builder()
                        .questionText("What do you like?")
                        .position(1)
                        .build(),
                    CreateSurveyDTO.QuestionDTO.builder()
                        .questionText("Any suggestions?")
                        .position(2)
                        .build()))
            .build();

    SurveyDTO mockSurveyDTO =
        SurveyDTO.builder()
            .id(1L)
            .title(dto.getTitle())
            .questions(
                List.of(
                    SurveyDTO.QuestionDTO.builder()
                        .id(100L)
                        .questionText("What do you like?")
                        .position(1)
                        .build(),
                    SurveyDTO.QuestionDTO.builder()
                        .id(101L)
                        .questionText("Any suggestions?")
                        .position(2)
                        .build()))
            .build();

    Mockito.when(surveyService.createSurvey(Mockito.any())).thenReturn(mockSurveyDTO);

    // Act & Assert
    mockMvc
        .perform(
            post("/surveys/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("User Feedback"))
        .andExpect(jsonPath("$.questions.length()").value(2));
  }
}
