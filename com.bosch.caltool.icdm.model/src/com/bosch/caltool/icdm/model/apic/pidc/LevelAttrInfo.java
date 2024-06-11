/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class LevelAttrInfo {

  private Long levelNo;

  private Long levelAttrId;

  private Long levelAttrValueId;

  private String levelName;


  /**
   * @return the levelNo
   */
  public Long getLevelNo() {
    return this.levelNo;
  }


  /**
   * @param levelNo the levelNo to set
   */
  public void setLevelNo(final Long levelNo) {
    this.levelNo = levelNo;
  }


  /**
   * @return the levelAttrId
   */
  public Long getLevelAttrId() {
    return this.levelAttrId;
  }


  /**
   * @param levelAttrId the levelAttrId to set
   */
  public void setLevelAttrId(final Long levelAttrId) {
    this.levelAttrId = levelAttrId;
  }


  /**
   * @return the levelAttrValueId
   */
  public Long getLevelAttrValueId() {
    return this.levelAttrValueId;
  }


  /**
   * @param levelAttrValueId the levelAttrValueId to set
   */
  public void setLevelAttrValueId(final Long levelAttrValueId) {
    this.levelAttrValueId = levelAttrValueId;
  }


  /**
   * @return the levelName
   */
  public String getLevelName() {
    return this.levelName;
  }


  /**
   * @param levelName the levelName to set
   */
  public void setLevelName(final String levelName) {
    this.levelName = levelName;
  }
}
