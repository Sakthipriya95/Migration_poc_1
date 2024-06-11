/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.bo.a2l;


/**
 * @author rgo7cob
 */
public class ParamWpResponsibility {

  private Long paramId;
  private Long respId;
  private Long wpId;
  private Long wpRespId;
  private String paramName;


  /**
   * @return the wpId
   */
  public Long getWpId() {
    return this.wpId;
  }


  /**
   * @param wpId the wpId to set
   */
  public void setWpId(final Long wpId) {
    this.wpId = wpId;
  }

  /**
   * @return the paramId
   */
  public Long getParamId() {
    return this.paramId;
  }

  /**
   * @param paramId the paramId to set
   */
  public void setParamId(final Long paramId) {
    this.paramId = paramId;
  }

  /**
   * @return the respId
   */
  public Long getRespId() {
    return this.respId;
  }

  /**
   * @param respId the respId to set
   */
  public void setRespId(final Long respId) {
    this.respId = respId;
  }


  /**
   * @return the wpRespId
   */
  public Long getWpRespId() {
    return this.wpRespId;
  }


  /**
   * @param wpRespId the wpRespId to set
   */
  public void setWpRespId(final Long wpRespId) {
    this.wpRespId = wpRespId;
  }


  /**
   * @return the paramName
   */
  public String getParamName() {
    return this.paramName;
  }


  /**
   * @param paramName the paramName to set
   */
  public void setParamName(final String paramName) {
    this.paramName = paramName;
  }


}
