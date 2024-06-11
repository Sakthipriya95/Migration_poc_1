/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * @author TUD1COB
 */
public class SSDAPIUtilTest {

  private static final Logger logger = LoggerFactory.getLogger(SSDAPIUtilTest.class);
  @Mock
  SSDAPIUtil ssdAPIUtil;

  /**
   *
   */
  @Test
  public void testJoinString() {
    String delimiter = "/";
    String value1 = "path";
    String value2 = "to";
    String value3 = "file";

    String expected = "path/to/file";
    String result = SSDAPIUtil.joinString(delimiter, value1, value2, value3);

    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testJoinStringWithDifferentDelimiter() {
    String delimiter = "-";
    String value1 = "Hello";
    String value2 = "World";

    String expected = "Hello-World";
    String result = SSDAPIUtil.joinString(delimiter, value1, value2);

    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testJoinStringWithEmptyValues() {
    String delimiter = "-";
    String[] values = new String[0];

    String expected = "";
    String result = SSDAPIUtil.joinString(delimiter, values);

    Assert.assertEquals(expected, result);
  }

  /**
   *
   */
  @Test
  public void testGetCurrentDate() {
    String actualDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(LocalDate.now());
    assertEquals(SSDAPIUtil.getCurrentDate(), actualDate);
  }

  /**
   *
   */
  @Test
  public void isValid_ValidValue_ReturnsTrue() {
    String value = "Hello";
    boolean result = SSDAPIUtil.isValid(value);
    assertTrue(result);
  }

  /**
   *
   */
  @Test
  public void isValid_NullValue_ReturnsFalse() {
    String value = null;
    boolean result = SSDAPIUtil.isValid(value);
    assertFalse(result);
  }

  /**
   *
   */
  @Test
  public void isValid_EmptyValue_ReturnsFalse() {
    String value = "";
    boolean result = SSDAPIUtil.isValid(value);
    assertFalse(result);
  }


  /**
   * @throws IOException e
   */
  @Test
  public void loadFileAsResource_ExistingFile_ReturnsValidResource() throws IOException {
    // Arrange
    Path filePath = Paths.get("././TestFiles/OSS_Latest.pdf");
    // Act
    Resource actualResource = SSDAPIUtil.loadFileAsResource(filePath, logger);
    // Assert
    assertNotNull(actualResource);
  }

  /**
   *
   */
  @Test
  public void loadFileAsResource_NonExistentFile_ThrowsIOException() {
    Path filePath = Paths.get("././TestFiles/OSS_Latest2.pdf");
    assertThrows(IOException.class, () -> SSDAPIUtil.loadFileAsResource(filePath, logger));

  }

  /**
   *
   */
  @Test
  public void loadFileAsResource_InvalidFilePath_ThrowsIOException() {
    Path filePath = Paths.get("path/to/invalid/file");
    assertThrows(IOException.class, () -> SSDAPIUtil.loadFileAsResource(filePath, logger));

  }
}
