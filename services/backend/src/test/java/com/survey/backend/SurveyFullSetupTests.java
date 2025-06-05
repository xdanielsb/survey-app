package com.survey.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.survey.backend.entity.*;
import com.survey.backend.repository.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SurveyFullSetupTests extends IntegrationTest {

  @Autowired private SurveyRepository surveyRepository;
  @Autowired private QuestionRepository questionRepository;
  @Autowired private ResponseRepository responseRepository;
  @Autowired private AnswerRepository answerRepository;

  @Test
  void fullSurveySetupTest() {
    Survey survey = new Survey();
    survey.setTitle("Team Feedback");
    survey = surveyRepository.save(survey);

    for (int i = 1; i <= 5; i++) {
      Question q = new Question();
      q.setSurvey(survey);
      q.setQuestionText("Question " + i);
      q.setPosition(i);
      questionRepository.save(q);
    }

    List<Question> questions = questionRepository.findAll();
    assertThat(questions).hasSize(5);

    Response response = new Response();
    response.setSurvey(survey);
    response = responseRepository.save(response);

    for (Question q : questions) {
      Answer a = new Answer();
      a.setResponse(response);
      a.setQuestion(q);
      a.setAnswer(LikertScale.AGREE);
      answerRepository.save(a);
    }

    List<Answer> answers = answerRepository.findAll();
    assertThat(answers).hasSize(5);
    assertThat(answers.get(0).getAnswer()).isEqualTo(LikertScale.AGREE);

    assertThat(response.getSurvey().getId()).isEqualTo(survey.getId());
    assertThat(answers.get(0).getQuestion().getSurvey().getId()).isEqualTo(survey.getId());
  }
}
