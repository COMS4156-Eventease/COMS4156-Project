package com.eventease.eventease_service.service;

import com.eventease.eventease_service.exception.GCSUploadException;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {
  @Value("${gcs.bucket}")
  private String bucketName;

  private final Storage storage;

  @Autowired
  public ImageStorageService(Storage storage) {
    this.storage = storage;
  }

  public String save(MultipartFile file) throws GCSUploadException {
    String filename = UUID.randomUUID().toString();
    BlobInfo blobInfo = null;
    try {
      blobInfo = storage.createFrom(
          BlobInfo
              .newBuilder(bucketName, filename)
              .setContentType("image/jpeg")
              .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
              .build(),
          file.getInputStream());
    } catch (IOException exception) {
      throw new GCSUploadException("Failed to upload file to GCS");
    }
    return blobInfo.getMediaLink();
  }
}