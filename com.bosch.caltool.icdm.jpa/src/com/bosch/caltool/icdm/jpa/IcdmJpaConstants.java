/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

/**
 * @author adn1cob
 */
public final class IcdmJpaConstants {


  /**
   * Language key
   */
  public static final String LANGUAGE = "Language";


  /**
   * Label define for Preference language
   */
  public static final String LANGUAGE_SELECT = "Select the language";

  /**
   * Locking object for synchronziation of A2L Data provider loading, during startup
   */
  public static final Object A2L_DATA_SYNC_LOCK = new Object();

  /**
   * Locking object for synchronziation of Component package Data provider loading, during startup
   */
  public static final Object CP_DATA_SYNC_LOCK = new Object();


  /**
   * Private constructor for constant definitions class
   */
  private IcdmJpaConstants() {
    // Private constructor for constant definitions class
  }

}
