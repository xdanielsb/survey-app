package com.survey.backend.respository;

import com.survey.backend.entity.Answer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
  List<Answer> findByQuestionId(Long questionId);
}
