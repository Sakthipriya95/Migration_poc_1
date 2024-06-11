/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * Provides util class to encryption and decryption. Uses AES algorithm. This is a singleton class.
 *
 * @author bne4cob
 */
enum CryptUtil {
                /**
                 * Unique instance for singleton class implementation
                 */
                INSTANCE;

  /**
   * Algorithm
   */
  private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

  /**
   * Key
   */
  // Byte array for string "This is a secret key"
  // Sample key
  private static final byte[] KEY_BYTE =
      { 0x74, 0x68, 0x69, 0x73, 0x49, 0x73, 0x41, 0x53, 0x65, 0x63, 0x72, 0x65, 0x74, 0x4b, 0x65, 0x79 };

  /**
   * Key object
   */
  private static final SecretKeySpec SECRET_KEY = new SecretKeySpec(KEY_BYTE, "AES");

  /**
   * @return the instance
   */
  public static CryptUtil getInstance() {
    return INSTANCE;
  }

  /**
   * @param strToEncrypt String to encrypt
   * @return the encrypted string
   * @throws BadPaddingException exception
   * @throws IllegalBlockSizeException exception
   * @throws NoSuchPaddingException exception
   * @throws NoSuchAlgorithmException exception
   * @throws InvalidKeyException exception
   */
  public String encrypt(final String strToEncrypt)
      throws IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException,
      InvalidKeyException {

    final Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);

    return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));


  }

  /**
   * @param strToDecrypt String to decrypt. The encrypted string should be the result of the method
   *          <code>CryptUtil.encrypt()</code>
   * @return decrypted string
   * @throws IOException refer BASE64Decoder.decodeBuffer() documentation
   * @throws BadPaddingException refer Cipher.doFinal() documentation
   * @throws IllegalBlockSizeException refer Cipher.doFinal() documentation
   * @throws NoSuchPaddingException refer Cipher.getInstance() documentation
   * @throws NoSuchAlgorithmException refer Cipher.getInstance() documentation
   * @throws InvalidKeyException refer Cipher.init() documentation
   */
  public String decrypt(final String strToDecrypt)
      throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException,
      IOException, InvalidKeyException {

    final Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
    cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);

    return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
  }
}