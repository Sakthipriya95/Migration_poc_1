/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.ssdfeature;

/**
 * @author dja7cob
 */
public class FeatureValue {

  private Long id;

  private Long featureId;

  private Long attrValId;

  private String name;

  private String attrValName;

  private String usedFlag;

  /**
   * @return the featValId
   */
  public Long getId() {
    return this.id;
  }


  /**
   * @param featValId the featValId to set
   */
  public void setId(final Long featValId) {
    this.id = featValId;
  }


  /**
   * @return the featureId
   */
  public Long getFeatureId() {
    return this.featureId;
  }


  /**
   * @param featureId the featureId to set
   */
  public void setFeatureId(final Long featureId) {
    this.featureId = featureId;
  }


  /**
   * @return the attrValId
   */
  public Long getAttrValId() {
    return this.attrValId;
  }


  /**
   * @param attrValId the attrValId to set
   */
  public void setAttrValId(final Long attrValId) {
    this.attrValId = attrValId;
  }


  /**
   * @return the featValName
   */
  public String getName() {
    return this.name;
  }


  /**
   * @param featValName the featValName to set
   */
  public void setName(final String featValName) {
    this.name = featValName;
  }


  /**
   * @return the attrValName
   */
  public String getAttrValName() {
    return this.attrValName;
  }


  /**
   * @param attrValName the attrValName to set
   */
  public void setAttrValName(final String attrValName) {
    this.attrValName = attrValName;
  }


  /**
   * @return the usedFlag
   */
  public String getUsedFlag() {
    return usedFlag;
  }


  /**
   * @param usedFlag the usedFlag to set
   */
  public void setUsedFlag(String usedFlag) {
    this.usedFlag = usedFlag;
  }

}
