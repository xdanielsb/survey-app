package com.survey.backend.repository;

import com.survey.backend.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUid(String uid);

  @Modifying
  @Query(
      """
     UPDATE User u
        SET u.surveyCredits = u.surveyCredits - 1
      WHERE u.id = :id AND u.surveyCredits > 0
  """)
  int decrementCreditIfAvailable(Long id);
}
