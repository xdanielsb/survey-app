package com.survey.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.survey.backend.dto.*;
import com.survey.backend.entity.Question;
import com.survey.backend.entity.Survey;
import com.survey.backend.entity.User;
import com.survey.backend.repository.UserRepository;
import com.survey.backend.service.AnalyticsService;
import com.survey.backend.service.SurveyService;
import com.survey.backend.service.UserService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
class SurveyControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SurveyService surveyService;
  @MockBean private UserRepository userRepository;
  @MockBean private AnalyticsService analyticsService;
  @MockBean private UserService userService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void testGetSurveyById() throws Exception {
    SurveyDTO dto =
        SurveyDTO.builder()
            .id(1L)
            .title("Team Survey")
            .responseCount(0)
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
  void shouldReturnPagedListOfSurveys() throws Exception {
    SurveyDTO survey1 = SurveyDTO.builder().id(1L).title("Team Feedback").responseCount(0).build();
    SurveyDTO survey2 = SurveyDTO.builder().id(2L).title("Sprint Review").responseCount(0).build();
    Page<SurveyDTO> page = new PageImpl<>(List.of(survey1, survey2));

    Mockito.when(surveyService.getAllSurveys(Mockito.any(Pageable.class), Mockito.isNull()))
        .thenReturn(page);

    mockMvc
        .perform(get("/surveys?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].title").value("Team Feedback"))
        .andExpect(jsonPath("$.content[1].title").value("Sprint Review"));
  }

  @Test
  void shouldFilterSurveysUsingQuery() throws Exception {
    SurveyDTO survey = SurveyDTO.builder().id(5L).title("Search Title").responseCount(0).build();
    Page<SurveyDTO> page = new PageImpl<>(List.of(survey));

    Mockito.when(surveyService.getAllSurveys(Mockito.any(Pageable.class), Mockito.eq("search")))
        .thenReturn(page);

    mockMvc
        .perform(get("/surveys?q=search&page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Search Title"));
  }

  @Test
  @WithMockUser(
      username = "user",
      authorities = {"CUSTOMER", "MANAGER"})
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
                        .build()))
            .build();

    Survey mockSurvey =
        Survey.builder()
            .id(1L)
            .title(dto.getTitle())
            .questions(
                List.of(
                    Question.builder()
                        .id(100L)
                        .questionText("What do you like?")
                        .position(1)
                        .build()))
            .build();

    Mockito.when(surveyService.createSurvey(Mockito.any())).thenReturn(mockSurvey);

    // Act & Assert
    mockMvc
        .perform(
            post("/surveys/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.title").value("User Feedback"))
        .andExpect(jsonPath("$.questions.length()").value(1));
  }

  @Test
  @WithMockUser(
      username = "admin-user",
      authorities = {"ADMIN"})
  void deleteSurvey_shouldSucceed_withAdminRole() throws Exception {
    mockMvc.perform(delete("/surveys/delete/1")).andExpect(status().isNoContent());

    Mockito.verify(surveyService).deleteSurvey(1L);
  }

  @Test
  @WithMockUser(
      username = "customer-user",
      authorities = {"CUSTOMER"})
  void deleteSurvey_shouldFail_withCustomerRole() throws Exception {
    mockMvc.perform(delete("/surveys/delete/1")).andExpect(status().isForbidden());

    verifyNoInteractions(surveyService);
  }

  @Test
  @WithMockUser(
      username = "u1",
      authorities = {"CUSTOMER"})
  void chat_shouldReturnAnswer() throws Exception {
    ChatResponseDTO resp = ChatResponseDTO.builder().answer("pong").build();
    when(userService.getCurrentUser())
        .thenReturn(User.builder().email("u1").isPremium(true).build());
    when(analyticsService.askQuestion(any())).thenReturn(resp);

    mockMvc
        .perform(
            post("/surveys/1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"ping\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.answer").value("pong"));
  }

  @Test
  @WithMockUser(
      username = "u2",
      authorities = {"CUSTOMER"})
  void chat_shouldPromptUpgrade_whenNotPremium() throws Exception {
    when(userService.getCurrentUser())
        .thenReturn(User.builder().email("u2").isPremium(false).build());

    mockMvc
        .perform(
            post("/surveys/1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\":\"ping\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.answer").value("Buy a credit and enjoy chatting with a LLM"));
  }

  @Test
  void unauthenticatedRequest_returns401_withReasonPhrase() throws Exception {
    mockMvc
        .perform(post("/surveys/create").contentType("application/json").content("{}"))
        .andExpect(status().isUnauthorized()) // 401
        .andExpect(status().reason(HttpStatus.UNAUTHORIZED.getReasonPhrase()));

    verifyNoInteractions(surveyService);
  }

  @Test
  @WithMockUser(authorities = "CUSTOMER")
  void forbiddenRequest_returns403_andJsonError() throws Exception {
    mockMvc
        .perform(delete("/surveys/delete/1"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.status").value("AUTH_ERROR"));
  }
}
