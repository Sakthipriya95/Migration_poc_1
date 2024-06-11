/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;


/**
 * @author apj4cob Model to take input from data checker client for compliance review
 */
public class CompliDstInput {

  /**
   * pidc A2l id
   */
  private Long pidcA2lId;

  /**
   * pidc element id-> Varaint id/ Pidc version id
   */
  private Long pidcElementId;
  /**
   * vcdm Dst revision id
   */
  private Long vcdmDstId;

  private String aprjName;

  private Integer aprjRevision;

  private String dstName;

  private Integer dstRevision;

  private boolean isCheckedIn;


  /**
   * @return the aprjRevision
   */
  public Integer getAprjRevision() {
    return this.aprjRevision;
  }


  /**
   * @return the aprjName
   */
  public String getAprjName() {
    return this.aprjName;
  }


  /**
   * @param aprjName the aprjName to set
   */
  public void setAprjName(final String aprjName) {
    this.aprjName = aprjName;
  }


  /**
   * @return the dstRevision
   */
  public Integer getDstRevision() {
    return this.dstRevision;
  }


  /**
   * @param dstRevision the dstRevision to set
   */
  public void setDstRevision(final Integer dstRevision) {
    this.dstRevision = dstRevision;
  }


  /**
   * @param aprjRevision the aprjRevision to set
   */
  public void setAprjRevision(final Integer aprjRevision) {
    this.aprjRevision = aprjRevision;
  }


  /**
   * @return the dstName
   */
  public String getDstName() {
    return this.dstName;
  }


  /**
   * @param dstName the dstName to set
   */
  public void setDstName(final String dstName) {
    this.dstName = dstName;
  }


  /**
   * @return the isCheckedIn
   */
  public boolean isCheckedIn() {
    return this.isCheckedIn;
  }


  /**
   * @param isCheckedIn the isCheckedIn to set
   */
  public void setCheckedIn(final boolean isCheckedIn) {
    this.isCheckedIn = isCheckedIn;
  }

  /**
   * @return the pidcA2lId
   */
  public Long getPidcA2lId() {
    return this.pidcA2lId;
  }

  /**
   * @param pidcA2lId the pidcA2lId to set
   */
  public void setPidcA2lId(final Long pidcA2lId) {
    this.pidcA2lId = pidcA2lId;
  }

  /**
   * @return the vcdmDstId
   */
  public Long getVcdmDstId() {
    return this.vcdmDstId;
  }

  /**
   * @param vcdmDstId the vcdmDstId to set
   */
  public void setVcdmDstId(final Long vcdmDstId) {
    this.vcdmDstId = vcdmDstId;
  }

  /**
   * @return the pidcElementId
   */
  public Long getPidcElementId() {
    return this.pidcElementId;
  }


  /**
   * @param pidcElementId the pidcElementId to set
   */
  public void setPidcElementId(final Long pidcElementId) {
    this.pidcElementId = pidcElementId;
  }
}
