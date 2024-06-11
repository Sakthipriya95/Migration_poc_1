/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;


/**
 * @author dmr1cob
 */
public class LevelAttrInfoV2 {

  private long levelNo;

  private long levelAttrId;

  private long levelAttrValueId;

  private String levelName;


  /**
   * @return the levelNo
   */
  public long getLevelNo() {
    return levelNo;
  }


  /**
   * @param levelNo the levelNo to set
   */
  public void setLevelNo(long levelNo) {
    this.levelNo = levelNo;
  }


  /**
   * @return the levelAttrId
   */
  public long getLevelAttrId() {
    return levelAttrId;
  }


  /**
   * @param levelAttrId the levelAttrId to set
   */
  public void setLevelAttrId(long levelAttrId) {
    this.levelAttrId = levelAttrId;
  }


  /**
   * @return the levelAttrValueId
   */
  public long getLevelAttrValueId() {
    return levelAttrValueId;
  }


  /**
   * @param levelAttrValueId the levelAttrValueId to set
   */
  public void setLevelAttrValueId(long levelAttrValueId) {
    this.levelAttrValueId = levelAttrValueId;
  }


  /**
   * @return the levelName
   */
  public String getLevelName() {
    return levelName;
  }


  /**
   * @param levelName the levelName to set
   */
  public void setLevelName(String levelName) {
    this.levelName = levelName;
  }
}
