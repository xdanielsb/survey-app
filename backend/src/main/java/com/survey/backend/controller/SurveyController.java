package com.survey.backend.controller;

import com.survey.backend.dto.CreateSurveyDTO;
import com.survey.backend.dto.SurveyDTO;
import com.survey.backend.dto.SurveyResponseDTO;
import com.survey.backend.dto.SurveyResultDTO;
import com.survey.backend.security.RequireRole;
import com.survey.backend.security.RoleType;
import com.survey.backend.service.SurveyService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/surveys")
@RequiredArgsConstructor
public class SurveyController {

  private final SurveyService surveyService;

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

  @RequireRole({RoleType.MANAGER, RoleType.ADMIN})
  @PostMapping("/create")
  public ResponseEntity<SurveyDTO> createSurvey(@RequestBody CreateSurveyDTO dto) {
    SurveyDTO created = surveyService.createSurvey(dto);
    return ResponseEntity.ok(created);
  }

  @GetMapping
  public ResponseEntity<List<SurveyDTO>> getAllSurveys() {
    List<SurveyDTO> surveys = surveyService.getAllSurveys();
    return ResponseEntity.ok(surveys);
  }

  @RequireRole({RoleType.ADMIN})
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
    surveyService.deleteSurvey(id);
    return ResponseEntity.noContent().build();
  }
}
