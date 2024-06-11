/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author bne4cob
 */
public class TestDecryptor extends JUnitTest {

  /**
   * Sample password for testing
   */
  private static final String SAMPLE_TEST_PSWRD = "SamplePassword334341##$#$";

  /**
   * Test decrypt() method.
   *
   * @throws InvalidKeyException exception during encryption
   * @throws IllegalBlockSizeException exception during encryption
   * @throws BadPaddingException exception during encryption
   * @throws NoSuchAlgorithmException exception during encryption
   * @throws NoSuchPaddingException exception during encryption
   */
  @Test
  public void test01()
      throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException,
      NoSuchPaddingException {

    // Encrypt a password
    String encPassword = CryptUtil.INSTANCE.encrypt(SAMPLE_TEST_PSWRD);
    assertNotNull("Encrypted password", encPassword);

    String decPasswd = Decryptor.INSTANCE.decrypt(encPassword, AUT_LOGGER);
    assertEquals("Decrypted password", SAMPLE_TEST_PSWRD, decPasswd);
  }
}
