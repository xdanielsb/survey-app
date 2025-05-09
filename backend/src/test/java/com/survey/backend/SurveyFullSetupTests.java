package com.survey.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.survey.backend.entity.*;
import com.survey.backend.respository.AnswerRepository;
import com.survey.backend.respository.QuestionRepository;
import com.survey.backend.respository.ResponseRepository;
import com.survey.backend.respository.SurveyRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class SurveyFullSetupTests {

  @Autowired private SurveyRepository surveyRepository;

  @Autowired private QuestionRepository questionRepository;

  @Autowired private ResponseRepository responseRepository;

  @Autowired private AnswerRepository answerRepository;

  @Test
  void fullSurveySetupTest() {
    // 1. Create survey
    Survey survey = new Survey();
    survey.setTitle("Team Feedback");
    survey = surveyRepository.save(survey);

    // 2. Add 5 questions to the survey
    for (int i = 1; i <= 5; i++) {
      Question q = new Question();
      q.setSurvey(survey);
      q.setQuestionText("Question " + i);
      q.setPosition(i);
      questionRepository.save(q);
    }

    List<Question> questions = questionRepository.findAll();
    assertThat(questions).hasSize(5);

    // 3. Create response
    Response response = new Response();
    response.setSurvey(survey);
    response = responseRepository.save(response);

    // 4. Add answers for each question
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

    // Verify survey linkage
    assertThat(response.getSurvey().getId()).isEqualTo(survey.getId());
    assertThat(answers.get(0).getQuestion().getSurvey().getId()).isEqualTo(survey.getId());
  }
}
