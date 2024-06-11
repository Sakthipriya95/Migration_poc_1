/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.controller.test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;

import com.bosch.boot.ssd.api.controller.FileController;
import com.bosch.boot.ssd.api.exception.FileChecksumException;
import com.bosch.boot.ssd.api.exception.ParameterInvalidException;
import com.bosch.boot.ssd.api.exception.ResourceNotFoundException;
import com.bosch.boot.ssd.api.service.FileService;

/**
 * @author TAB1JA
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class FileControllerTest {
  
  @InjectMocks
  private FileController fileController;
  
  @Mock
  private FileService fileStorageService;

  private String toolName;

  private String fileCategory;

  private String fileNameWithExtn;

  private Long fileCheckSum;

  private Path filePath;
  

  private static final Logger logger = LoggerFactory.getLogger(FileController.class);
  
  /**
   * 
   */
  @Before()
  public void setUp() {
    this.toolName = "ssd";
    this.fileCategory = "oss";
    this.fileNameWithExtn = "OSS_Latest.pdf";
    this.fileCheckSum = 0L;
    this.filePath=Paths.get("TestFiles\\").resolve("TestFiles\\OSS_Latest.pdf");
  }
  
  /**
   * 
   */
  @Test
  public void fileStorageServiceTest() {
    Resource resource;
    try {
      resource = new UrlResource(this.filePath.toUri());
      when(this.fileStorageService.getResource(this.fileNameWithExtn, this.toolName, this.fileCategory, logger)).thenReturn(resource);
      ResponseEntity<Object> responseEntity= this.fileController.getFile(this.fileNameWithExtn, this.toolName,  this.fileCategory, this.fileCheckSum);
      assertEquals(200, responseEntity.getStatusCodeValue());
    }
    catch (ParameterInvalidException | IOException | FileChecksumException | ResourceNotFoundException e) {
      e.printStackTrace();
    }  
  }
  
  
}
