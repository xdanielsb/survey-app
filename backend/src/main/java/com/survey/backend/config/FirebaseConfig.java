package com.survey.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
@Slf4j
public class FirebaseConfig {

  @PostConstruct
  public void init() throws IOException {
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        InputStream serviceAccount =
            getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

        if (serviceAccount == null) {
          log.error("firebase-service-account.json not found in classpath");
          throw new FileNotFoundException("firebase-service-account.json not found in classpath");
        }

        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        log.info("Firebase initialized successfully.");
      }
    } catch (IOException e) {
      log.error("Failed to initialize Firebase: " + e.getMessage());
      e.printStackTrace();
      throw new IllegalStateException("Firebase initialization error", e);
    }
  }
}
