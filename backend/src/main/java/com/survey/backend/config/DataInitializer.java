package com.survey.backend.config;

import com.survey.backend.entity.Question;
import com.survey.backend.entity.Survey;
import com.survey.backend.respository.SurveyRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {

  private final SurveyRepository surveyRepository;

  @PostConstruct
  public void init() {
    // Clean existing data
    if (surveyRepository.count() > 0) return;
    // Survey 1
    Survey customerSurvey =
        Survey.builder()
            .title("Customer Satisfaction Survey")
            .createdAt(LocalDateTime.now())
            .build();

    List<Question> customerQuestions =
        List.of(
            Question.builder()
                .questionText("The service met my expectations.")
                .survey(customerSurvey)
                .position(1)
                .build(),
            Question.builder()
                .questionText("Staff was helpful and friendly.")
                .survey(customerSurvey)
                .position(2)
                .build(),
            Question.builder()
                .questionText("The product quality is satisfactory.")
                .survey(customerSurvey)
                .position(3)
                .build(),
            Question.builder()
                .questionText("I would recommend this company.")
                .survey(customerSurvey)
                .position(4)
                .build(),
            Question.builder()
                .questionText("The price was reasonable.")
                .survey(customerSurvey)
                .position(5)
                .build());
    customerSurvey.setQuestions(customerQuestions);

    // Survey 2
    Survey employeeSurvey =
        Survey.builder().title("Employee Feedback Survey").createdAt(LocalDateTime.now()).build();

    List<Question> employeeQuestions =
        List.of(
            Question.builder()
                .questionText("I feel valued at work.")
                .survey(employeeSurvey)
                .position(1)
                .build(),
            Question.builder()
                .questionText("Communication in the company is effective.")
                .survey(employeeSurvey)
                .position(2)
                .build(),
            Question.builder()
                .questionText("I have growth opportunities.")
                .survey(employeeSurvey)
                .position(3)
                .build(),
            Question.builder()
                .questionText("Management supports my goals.")
                .survey(employeeSurvey)
                .position(4)
                .build(),
            Question.builder()
                .questionText("The work environment is healthy.")
                .survey(employeeSurvey)
                .position(5)
                .build());
    employeeSurvey.setQuestions(employeeQuestions);

    // Survey 3
    Survey productSurvey =
        Survey.builder().title("Product Feedback Survey").createdAt(LocalDateTime.now()).build();

    List<Question> productQuestions =
        List.of(
            Question.builder()
                .questionText("The product meets my needs.")
                .survey(productSurvey)
                .position(1)
                .build(),
            Question.builder()
                .questionText("The design is appealing.")
                .survey(productSurvey)
                .position(2)
                .build(),
            Question.builder()
                .questionText("It is easy to use.")
                .survey(productSurvey)
                .position(3)
                .build(),
            Question.builder()
                .questionText("Support materials are helpful.")
                .survey(productSurvey)
                .position(4)
                .build(),
            Question.builder()
                .questionText("I am satisfied with the purchase.")
                .survey(productSurvey)
                .position(5)
                .build());
    productSurvey.setQuestions(productQuestions);

    // Save all surveys (cascades to questions)
    surveyRepository.saveAll(List.of(customerSurvey, employeeSurvey, productSurvey));
  }
}
