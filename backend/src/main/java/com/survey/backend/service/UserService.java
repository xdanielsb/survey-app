package com.survey.backend.service;

import com.survey.backend.entity.User;
import com.survey.backend.respository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public User saveUser(String uid, String email) {
    return userRepository
        .findByUid(uid)
        .map(
            existing -> {
              existing.setEmail(email); // update email if changed
              return userRepository.save(existing);
            })
        .orElseGet(
            () -> {
              User user = User.builder().uid(uid).email(email).build();
              return userRepository.save(user);
            });
  }
}
