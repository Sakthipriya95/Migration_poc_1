/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.calcomp.ssd.api.client.model;

/**
 * @author SMN6KOR
 */
public class ReviewResultModel {

  private final String jsonString;

  private String ssdId;

  private String reviewId;

  private String returnMsg;

  private String directLink;


  /**
   * @param jsonObject service response
   */
  public ReviewResultModel(final String jsonString) {
    this.jsonString = jsonString;
  }

  /**
   * @return the jsonObject
   */
  public String getJsonObject() {
    return this.jsonString;
  }

}