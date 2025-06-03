package com.survey.backend.command;

import com.survey.backend.entity.Question;
import com.survey.backend.entity.Survey;
import com.survey.backend.repository.SurveyRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;
import org.jline.utils.AttributedString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@Profile("shell")
@ShellComponent
public class ShellSurveyCommands {

  private static final Logger log = LoggerFactory.getLogger(ShellSurveyCommands.class);
  private final SurveyRepository repo;

  public ShellSurveyCommands(SurveyRepository repo) {
    this.repo = repo;
  }

  @ShellMethod("Generate random surveys")
  public AttributedString generateSurveys(@ShellOption(defaultValue = "5") int count) {

    LocalDateTime baseTime = LocalDateTime.now();

    for (int i = 1; i <= count; i++) {
      LocalDateTime timestamp = baseTime.plusSeconds(i); // avoid identical timestamps

      Survey s = Survey.builder().title("Shell Survey " + timestamp).createdAt(timestamp).build();

      List<Question> questions =
          IntStream.rangeClosed(1, 5)
              .mapToObj(
                  q ->
                      Question.builder()
                          .questionText("CLI Q" + q + " for " + s.getTitle())
                          .position(q)
                          .survey(s)
                          .build())
              .toList();

      s.setQuestions(questions);
      repo.save(s);
    }
    log.info("Generated {} surveys", count);
    return new AttributedString("Generated " + count + " surveys.");
  }
}
