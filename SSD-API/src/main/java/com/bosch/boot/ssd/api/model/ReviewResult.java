/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.boot.ssd.api.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ICP1COB
 */
public class ReviewResult implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -7848386451827542758L;

  @JsonProperty("SSDID")
  private String ssdId;

  private String reviewIDStr;

  private String returnMsg;

  @JsonProperty("DirectLink")
  private String directLink;


  /**
   * @return the ssdId
   */
  public String getSsdId() {
    return ssdId;
  }


  /**
   * @param ssdId the ssdId to set
   */
  public void setSsdId(String ssdId) {
    this.ssdId = ssdId;
  }


  /**
   * @return the reviewIDStr
   */
  public String getReviewIDStr() {
    return reviewIDStr;
  }


  /**
   * @param reviewIDStr the reviewIDStr to set
   */
  public void setReviewIDStr(String reviewIDStr) {
    this.reviewIDStr = reviewIDStr;
  }


  /**
   * @return the returnMsg
   */
  public String getReturnMsg() {
    return returnMsg;
  }


  /**
   * @param returnMsg the returnMsg to set
   */
  public void setReturnMsg(String returnMsg) {
    this.returnMsg = returnMsg;
  }


  /**
   * @return the directLink
   */
  public String getDirectLink() {
    return directLink;
  }


  /**
   * @param directLink the directLink to set
   */
  public void setDirectLink(String directLink) {
    this.directLink = directLink;
  }
}