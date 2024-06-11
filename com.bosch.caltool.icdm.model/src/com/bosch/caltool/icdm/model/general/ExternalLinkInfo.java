/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.general;


/**
 * @author bne4cob
 */
public class ExternalLinkInfo {

  private String linkType;
  private Long linkableObjId;

  private String url;
  private String displayText;

  /**
   * @return the linkType
   */
  public String getLinkType() {
    return this.linkType;
  }

  /**
   * @param linkType the linkType to set
   */
  public void setLinkType(final String linkType) {
    this.linkType = linkType;
  }


  /**
   * @return the linkableObjId
   */
  public Long getLinkableObjId() {
    return this.linkableObjId;
  }

  /**
   * @param linkableObjId the linkableObjId to set
   */
  public void setLinkableObjId(final Long linkableObjId) {
    this.linkableObjId = linkableObjId;
  }

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
