/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;

/**
 * @author TRL1COB
 */
public class FileNameUtilTest extends JUnitTest {

  /**
   * Regex to format file name with spl chars
   */
  private static final String SPL_CHAR_PTRN = "[/:<?>|\\*]";

  /**
   * Regex to format file name with invalid chars
   */
  private static final String INVALID_CHAR_PTRN = "[^a-zA-Z0-9]+";


  /**
   * Test Method for {@link FileNameUtil#formatFileName(String, String)}
   */
  @Test
  public void testFormatFileName() {
    String fileName = "<TE%S@T*>";
    assertEquals("_TE%S@T__", FileNameUtil.formatFileName(fileName, SPL_CHAR_PTRN));
    assertEquals("_TE_S_T_", FileNameUtil.formatFileName(fileName, INVALID_CHAR_PTRN));
  }

  /**
   * Test Method for {@link FileNameUtil#formatFileName(String, String)} with filename as null
   */
  @Test
  public void testNullFormatFileName() {
    assertNull(FileNameUtil.formatFileName(null, SPL_CHAR_PTRN));
    assertNull(FileNameUtil.formatFileName(null, INVALID_CHAR_PTRN));
  }

  /**
   * Test Method for {@link FileNameUtil#formatFileName(String, String)} with empty filename
   */
  @Test
  public void testEmptyFormatFileName() {
    assertEquals("", FileNameUtil.formatFileName("", SPL_CHAR_PTRN));
    assertEquals("", FileNameUtil.formatFileName("", INVALID_CHAR_PTRN));
  }

}
