package com.survey.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.survey.backend.dto.ChatRequestDTO;
import com.survey.backend.dto.ChatResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

  @Mock private RestTemplate restTemplate;

  @InjectMocks private AnalyticsService analyticsService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(analyticsService, "analyticsUrl", "http://analytics");
  }

  @Test
  void askQuestion_returnsBody_whenResponseIsSuccessful() {
    ChatRequestDTO request = ChatRequestDTO.builder().question("hello?").build();
    ChatResponseDTO expected = ChatResponseDTO.builder().answer("hi").build();
    ResponseEntity<ChatResponseDTO> resp = new ResponseEntity<>(expected, HttpStatus.OK);

    when(restTemplate.postForEntity("http://analytics/ask", request, ChatResponseDTO.class))
        .thenReturn(resp);

    ChatResponseDTO result = analyticsService.askQuestion(request);

    assertEquals(expected, result);
    verify(restTemplate).postForEntity("http://analytics/ask", request, ChatResponseDTO.class);
  }

  @Test
  void askQuestion_throws_whenResponseInvalid() {
    ChatRequestDTO request = ChatRequestDTO.builder().question("why?").build();
    ResponseEntity<ChatResponseDTO> resp =
        new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    when(restTemplate.postForEntity("http://analytics/ask", request, ChatResponseDTO.class))
        .thenReturn(resp);

    assertThrows(IllegalStateException.class, () -> analyticsService.askQuestion(request));
  }
}
