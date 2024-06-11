/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.service.test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import com.bosch.boot.ssd.api.service.FileService;

class FileServiceTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Test
  public void checkIfIdenticalFiles_IdenticalFiles_ReturnsTrue() {
    long inputCheckSumValue = 2442981939L;
    File fileInServer = new File("././TestFiles/OSS_Latest.pdf");
    boolean result = new FileService().checkIfIdenticalFiles(inputCheckSumValue, fileInServer);
    assertTrue(result);
  }

  @Test
  public void checkIfIdenticalFiles_IdenticalFiles_ReturnsFalse() {
    long inputCheckSumValue = 12345L;
    File fileInServer = new File("././TestFiles/OSS_Latest.pdf");
    boolean result = new FileService().checkIfIdenticalFiles(inputCheckSumValue, fileInServer);
    assertFalse(result);
  }

  @Test
  public void checkIfIdenticalFiles_IdenticalFiles_CheckSumValueZero() {
    long inputCheckSumValue = 0;
    File fileInServer = new File("././TestFiles/OSS_Latest.pdf");
    boolean result = new FileService().checkIfIdenticalFiles(inputCheckSumValue, fileInServer);
    assertFalse(result);
  }

  @Test
  void checkIfIdenticalFiles_IOException_ReturnsFalse() {
    long inputCheckSumValue = 12345L;
    File fileInServer = new File("path/to/existing/file");
    boolean result = new FileService().checkIfIdenticalFiles(inputCheckSumValue, fileInServer);
    assertFalse(result);
  }

}

