/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author MSP5COB
 */
public class DataAssessSharePointUploadInputModel {

  private String userName;
  private String encryptedPassword;
  private Long baselineId;
  private String sharePointUrl;
  private String sharePointAttrName;

  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(final String userName) {
    this.userName = userName;
  }

  /**
   * @return the encryptedPassword
   */
  public String getEncryptedPassword() {
    return this.encryptedPassword;
  }

  /**
   * @param encryptedPassword the encryptedPassword to set
   */
  public void setEncryptedPassword(final String encryptedPassword) {
    this.encryptedPassword = encryptedPassword;
  }

  /**
   * @return the baselineId
   */
  public Long getBaselineId() {
    return this.baselineId;
  }

  /**
   * @param baselineId the baselineId to set
   */
  public void setBaselineId(final Long baselineId) {
    this.baselineId = baselineId;
  }


  /**
   * @return the sharePointUrl
   */
  public String getSharePointUrl() {
    return this.sharePointUrl;
  }


  /**
   * @param sharePointUrl the sharePointUrl to set
   */
  public void setSharePointUrl(final String sharePointUrl) {
    this.sharePointUrl = sharePointUrl;
  }


  /**
   * @return the sharePointAttrName
   */
  public String getSharePointAttrName() {
    return this.sharePointAttrName;
  }


  /**
   * @param sharePointAttrName the sharePointAttrName to set
   */
  public void setSharePointAttrName(final String sharePointAttrName) {
    this.sharePointAttrName = sharePointAttrName;
  }

}
