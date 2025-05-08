package com.survey.backend.service;

import com.survey.backend.entity.Survey;
import com.survey.backend.respository.SurveyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SurveyService {
    private final SurveyRepository surveyRepo;

    public SurveyService(SurveyRepository surveyRepo) {
        this.surveyRepo = surveyRepo;
    }

    public Optional<Survey> getSurveyById(Long id) {
        return surveyRepo.findById(id);
    }
}
