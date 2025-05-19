package com.survey.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class FirebaseConfig {

  @PostConstruct
  public void init() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      FileInputStream serviceAccount =
          new FileInputStream("src/main/resources/firebase-service-account.json");

      FirebaseOptions options =
          new FirebaseOptions.Builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      FirebaseApp.initializeApp(options);
    }
  }
}
