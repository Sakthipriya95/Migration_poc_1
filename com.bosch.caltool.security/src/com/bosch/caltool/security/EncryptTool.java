/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.security;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;


/**
 * Main class for text encryption.
 * <p>
 * Usage : <code>java EncryptTool &lt;text enclosed in double quotes&gt;</code>
 *
 * @author bne4cob
 */
public final class EncryptTool {

  /**
   * Valid arguments count
   */
  private static final int VALID_ARG_COUNT = 1;

  /**
   * Logger
   */
  private static final ILoggerAdapter LOGGER = new Log4JLoggerAdapterImpl("C:\\Temp\\EncryptTool.log");

  /**
   * Private constructor
   */
  private EncryptTool() {
    // Private constructor
  }

  /**
   * Main method.
   *
   * @param args arguements
   */
  public static void main(final String[] args) {
    if (args.length != VALID_ARG_COUNT) {
      LOGGER.error("Error :Invalid arguements");
      LOGGER.error("  Usage :java -jar EncryptTool.jar <text enclosed in double quotes>");
      return;
    }

    System.out.println("Input text \t: " + args[0]);

    // Encrypt the input text and print the output to console
    System.out.println("Encrypted text \t: " + Encryptor.getInstance().encrypt(args[0], LOGGER));


  }
}
