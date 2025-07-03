package com.survey.backend.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LoginResponseDTO {
  private String token;
  private List<String> roles;
  private boolean isPremium;
}
