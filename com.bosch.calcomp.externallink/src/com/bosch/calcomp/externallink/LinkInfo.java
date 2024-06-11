/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.externallink;


/**
 * @author bne4cob
 */
public class LinkInfo {

  /**
   * Link URL
   */
  private String url;

  /**
   * Display text of the link
   */
  private String displayText;

  /**
   * @return the url
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(final String url) {
    this.url = url;
  }

  /**
   * @return the displayText
   */
  public String getDisplayText() {
    return this.displayText;
  }

  /**
   * @param displayText the displayText to set
   */
  public void setDisplayText(final String displayText) {
    this.displayText = displayText;
  }

}
