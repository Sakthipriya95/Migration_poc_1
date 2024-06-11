/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.util;


/**
 * @author rgo7cob Icdm-569
 */
public enum WelcomePagePref {


  /**
   * Show the Welcome Page for the User
   */
  SHOW_WELCOME_PAGE("Yes"),
  /**
   * Do not show the Welcome Page for the User
   */
  NO_WELCOME_PAGE("No");

  private String text;


  WelcomePagePref(final String text) {
    this.text = text;
  }

  /**
   * @return the Text yes or no
   */
  public String getText() {
    return this.text;
  }

  /**
   * For later use if needed
   * 
   * @param text text
   * @return the Pref
   */
  public static WelcomePagePref getWelcomePagePref(final String text) {
    for (WelcomePagePref l : WelcomePagePref.values()) {
      if (l.text.equals(text)) {
        return l;
      }
    }
    return SHOW_WELCOME_PAGE;
  }

}
