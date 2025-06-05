package com.survey.backend.helper;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.Role;
import com.survey.backend.entity.User;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
  public static UserDTO toDTO(User user) {
    List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
    return UserDTO.builder()
        .id(user.getId())
        .uid(user.getUid())
        .email(user.getEmail())
        .surveyCredits(user.getSurveyCredits())
        .premium(user.isPremium())
        .roles(roles)
        .build();
  }
}
