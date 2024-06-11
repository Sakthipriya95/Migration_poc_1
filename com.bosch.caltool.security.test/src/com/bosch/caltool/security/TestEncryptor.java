/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author dmo5cob
 */
public class TestEncryptor extends JUnitTest {

  /**
   * Sample password for testing
   */
  private static final String SAMPLE_TEST_PSWRD = "SamplePassword334341##$#$";

  /**
   * Test encrypt() method.
   */
  @Test
  public void test01() {

    // Encrypt a password
    String encPassword = Encryptor.INSTANCE.encrypt(SAMPLE_TEST_PSWRD, AUT_LOGGER);
    assertNotNull("Encrypted password", encPassword);

    String decPasswd = Decryptor.INSTANCE.decrypt(encPassword, AUT_LOGGER);
    assertEquals("Decrypted password", SAMPLE_TEST_PSWRD, decPasswd);
  }
}
