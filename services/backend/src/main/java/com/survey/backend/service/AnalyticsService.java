package com.survey.backend.service;

import com.survey.backend.dto.AiInsightsDTO;
import com.survey.backend.dto.SurveyResultDTO;
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

  public AiInsightsDTO analyzeSurvey(SurveyResultDTO result) {
    String url = analyticsUrl + "/analyze";
    ResponseEntity<AiInsightsDTO> response =
        restTemplate.postForEntity(url, result, AiInsightsDTO.class);
    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new IllegalStateException("Failed to get AI insights");
    }
    return response.getBody();
  }
}
