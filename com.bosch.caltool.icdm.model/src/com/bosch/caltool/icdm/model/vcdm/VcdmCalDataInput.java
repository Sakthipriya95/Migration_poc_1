/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.vcdm;


/**
 * Input for VcdmCalDataService
 *
 * @author dja7cob
 */
public class VcdmCalDataInput {

  /**
   * A2L File Id
   */
  private long a2lFileId;

  /**
   * vCDM DST Id
   */
  private long vcdmDstId;

  /**
   * Current user's encypted pssword
   */
  private String encPwd;

  /**
   * @return the a2lFileId
   */
  public long getA2lFileId() {
    return this.a2lFileId;
  }

  /**
   * @param a2lFileId the a2lFileId to set
   */
  public void setA2lFileId(final long a2lFileId) {
    this.a2lFileId = a2lFileId;
  }


  /**
   * @return the vcdmDstId
   */
  public long getVcdmDstId() {
    return this.vcdmDstId;
  }


  /**
   * @param vcdmDstId the vcdmDstId to set
   */
  public void setVcdmDstId(final long vcdmDstId) {
    this.vcdmDstId = vcdmDstId;
  }


  /**
   * @return the encPwd
   */
  public String getEncPwd() {
    return this.encPwd;
  }


  /**
   * @param encPwd the encPwd to set
   */
  public void setEncPwd(final String encPwd) {
    this.encPwd = encPwd;
  }


}
