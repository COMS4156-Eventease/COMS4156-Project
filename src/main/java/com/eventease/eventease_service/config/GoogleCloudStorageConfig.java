package com.eventease.eventease_service.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Configuration
@Getter
public class GoogleCloudStorageConfig {

  private static final Logger logger = LoggerFactory.getLogger(GoogleCloudStorageConfig.class);

  @Value("${gcp.credentials}")
  private String credentialsJson;

  @Bean
  public Storage storage() throws IOException {
    if (credentialsJson == null || credentialsJson.isEmpty()) {
      logger.error("GCP credentials are missing or empty. Please set the 'gcp.credentials' property.");
      throw new IllegalStateException("GCP credentials are missing or empty.");
    }

    GoogleCredentials credentials = GoogleCredentials.fromStream(
            new ByteArrayInputStream(credentialsJson.getBytes())
    );
    logger.info("Successfully loaded Google Cloud Storage credentials.");
    return StorageOptions.newBuilder().setCredentials(credentials).build().getService();
  }
}
