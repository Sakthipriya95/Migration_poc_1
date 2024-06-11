/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author dmr1cob
 */
public class UnMappedBcsData {

  private String bcVersion;

  private Long nodeLevel;


  /**
   * @return the bcVersion
   */
  public String getBcVersion() {
    return this.bcVersion;
  }


  /**
   * @param bcVersion the bcVersion to set
   */
  public void setBcVersion(final String bcVersion) {
    this.bcVersion = bcVersion;
  }


  /**
   * @return the nodeLevel
   */
  public Long getNodeLevel() {
    return this.nodeLevel;
  }


  /**
   * @param nodeLevel the nodeLevel to set
   */
  public void setNodeLevel(final Long nodeLevel) {
    this.nodeLevel = nodeLevel;
  }
}
