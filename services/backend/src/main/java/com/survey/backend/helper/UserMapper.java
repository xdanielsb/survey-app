package com.survey.backend.helper;

import com.survey.backend.dto.UserDTO;
import com.survey.backend.entity.User;
import java.util.List;

public class UserMapper {
  public static UserDTO toDTO(User user, List<String> roles) {
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
