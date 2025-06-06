package com.survey.backend.repository;

import com.survey.backend.entity.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
  @Modifying
  @Query("update Survey s set s.responseCount = s.responseCount + 1 where s.id = :id")
  int incrementResponseCount(@Param("id") Long id);
}
