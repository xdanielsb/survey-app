package com.survey.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class FirebaseConfig {

  @PostConstruct
  public void init() throws IOException {
    try {
      if (FirebaseApp.getApps().isEmpty()) {
        InputStream serviceAccount =
            getClass().getClassLoader().getResourceAsStream("firebase-service-account.json");

        if (serviceAccount == null) {
          throw new FileNotFoundException("firebase-service-account.json not found in classpath");
        }

        FirebaseOptions options =
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
        System.out.println("Firebase initialized successfully.");
      }
    } catch (IOException e) {
      System.err.println("Failed to initialize Firebase: " + e.getMessage());
      e.printStackTrace();
      throw new IllegalStateException("Firebase initialization error", e);
    }
  }
}
