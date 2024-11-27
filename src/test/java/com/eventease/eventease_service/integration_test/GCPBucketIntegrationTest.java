package com.eventease.eventease_service;

import com.eventease.eventease_service.config.GoogleCloudStorageConfig;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class GCPBucketIntegrationTest {

  @MockBean
  private Storage storage; // Mock the Storage bean

  @Autowired
  private GoogleCloudStorageConfig googleCloudStorageConfig;

  @Test
  public void testStorageBeanInitialization() throws Exception {
    // Verify that the storage bean is initialized
    Storage storageBean = googleCloudStorageConfig.storage();
    assertEquals(storage, storageBean, "Storage bean should match the mock");
  }

  @Test
  public void testFileUpload() {
    // Mock the bucket and blob behavior
    Bucket bucket = Mockito.mock(Bucket.class);
    Blob blob = Mockito.mock(Blob.class);

    Mockito.when(storage.get("4156-group-bucket")).thenReturn(bucket);
    Mockito.when(bucket.create(eq("test.txt"), any(byte[].class), eq("text/plain"))).thenReturn(blob);
    Mockito.when(blob.getMediaLink()).thenReturn("https://example.com/test.txt");

    // Simulate file upload
    String bucketName = "4156-group-bucket";
    String fileName = "test.txt";
    String fileContent = "Test content";
    Bucket mockBucket = storage.get(bucketName);
    Blob uploadedBlob = mockBucket.create(fileName, fileContent.getBytes(), "text/plain");
    String mediaLink = uploadedBlob.getMediaLink();

    // Assertions
    Mockito.verify(storage).get(bucketName);
    Mockito.verify(bucket).create(fileName, fileContent.getBytes(), "text/plain");
    assertEquals("https://example.com/test.txt", mediaLink, "The media link should match the mocked URL");
  }
}
