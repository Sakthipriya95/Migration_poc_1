/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.general;


/**
 * @author ukt1cob
 */
public class MessageCodeUtils {


  /**
   * seperator that append the groupname and name of message code
   */
  public static final String MESSAGECODE_SEPARATOR = ".";

  /**
   * Private constructor for utility class
   */
  private MessageCodeUtils() {
    // Private constructor for utility class
  }

  // method to create code - GroupName.Name
  public static String createMessageCode(final String groupName, final String name) {
    return groupName + MESSAGECODE_SEPARATOR + name;
  }

}
