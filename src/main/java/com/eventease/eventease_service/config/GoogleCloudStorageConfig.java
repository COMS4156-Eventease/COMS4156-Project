package com.eventease.eventease_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleCloudStorageConfig {

  @Bean
  public Storage storage() throws IOException {
    // Load environment variables from .env file
    //
//    Dotenv dotenv = Dotenv.configure().load();
//    String credentialsJson = dotenv.get("GCP_CREDENTIALS");
    String credentialsJson = System.getenv("GCP_CREDENTIALS");
    if (credentialsJson == null || credentialsJson.isEmpty()) {
      throw new IllegalStateException("Environment variable GCP_CREDENTIALS is not set or empty.");
    }

    GoogleCredentials credentials = GoogleCredentials.fromStream(
        new ByteArrayInputStream(credentialsJson.getBytes())
    );
    return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
  }
}
