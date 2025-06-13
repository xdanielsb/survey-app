package com.survey.backend.service;

import com.survey.backend.dto.ChatRequestDTO;
import com.survey.backend.dto.ChatResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
  private final RestTemplate restTemplate;

  @Value("${analytics.service.url}")
  private String analyticsUrl;

  public ChatResponseDTO askQuestion(ChatRequestDTO request) {
    String url = analyticsUrl + "/ask";
    ResponseEntity<ChatResponseDTO> response =
        restTemplate.postForEntity(url, request, ChatResponseDTO.class);
    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new IllegalStateException("Failed to get AI response");
    }
    return response.getBody();
  }
}
