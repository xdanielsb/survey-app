package com.survey.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.survey.backend.entity.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  @Test
  @Transactional
  void decrementCreditIfAvailable_shouldDecrementCreditsWhenAvailable() {
    // given
    User user = new User();
    user.setUid("uid123");
    user.setEmail("email@example.com");
    user.setSurveyCredits(2);
    user = userRepository.save(user);

    entityManager.flush();
    entityManager.clear();

    // when
    int updatedRows = userRepository.decrementCreditIfAvailable(user.getId());

    // then
    assertThat(updatedRows).isEqualTo(1);
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getSurveyCredits()).isEqualTo(1);
  }

  @Test
  @Transactional
  void decrementCreditIfAvailable_shouldDoNothingWhenCreditsAreZero() {
    // given
    User user = new User();
    user.setUid("uid124");
    user.setEmail("email@example.com");
    user.setSurveyCredits(0);
    user = userRepository.save(user);

    // when
    int updatedRows = userRepository.decrementCreditIfAvailable(user.getId());

    // then
    assertThat(updatedRows).isEqualTo(0);
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertThat(updatedUser.getSurveyCredits()).isEqualTo(0);
  }
}
