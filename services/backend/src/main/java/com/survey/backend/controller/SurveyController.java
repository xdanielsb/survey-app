package com.survey.backend.controller;

import com.survey.backend.dto.*;
import com.survey.backend.helper.SurveyMapper;
import com.survey.backend.security.KeycloakRole;
import com.survey.backend.security.RequireAuth;
import com.survey.backend.security.RequireRole;
import com.survey.backend.service.AnalyticsService;
import com.survey.backend.service.SurveyService;
import com.survey.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {

  private final SurveyService surveyService;
  private final UserService userService;
  private final AnalyticsService analyticsService;

  @GetMapping("/{id}")
  public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Long id) {
    return surveyService
        .getSurveyById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{id}/responses")
  public ResponseEntity<Void> submitResponse(
      @PathVariable Long id, @RequestBody SurveyResponseDTO responseDTO) {

    boolean success = surveyService.saveSurveyResponse(id, responseDTO);
    return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }

  @GetMapping("/{id}/results")
  public ResponseEntity<SurveyResultDTO> getResults(@PathVariable Long id) {
    return surveyService
        .getSurveyResults(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{id}/chat")
  public ResponseEntity<ChatResponseDTO> chat(
      @PathVariable Long id, @RequestBody ChatRequestDTO request) {
    ChatResponseDTO resp = analyticsService.askQuestion(request);
    return ResponseEntity.ok(resp);
  }

  @RequireAuth
  @PostMapping("/create")
  public ResponseEntity<SurveyDTO> createSurvey(@RequestBody CreateSurveyDTO dto) {
    SurveyDTO created = SurveyMapper.toDTO(surveyService.createSurvey(dto));
    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<Page<SurveyDTO>> getSurveys(
      Pageable pageable, @RequestParam(required = false, name = "q") String query) {
    return ResponseEntity.ok(surveyService.getAllSurveys(pageable, query));
  }

  @RequireRole({KeycloakRole.ADMIN})
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
    surveyService.deleteSurvey(id);
    return ResponseEntity.noContent().build();
  }
}
