/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;


/**
 * This class defines the error codes for the command exception. <br>
 * The range is from -9100 to -9199
 *
 * @author bne4cob
 */
public final class CommandErrorCodes {

  /**
   * Error constant for the Attribute, which as depedencies
   */
  public static final String ATTR_HAS_DEP = "-9100";
  /**
   * Error constant for the Attribute which has value dependencies
   */
  public static final String ATTR_HAS_VAL_DEP = "-9101";
  /**
   * Error constant for the parsing invalid date formats
   */
  public static final String PRSING_INVLD_DATE = "-9102";

  /**
   * Error code for errors when serializing data
   */
  public static final String ERR_SERIALIZE_DATA = "-9103";


  /**
   * Private constructor
   */
  private CommandErrorCodes() {
    // Do nothing
  }

}