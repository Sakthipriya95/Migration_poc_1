/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import com.bosch.calcomp.junittestframework.JUnitTest;


/**
 * @author bne4cob
 */
public class TestEncryptTool extends JUnitTest {

  /**
   * Sample password for testing
   */
  private static final String SAMPLE_TEST_PSWRD = "SamplePassword334341##$#$";

  /**
   * Test main() method. Test with proper arguments
   */
  @Test
  public void test01() {
    // Output of the tool is available only in system out. Hence for testing, system out should be read and interpreted
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream newPrintStream = new PrintStream(baos);

    // Backup old system out
    PrintStream psBackup = System.out;
    // Set custom system out
    System.setOut(newPrintStream);

    EncryptTool.main(new String[] { SAMPLE_TEST_PSWRD });

    // Revert the system out
    System.out.flush();
    System.setOut(psBackup);

    // Find the encrypted message from the captured system out
    // Expected format of message is :
    // Input text : <un encrypted password><new line>Encrypted text : <encrypted password>
    String sysoutMsg = baos.toString();
    AUT_LOGGER.info("Tool output");
    AUT_LOGGER.info(sysoutMsg);

    String encPswd = sysoutMsg.split("\n")[1].split(":")[1];
    encPswd = encPswd.trim();

    // Decrypt the password for testing
    String decPswd = Decryptor.getInstance().decrypt(encPswd, AUT_LOGGER);
    assertEquals("Decrypted password", SAMPLE_TEST_PSWRD, decPswd);
  }

  /**
   * Test main() method. Test with invalid arguments
   */
  @Test
  public void test02() {
    // Output of the tool is available only in system out.
    // Hence for testing, system out should be read and interpreted
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream newPrintStream = new PrintStream(baos);

    // Backup old system out
    PrintStream psBackup = System.err;
    // Set custom system out
    System.setOut(newPrintStream);

    // Invalid : no arguments
    EncryptTool.main(new String[] {});

    // Revert the system out
    System.out.flush();
    System.setOut(psBackup);

    // Find the encrypted message from the captured system out
    // Expected format of message is :
    // Error :Invalid arguements<new line> Usage :java -jar EncryptTool.jar <text enclosed in double quotes>
    String sysoutMsg = baos.toString();
    AUT_LOGGER.info("Tool output");
    AUT_LOGGER.info(sysoutMsg);

    // Decrypt the password for testing
    assertTrue("Test empty tool output",
        "".equals(sysoutMsg.trim()) || sysoutMsg.contains("Error :Invalid arguements"));
  }
}
