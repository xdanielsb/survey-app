package com.survey.backend.service;

import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
import com.survey.backend.respository.RoleRepository;
import com.survey.backend.respository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

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
              Role customerRole =
                  roleRepository
                      .findByName("CUSTOMER")
                      .orElseThrow(() -> new IllegalStateException("CUSTOMER role not found"));
              User user = User.builder().uid(uid).email(email).roles(Set.of(customerRole)).build();
              return userRepository.save(user);
            });
  }
}
